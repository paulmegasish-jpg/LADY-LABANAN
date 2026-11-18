package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SeedData
import com.example.data.model.AppSettingsEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.ProductEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class AdminAnalytics(
    val totalRevenueInr: Int = 0,
    val totalOrders: Int = 0,
    val activeOrders: Int = 0,
    val avgOrderValueInr: Int = 0,
    val customerCount: Int = 0,
    val lowStockProducts: List<ProductEntity> = emptyList(),
    val categoryProductCounts: List<Pair<String, Int>> = emptyList(),
    val recentOrderAmounts: List<Pair<String, Int>> = emptyList()
)

class AdminViewModel(private val repo: StoreRepository) : ViewModel() {

    val products: StateFlow<List<ProductEntity>> = repo.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repo.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val banners: StateFlow<List<BannerEntity>> = repo.banners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<CouponEntity>> = repo.coupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repo.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repo.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<SupportTicketEntity>> = repo.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettingsEntity> = repo.settings
        .map { it ?: SeedData.defaultSettings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.defaultSettings)

    val analytics: StateFlow<AdminAnalytics> = combine(allOrders, products, allUsers) { orders, prods, users ->
        val validOrders = orders.filter {
            it.orderStatus != OrderStatus.CANCELLED.name && it.orderStatus != OrderStatus.RETURNED_REFUNDED.name
        }
        val revenue = validOrders.sumOf { it.totalInr }
        val avg = if (validOrders.isNotEmpty()) revenue / validOrders.size else 0
        val active = orders.count {
            it.orderStatus !in listOf(OrderStatus.DELIVERED.name, OrderStatus.CANCELLED.name, OrderStatus.RETURNED_REFUNDED.name)
        }
        val lowStock = prods.filter { it.stockCount <= 8 }
        val catCounts = prods.groupBy { it.category }.map { it.key to it.value.size }.sortedByDescending { it.second }
        val orderBars = orders.take(6).reversed().map { it.id.takeLast(4) to it.totalInr }
        AdminAnalytics(
            totalRevenueInr = revenue,
            totalOrders = orders.size,
            activeOrders = active,
            avgOrderValueInr = avg,
            customerCount = users.count { it.role == UserRole.CUSTOMER.name },
            lowStockProducts = lowStock,
            categoryProductCounts = catCounts,
            recentOrderAmounts = orderBars
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminAnalytics())

    private val _adminFeedback = MutableStateFlow<String?>(null)
    val adminFeedback: StateFlow<String?> = _adminFeedback.asStateFlow()

    fun clearFeedback() { _adminFeedback.value = null }

    fun saveProduct(
        existingId: String?,
        title: String,
        category: String,
        description: String,
        fabricCare: String,
        priceInr: Int,
        mrpInr: Int,
        sizesCsv: String,
        colorsCsv: String,
        stockCount: Int,
        imageKey: String,
        videoUri: String,
        videoTitle: String,
        isFeatured: Boolean,
        isTrending: Boolean,
        festivalTag: String
    ) {
        viewModelScope.launch {
            val prod = ProductEntity(
                id = existingId ?: "prod_${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                category = category.trim(),
                description = description.trim(),
                fabricCare = fabricCare.trim().ifBlank { "Luxury Atelier Craft • Dry Clean Recommended" },
                priceInr = priceInr.coerceAtLeast(1),
                mrpInr = mrpInr.coerceAtLeast(priceInr),
                sizesCsv = sizesCsv.ifBlank { "XS,S,M,L,XL,XXL" },
                colorsCsv = colorsCsv.ifBlank { "Blush Rose:#E09BAE,Champagne Gold:#C9A461" },
                stockCount = stockCount.coerceAtLeast(0),
                imageKey = imageKey.ifBlank { "img_hero_bridal_saree" },
                videoHighlightTitle = videoTitle.ifBlank { "Runway Drape Preview" },
                videoUri = videoUri,
                isFeatured = isFeatured,
                isTrending = isTrending,
                festivalTag = festivalTag.trim()
            )
            repo.adminUpsertProduct(prod)
            _adminFeedback.value = if (existingId == null) "Product added to storefront" else "Product updated"
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repo.adminDeleteProduct(id)
            _adminFeedback.value = "Product deleted"
        }
    }

    fun updateOrderStatus(order: OrderEntity, newStatus: OrderStatus, courier: String, awb: String) {
        viewModelScope.launch {
            val newRefund = if (newStatus == OrderStatus.RETURNED_REFUNDED || newStatus == OrderStatus.CANCELLED) "Refunded" else order.refundStatus
            val newPay = if (newRefund == "Refunded" && order.paymentStatus == "Paid") "Refunded" else order.paymentStatus
            repo.adminUpdateOrder(
                order.copy(
                    orderStatus = newStatus.name,
                    courierName = courier.ifBlank { order.courierName },
                    trackingNumber = awb.ifBlank { order.trackingNumber },
                    refundStatus = newRefund,
                    paymentStatus = newPay
                )
            )
            _adminFeedback.value = "Order ${order.id} updated to ${newStatus.displayLabel}"
        }
    }

    fun saveCoupon(code: String, title: String, festivalOffer: String, discountPercent: Int, maxDiscountInr: Int, minOrderInr: Int, isActive: Boolean) {
        if (code.isBlank()) return
        viewModelScope.launch {
            repo.adminUpsertCoupon(
                CouponEntity(
                    code = code.trim().uppercase(),
                    title = title.trim(),
                    festivalOffer = festivalOffer.trim(),
                    discountPercent = discountPercent.coerceIn(1, 90),
                    maxDiscountInr = maxDiscountInr.coerceAtLeast(100),
                    minOrderInr = minOrderInr.coerceAtLeast(0),
                    isActive = isActive
                )
            )
            _adminFeedback.value = "Coupon ${code.uppercase()} saved"
        }
    }

    fun deleteCoupon(code: String) {
        viewModelScope.launch {
            repo.adminDeleteCoupon(code)
            _adminFeedback.value = "Coupon removed"
        }
    }

    fun saveBanner(existingId: String?, title: String, subtitle: String, ctaText: String, targetCategory: String, imageKey: String, badge: String) {
        viewModelScope.launch {
            repo.adminUpsertBanner(
                BannerEntity(
                    id = existingId ?: "banner_${UUID.randomUUID().toString().take(8)}",
                    title = title.trim(),
                    subtitle = subtitle.trim(),
                    ctaText = ctaText.trim().ifBlank { "Shop Now" },
                    targetCategory = targetCategory,
                    imageKey = imageKey.ifBlank { "img_hero_bridal_saree" },
                    festivalBadge = badge.trim(),
                    isActive = true
                )
            )
            _adminFeedback.value = "Homepage banner saved"
        }
    }

    fun deleteBanner(id: String) {
        viewModelScope.launch {
            repo.adminDeleteBanner(id)
            _adminFeedback.value = "Banner deleted"
        }
    }

    fun saveCategory(existingId: String?, name: String, subtitle: String, imageKey: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repo.adminUpsertCategory(
                CategoryEntity(
                    id = existingId ?: "cat_${UUID.randomUUID().toString().take(8)}",
                    name = name.trim(),
                    subtitle = subtitle.trim().ifBlank { "Curated Women's Edit" },
                    imageKey = imageKey.ifBlank { "img_hero_festive_kurti" },
                    displayOrder = categories.value.size + 1,
                    isActive = true
                )
            )
            _adminFeedback.value = "Category $name saved"
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repo.adminDeleteCategory(id)
            _adminFeedback.value = "Category removed"
        }
    }

    fun replyTicket(ticket: SupportTicketEntity, reply: String, status: String) {
        viewModelScope.launch {
            repo.adminReplySupportTicket(ticket, reply, status)
            _adminFeedback.value = "Reply sent for ${ticket.id}"
        }
    }

    fun sendPushBroadcast(title: String, body: String) {
        if (title.isBlank() || body.isBlank()) return
        viewModelScope.launch {
            repo.adminBroadcastNotification(title, body)
            _adminFeedback.value = "Push notification broadcasted to all Lady Lebanan customers"
        }
    }

    fun updateSettings(newSettings: AppSettingsEntity) {
        viewModelScope.launch {
            repo.adminUpdateSettings(newSettings)
            _adminFeedback.value = "Store & delivery settings saved"
        }
    }

    class Factory(private val repo: StoreRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminViewModel(repo) as T
    }
}
