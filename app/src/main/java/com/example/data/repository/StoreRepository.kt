package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.SeedData
import com.example.data.local.SessionPreferences
import com.example.data.model.AddressEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.WishlistItemEntity
import com.example.data.service.NotificationHelper
import com.example.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class StoreRepository(
    private val appContext: Context,
    private val db: AppDatabase,
    private val prefs: SessionPreferences
) {
    private val userDao = db.userDao()
    private val catalogDao = db.catalogDao()
    private val shoppingDao = db.shoppingDao()
    private val engagementDao = db.engagementDao()

    val currentUserId: Flow<String?> = prefs.currentUserId
    val isGuestMode: Flow<Boolean> = prefs.isGuestMode
    val language: Flow<AppLanguage> = prefs.language

    val categories: Flow<List<CategoryEntity>> = catalogDao.observeCategories()
    val products: Flow<List<ProductEntity>> = catalogDao.observeProducts()
    val banners: Flow<List<BannerEntity>> = catalogDao.observeBanners()
    val coupons: Flow<List<CouponEntity>> = engagementDao.observeCoupons()
    val allOrders: Flow<List<OrderEntity>> = engagementDao.observeAllOrders()
    val allUsers: Flow<List<UserEntity>> = userDao.observeAllUsers()
    val allReviews: Flow<List<ReviewEntity>> = engagementDao.observeAllReviews()
    val allTickets: Flow<List<SupportTicketEntity>> = engagementDao.observeAllSupportTickets()
    val settings: Flow<AppSettingsEntity?> = engagementDao.observeSettings()

    suspend fun ensureSeeded() {
        AppDatabase.seedIfEmpty(db)
    }

    fun observeUser(userId: String): Flow<UserEntity?> = userDao.observeUserById(userId)
    fun observeCart(userId: String): Flow<List<CartItemEntity>> = shoppingDao.observeCart(userId)
    fun observeWishlist(userId: String): Flow<List<WishlistItemEntity>> = shoppingDao.observeWishlist(userId)
    fun observeAddresses(userId: String): Flow<List<AddressEntity>> = shoppingDao.observeAddresses(userId)
    fun observeUserOrders(userId: String): Flow<List<OrderEntity>> = engagementDao.observeUserOrders(userId)
    fun observeUserTickets(userId: String): Flow<List<SupportTicketEntity>> = engagementDao.observeUserSupportTickets(userId)
    fun observeNotifications(userId: String): Flow<List<NotificationEntity>> = engagementDao.observeNotificationsForUser(userId)
    fun observeReviewsForProduct(productId: String): Flow<List<ReviewEntity>> = engagementDao.observeReviewsForProduct(productId)

    suspend fun setLanguage(language: AppLanguage) {
        prefs.setLanguage(language)
    }

    suspend fun enterGuestMode() {
        prefs.setGuestMode(true)
    }

    suspend fun signOut() {
        prefs.signOut()
    }

    suspend fun loginCustomerWithEmail(email: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email.trim())
            ?: return Result.failure(IllegalArgumentException("No account found for this email."))
        if (user.role == UserRole.ADMIN.name) {
            return Result.failure(IllegalArgumentException("Please use the Owner Admin Portal to sign in as Admin."))
        }
        if (user.passwordHash != password) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }
        prefs.setSignedInUser(user.id)
        return Result.success(user)
    }

    suspend fun registerCustomer(name: String, email: String, phone: String, password: String): Result<UserEntity> {
        val cleanPhone = phone.filter { it.isDigit() }.takeLast(10)
        if (name.isBlank() || email.isBlank() || cleanPhone.length != 10 || password.length < 6) {
            return Result.failure(IllegalArgumentException("Enter valid name, email, 10-digit Indian mobile, and 6+ char password."))
        }
        if (userDao.getUserByEmail(email.trim()) != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists."))
        }
        if (userDao.getUserByPhone(cleanPhone) != null) {
            return Result.failure(IllegalArgumentException("This mobile number is already registered."))
        }
        val newUser = UserEntity(
            id = "cust_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            email = email.trim(),
            phone = cleanPhone,
            passwordHash = password,
            role = UserRole.CUSTOMER.name,
            isOtpVerified = true
        )
        userDao.upsertUser(newUser)
        prefs.setSignedInUser(newUser.id)
        return Result.success(newUser)
    }

    suspend fun loginOrRegisterViaMobileOtp(phone: String, nameIfNew: String): Result<UserEntity> {
        val cleanPhone = phone.filter { it.isDigit() }.takeLast(10)
        if (cleanPhone.length != 10) {
            return Result.failure(IllegalArgumentException("Enter a valid 10-digit Indian mobile number."))
        }
        val existing = userDao.getUserByPhone(cleanPhone)
        val user = if (existing != null) {
            if (existing.role == UserRole.ADMIN.name) {
                return Result.failure(IllegalArgumentException("Owner account requires Owner Admin Portal credentials."))
            }
            existing.copy(isOtpVerified = true).also { userDao.upsertUser(it) }
        } else {
            UserEntity(
                id = "cust_${UUID.randomUUID().toString().take(8)}",
                name = nameIfNew.trim().ifBlank { "Lady Lebanan Patron" },
                email = "patron_${cleanPhone.takeLast(4)}@ladylebanan.in",
                phone = cleanPhone,
                passwordHash = "OTP_VERIFIED",
                role = UserRole.CUSTOMER.name,
                isOtpVerified = true
            ).also { userDao.upsertUser(it) }
        }
        prefs.setSignedInUser(user.id)
        return Result.success(user)
    }

    suspend fun loginAdmin(email: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email.trim())
            ?: return Result.failure(IllegalArgumentException("Unauthorized admin account."))
        if (user.role != UserRole.ADMIN.name) {
            return Result.failure(IllegalArgumentException("Access denied: This account does not have Owner Admin privileges."))
        }
        if (user.passwordHash != password) {
            return Result.failure(IllegalArgumentException("Invalid owner credentials."))
        }
        prefs.setSignedInUser(user.id)
        return Result.success(user)
    }

    suspend fun addToCart(userId: String, productId: String, size: String, color: String) {
        val existing = shoppingDao.findCartItem(userId, productId, size, color)
        if (existing != null) {
            shoppingDao.updateCartQuantity(existing.id, existing.quantity + 1)
        } else {
            shoppingDao.upsertCartItem(
                CartItemEntity(
                    userId = userId,
                    productId = productId,
                    selectedSize = size,
                    selectedColor = color,
                    quantity = 1
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        if (quantity <= 0) {
            shoppingDao.removeCartItem(cartItemId)
        } else {
            shoppingDao.updateCartQuantity(cartItemId, quantity)
        }
    }

    suspend fun toggleWishlist(userId: String, productId: String, currentlyWishlisted: Boolean) {
        val key = "$userId:$productId"
        if (currentlyWishlisted) {
            shoppingDao.removeFromWishlist(key)
        } else {
            shoppingDao.addToWishlist(WishlistItemEntity(compositeId = key, userId = userId, productId = productId))
        }
    }

    suspend fun saveAddress(address: AddressEntity) {
        if (address.isDefault) {
            shoppingDao.clearDefaultAddress(address.userId)
        }
        shoppingDao.upsertAddress(address)
    }

    suspend fun deleteAddress(addressId: String) {
        shoppingDao.deleteAddress(addressId)
    }

    suspend fun validateCoupon(code: String, subtotalInr: Int): Result<CouponEntity> {
        val coupon = engagementDao.getCouponByCode(code.trim())
            ?: return Result.failure(IllegalArgumentException("Invalid coupon code."))
        if (!coupon.isActive) {
            return Result.failure(IllegalArgumentException("This coupon has expired."))
        }
        if (subtotalInr < coupon.minOrderInr) {
            return Result.failure(IllegalArgumentException("Minimum order ₹${coupon.minOrderInr} required for ${coupon.code}."))
        }
        return Result.success(coupon)
    }

    suspend fun placeOrder(
        user: UserEntity,
        cartLines: List<Pair<CartItemEntity, ProductEntity>>,
        address: AddressEntity,
        paymentMethodLabel: String,
        isCod: Boolean,
        deliverySpeed: String,
        couponCode: String,
        subtotalInr: Int,
        discountInr: Int,
        shippingFeeInr: Int
    ): OrderEntity {
        val orderId = "LL-2026-${(1000..9999).random()}"
        val summary = cartLines.joinToString(" • ") { (cart, prod) ->
            "${prod.title} (${cart.selectedSize}, ${cart.selectedColor}) × ${cart.quantity}"
        }
        val total = (subtotalInr - discountInr + shippingFeeInr).coerceAtLeast(0)
        val awb = "BD${(10000000..99999999).random()}IN"
        val order = OrderEntity(
            id = orderId,
            userId = user.id,
            customerName = address.fullName,
            customerPhone = address.phone,
            itemsSummary = summary,
            itemCount = cartLines.sumOf { it.first.quantity },
            primaryImageKey = cartLines.firstOrNull()?.second?.imageKey ?: "img_hero_bridal_saree",
            subtotalInr = subtotalInr,
            discountInr = discountInr,
            shippingFeeInr = shippingFeeInr,
            totalInr = total,
            couponCode = couponCode,
            paymentMethod = paymentMethodLabel,
            paymentStatus = if (isCod) "Pending COD" else "Paid",
            orderStatus = OrderStatus.CONFIRMED.name,
            courierName = if (deliverySpeed == "Express") "BlueDart Luxury Air" else "Delhivery Pan-India Express",
            trackingNumber = awb,
            deliveryAddress = "${address.line1}, ${if (address.line2.isNotBlank()) "${address.line2}, " else ""}${address.city}, ${address.state} - ${address.pinCode}",
            pinCode = address.pinCode,
            deliverySpeed = deliverySpeed
        )
        engagementDao.upsertOrder(order)
        cartLines.forEach { (cart, prod) ->
            catalogDao.decrementStock(prod.id, cart.quantity)
        }
        shoppingDao.clearCart(user.id)

        val notif = NotificationEntity(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            targetUserId = user.id,
            title = "Order $orderId Confirmed",
            body = "Thank you for shopping with Lady Lebanan! Tracking AWB: $awb will ship to PIN ${address.pinCode}.",
            category = "Order"
        )
        engagementDao.upsertNotification(notif)
        NotificationHelper.showNotification(appContext, notif.title, notif.body)
        return order
    }

    suspend fun requestCancellation(order: OrderEntity, reason: String) {
        val updated = order.copy(
            orderStatus = OrderStatus.CANCELLED.name,
            cancellationReason = reason,
            refundStatus = if (order.paymentStatus == "Paid") "Initiated" else "None",
            updatedAt = System.currentTimeMillis()
        )
        engagementDao.updateOrder(updated)
    }

    suspend fun requestReturn(order: OrderEntity, reason: String) {
        val updated = order.copy(
            orderStatus = OrderStatus.RETURN_REQUESTED.name,
            returnReason = reason,
            refundStatus = "Initiated",
            updatedAt = System.currentTimeMillis()
        )
        engagementDao.updateOrder(updated)
    }

    suspend fun submitReview(productId: String, user: UserEntity, rating: Int, comment: String) {
        val review = ReviewEntity(
            id = "rev_${UUID.randomUUID().toString().take(8)}",
            productId = productId,
            userId = user.id,
            userName = user.name,
            rating = rating.coerceIn(1, 5),
            comment = comment.trim(),
            verifiedPurchase = true
        )
        engagementDao.upsertReview(review)
        val product = catalogDao.getProductById(productId) ?: return
        val allForProduct = engagementDao.observeReviewsForProduct(productId).first()
        val avg = if (allForProduct.isNotEmpty()) allForProduct.map { it.rating }.average() else rating.toDouble()
        catalogDao.upsertProduct(
            product.copy(
                rating = String.format("%.1f", avg).toDouble(),
                reviewCount = allForProduct.size
            )
        )
    }

    suspend fun createSupportTicket(user: UserEntity, subject: String, message: String) {
        val ticket = SupportTicketEntity(
            id = "TKT-${(1000..9999).random()}",
            userId = user.id,
            userName = user.name,
            userEmail = user.email,
            subject = subject.trim(),
            message = message.trim()
        )
        engagementDao.upsertSupportTicket(ticket)
    }

    suspend fun markNotificationRead(id: String) {
        engagementDao.markNotificationRead(id)
    }

    // --- ADMIN OPERATIONS (Guarded in AdminViewModel & UI) ---

    suspend fun adminUpsertProduct(product: ProductEntity) = catalogDao.upsertProduct(product)
    suspend fun adminDeleteProduct(id: String) = catalogDao.deleteProduct(id)

    suspend fun adminUpsertCategory(category: CategoryEntity) = catalogDao.upsertCategory(category)
    suspend fun adminDeleteCategory(id: String) = catalogDao.deleteCategory(id)

    suspend fun adminUpsertBanner(banner: BannerEntity) = catalogDao.upsertBanner(banner)
    suspend fun adminDeleteBanner(id: String) = catalogDao.deleteBanner(id)

    suspend fun adminUpsertCoupon(coupon: CouponEntity) = engagementDao.upsertCoupon(coupon)
    suspend fun adminDeleteCoupon(code: String) = engagementDao.deleteCoupon(code)

    suspend fun adminUpdateOrder(order: OrderEntity, notifyCustomer: Boolean = true) {
        engagementDao.updateOrder(order.copy(updatedAt = System.currentTimeMillis()))
        if (notifyCustomer) {
            val statusLabel = runCatching { OrderStatus.valueOf(order.orderStatus).displayLabel }.getOrDefault(order.orderStatus)
            val notif = NotificationEntity(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                targetUserId = order.userId,
                title = "Order ${order.id}: $statusLabel",
                body = "Status updated to $statusLabel. Courier: ${order.courierName} (${order.trackingNumber}).",
                category = "Order"
            )
            engagementDao.upsertNotification(notif)
            NotificationHelper.showNotification(appContext, notif.title, notif.body)
        }
    }

    suspend fun adminReplySupportTicket(ticket: SupportTicketEntity, reply: String, status: String) {
        engagementDao.upsertSupportTicket(ticket.copy(adminReply = reply.trim(), status = status))
    }

    suspend fun adminBroadcastNotification(title: String, body: String) {
        val notif = NotificationEntity(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            targetUserId = "ALL",
            title = title.trim(),
            body = body.trim(),
            category = "Offer"
        )
        engagementDao.upsertNotification(notif)
        NotificationHelper.showNotification(appContext, notif.title, notif.body)
    }

    suspend fun adminUpdateSettings(settings: AppSettingsEntity) {
        engagementDao.upsertSettings(settings)
    }

    suspend fun getSettingsOnce(): AppSettingsEntity {
        return engagementDao.getSettingsOnce() ?: SeedData.defaultSettings
    }
}
