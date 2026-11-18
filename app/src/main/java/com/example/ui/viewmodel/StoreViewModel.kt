package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SeedData
import com.example.data.model.AddressEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.StoreRepository
import com.example.data.service.PinCodeService
import com.example.data.service.PinServiceabilityResult
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.i18n.LocalizationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class SortOption(val label: String) {
    FEATURED("Featured"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low"),
    TOP_RATED("Top Rated"),
    NEWEST("New Arrivals")
}

data class CatalogFilterState(
    val query: String = "",
    val selectedCategory: String = "All",
    val selectedSize: String = "All",
    val maxPriceInr: Int = 20000,
    val sortBy: SortOption = SortOption.FEATURED
)

data class CartLineItem(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModel(private val repo: StoreRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repo.ensureSeeded()
        }
    }

    val language: StateFlow<AppLanguage> = repo.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val strings: StateFlow<AppStrings> = language
        .map { LocalizationProvider.forLanguage(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalizationProvider.forLanguage(AppLanguage.ENGLISH))

    val currentUserId: StateFlow<String?> = repo.currentUserId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isGuestMode: StateFlow<Boolean> = repo.isGuestMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentUser: StateFlow<UserEntity?> = currentUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUser(uid) else flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isAdmin: StateFlow<Boolean> = currentUser
        .map { it?.role == UserRole.ADMIN.name }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val categories: StateFlow<List<CategoryEntity>> = repo.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<ProductEntity>> = repo.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val banners: StateFlow<List<BannerEntity>> = repo.banners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<CouponEntity>> = repo.coupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettingsEntity> = repo.settings
        .map { it ?: SeedData.defaultSettings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.defaultSettings)

    val cartLines: StateFlow<List<CartLineItem>> = combine(
        currentUserId.flatMapLatest { uid -> if (uid != null) repo.observeCart(uid) else flowOf(emptyList()) },
        products
    ) { cartList, prodList ->
        val prodMap = prodList.associateBy { it.id }
        cartList.mapNotNull { c -> prodMap[c.productId]?.let { CartLineItem(c, it) } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistProductIds: StateFlow<Set<String>> = currentUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeWishlist(uid) else flowOf(emptyList()) }
        .map { list -> list.map { it.productId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val addresses: StateFlow<List<AddressEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeAddresses(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userOrders: StateFlow<List<OrderEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserOrders(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTickets: StateFlow<List<SupportTicketEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserTickets(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = currentUserId
        .flatMapLatest { uid -> repo.observeNotifications(uid ?: "GUEST") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filterState = MutableStateFlow(CatalogFilterState())
    val filterState: StateFlow<CatalogFilterState> = _filterState.asStateFlow()

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(products, _filterState) { list, filter ->
        list.filter { p ->
            val matchQuery = filter.query.isBlank() ||
                p.title.contains(filter.query, ignoreCase = true) ||
                p.category.contains(filter.query, ignoreCase = true) ||
                p.description.contains(filter.query, ignoreCase = true)
            val matchCat = filter.selectedCategory == "All" || p.category.equals(filter.selectedCategory, ignoreCase = true)
            val matchSize = filter.selectedSize == "All" || p.sizesCsv.split(",").any { it.trim() == filter.selectedSize }
            val matchPrice = p.priceInr <= filter.maxPriceInr
            matchQuery && matchCat && matchSize && matchPrice
        }.let { filtered ->
            when (filter.sortBy) {
                SortOption.FEATURED -> filtered.sortedByDescending { it.isFeatured }
                SortOption.PRICE_LOW -> filtered.sortedBy { it.priceInr }
                SortOption.PRICE_HIGH -> filtered.sortedByDescending { it.priceInr }
                SortOption.TOP_RATED -> filtered.sortedByDescending { it.rating }
                SortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _pinResult = MutableStateFlow<PinServiceabilityResult?>(null)
    val pinResult: StateFlow<PinServiceabilityResult?> = _pinResult.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _generatedOtp = MutableStateFlow<String?>(null)
    val generatedOtp: StateFlow<String?> = _generatedOtp.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    fun clearStatusBanner() { _statusBanner.value = null }
    fun clearAuthError() { _authError.value = null }
    fun clearLastPlacedOrder() { _lastPlacedOrder.value = null }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch { repo.setLanguage(lang) }
    }

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun selectCategoryFilter(category: String) {
        _filterState.value = _filterState.value.copy(selectedCategory = category)
    }

    fun updateFilters(size: String, maxPrice: Int, sortBy: SortOption) {
        _filterState.value = _filterState.value.copy(selectedSize = size, maxPriceInr = maxPrice, sortBy = sortBy)
    }

    fun resetFilters() {
        _filterState.value = CatalogFilterState()
    }

    fun checkPinCode(pin: String) {
        _pinResult.value = PinCodeService.checkPinCode(pin)
    }

    fun observeProductReviews(productId: String) = repo.observeReviewsForProduct(productId)

    // --- AUTH & OTP ---
    fun continueAsGuest() {
        viewModelScope.launch {
            _authError.value = null
            repo.enterGuestMode()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _appliedCoupon.value = null
            repo.signOut()
        }
    }

    fun loginCustomer(email: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            repo.loginCustomerWithEmail(email, password).onFailure {
                _authError.value = it.message
            }
        }
    }

    fun registerCustomer(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            repo.registerCustomer(name, email, phone, password).onFailure {
                _authError.value = it.message
            }
        }
    }

    fun sendMobileOtp(phone: String) {
        val digits = phone.filter { it.isDigit() }.takeLast(10)
        if (digits.length != 10) {
            _authError.value = "Please enter a valid 10-digit Indian mobile number."
            return
        }
        _authError.value = null
        val otp = (100000..999999).random().toString()
        _generatedOtp.value = otp
        _statusBanner.value = "SMS OTP sent to +91 $digits: $otp"
    }

    fun verifyMobileOtp(phone: String, enteredOtp: String, nameIfNew: String) {
        viewModelScope.launch {
            if (_generatedOtp.value == null || enteredOtp.trim() != _generatedOtp.value) {
                _authError.value = "Invalid OTP. Please check the 6-digit code sent to your device."
                return@launch
            }
            _authError.value = null
            repo.loginOrRegisterViaMobileOtp(phone, nameIfNew).onSuccess {
                _generatedOtp.value = null
            }.onFailure {
                _authError.value = it.message
            }
        }
    }

    fun loginAdmin(email: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            repo.loginAdmin(email, password).onFailure {
                _authError.value = it.message
            }
        }
    }

    // --- SHOPPING ACTIONS ---
    fun addToCart(product: ProductEntity, size: String, color: String, onRequireAuth: () -> Unit) {
        val uid = currentUserId.value
        if (uid == null) {
            onRequireAuth()
            return
        }
        if (product.stockCount <= 0) {
            _statusBanner.value = "Sorry, ${product.title} is currently out of stock."
            return
        }
        viewModelScope.launch {
            repo.addToCart(uid, product.id, size, color)
            _statusBanner.value = "Added ${product.title} ($size) to your bag"
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQty: Int) {
        viewModelScope.launch { repo.updateCartQuantity(cartItemId, newQty) }
    }

    fun toggleWishlist(productId: String, onRequireAuth: () -> Unit) {
        val uid = currentUserId.value
        if (uid == null) {
            onRequireAuth()
            return
        }
        val currentlySaved = wishlistProductIds.value.contains(productId)
        viewModelScope.launch {
            repo.toggleWishlist(uid, productId, currentlySaved)
            _statusBanner.value = if (currentlySaved) "Removed from wishlist" else "Saved to your wishlist"
        }
    }

    fun applyCoupon(code: String, subtotalInr: Int) {
        viewModelScope.launch {
            repo.validateCoupon(code, subtotalInr).onSuccess {
                _appliedCoupon.value = it
                _statusBanner.value = "Coupon ${it.code} applied (${it.discountPercent}% off)"
            }.onFailure {
                _statusBanner.value = it.message
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun computeDiscountInr(subtotalInr: Int, coupon: CouponEntity?): Int {
        if (coupon == null || subtotalInr < coupon.minOrderInr) return 0
        return ((subtotalInr * coupon.discountPercent) / 100).coerceAtMost(coupon.maxDiscountInr)
    }

    fun saveAddress(
        fullName: String,
        phone: String,
        line1: String,
        line2: String,
        city: String,
        state: String,
        pinCode: String,
        label: String,
        isDefault: Boolean
    ) {
        val uid = currentUserId.value ?: return
        viewModelScope.launch {
            repo.saveAddress(
                AddressEntity(
                    id = "addr_${UUID.randomUUID().toString().take(8)}",
                    userId = uid,
                    fullName = fullName.trim(),
                    phone = phone.filter { it.isDigit() }.takeLast(10),
                    line1 = line1.trim(),
                    line2 = line2.trim(),
                    city = city.trim(),
                    state = state.trim(),
                    pinCode = pinCode.trim(),
                    label = label,
                    isDefault = isDefault
                )
            )
            _statusBanner.value = "Delivery address saved"
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch { repo.deleteAddress(addressId) }
    }

    fun placeOrder(
        address: AddressEntity,
        paymentMethodLabel: String,
        isCod: Boolean,
        deliverySpeed: String,
        onComplete: (OrderEntity) -> Unit
    ) {
        val user = currentUser.value ?: return
        val lines = cartLines.value
        if (lines.isEmpty()) return

        viewModelScope.launch {
            val s = settings.value
            val subtotal = lines.sumOf { it.product.priceInr * it.cartItem.quantity }
            val coupon = _appliedCoupon.value
            val discount = computeDiscountInr(subtotal, coupon)
            val baseShipping = when {
                deliverySpeed == "Express" -> s.expressShippingFeeInr
                subtotal >= s.freeShippingThresholdInr -> 0
                else -> s.standardShippingFeeInr
            }
            val codExtra = if (isCod) s.codFeeInr else 0
            val order = repo.placeOrder(
                user = user,
                cartLines = lines.map { it.cartItem to it.product },
                address = address,
                paymentMethodLabel = paymentMethodLabel,
                isCod = isCod,
                deliverySpeed = deliverySpeed,
                couponCode = coupon?.code ?: "",
                subtotalInr = subtotal,
                discountInr = discount,
                shippingFeeInr = baseShipping + codExtra
            )
            _appliedCoupon.value = null
            _lastPlacedOrder.value = order
            onComplete(order)
        }
    }

    fun requestOrderCancellation(order: OrderEntity, reason: String) {
        viewModelScope.launch {
            repo.requestCancellation(order, reason)
            _statusBanner.value = "Order ${order.id} cancelled"
        }
    }

    fun requestOrderReturn(order: OrderEntity, reason: String) {
        viewModelScope.launch {
            repo.requestReturn(order, reason)
            _statusBanner.value = "Return request submitted for ${order.id}"
        }
    }

    fun submitReview(productId: String, rating: Int, comment: String) {
        val user = currentUser.value ?: return
        if (comment.isBlank()) return
        viewModelScope.launch {
            repo.submitReview(productId, user, rating, comment)
            _statusBanner.value = "Thank you for your verified review!"
        }
    }

    fun submitSupportTicket(subject: String, message: String) {
        val user = currentUser.value ?: return
        if (subject.isBlank() || message.isBlank()) return
        viewModelScope.launch {
            repo.createSupportTicket(user, subject, message)
            _statusBanner.value = "Support ticket submitted. Our concierge will respond shortly."
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch { repo.markNotificationRead(id) }
    }

    class Factory(private val repo: StoreRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = StoreViewModel(repo) as T
    }
}
