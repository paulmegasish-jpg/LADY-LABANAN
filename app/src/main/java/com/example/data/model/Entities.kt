package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,
    ADMIN
}

enum class OrderStatus(val displayLabel: String, val stepIndex: Int) {
    PLACED("Order Placed", 0),
    CONFIRMED("Confirmed", 1),
    PACKED("Packed at Atelier", 2),
    SHIPPED("Shipped", 3),
    OUT_FOR_DELIVERY("Out for Delivery", 4),
    DELIVERED("Delivered", 5),
    CANCELLED("Cancelled", -1),
    RETURN_REQUESTED("Return Requested", 6),
    RETURNED_REFUNDED("Returned & Refunded", 7)
}

enum class PaymentMethod(val label: String, val subtitle: String) {
    UPI("UPI (Any App)", "Pay via VPA e.g. name@okicici"),
    GOOGLE_PAY("Google Pay", "Instant UPI via Google Pay"),
    PHONE_PE("PhonePe", "One-tap PhonePe UPI gateway"),
    CARD("Debit / Credit Card", "Visa, Mastercard, RuPay"),
    NET_BANKING("Net Banking", "HDFC, ICICI, SBI, Axis, Kotak"),
    COD("Cash on Delivery", "Pay upon doorstep delivery across India")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String = UserRole.CUSTOMER.name,
    val isOtpVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val subtitle: String,
    val imageKey: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val description: String,
    val fabricCare: String,
    val priceInr: Int,
    val mrpInr: Int,
    val sizesCsv: String, // e.g., "XS,S,M,L,XL,XXL"
    val colorsCsv: String, // e.g., "Blush Rose:#E09BAE,Champagne Gold:#C9A461,Obsidian:#121013"
    val stockCount: Int,
    val imageKey: String, // drawable key or content:// URI
    val extraImagesCsv: String = "", // comma-separated drawable keys / URIs
    val videoHighlightTitle: String = "Runway Drape Preview",
    val videoUri: String = "", // picked video content:// URI or empty for curated lookbook preview
    val rating: Double = 4.8,
    val reviewCount: Int = 0,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val festivalTag: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val targetCategory: String,
    val imageKey: String,
    val festivalBadge: String = "",
    val isActive: Boolean = true
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val productId: String,
    val selectedSize: String,
    val selectedColor: String,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val compositeId: String, // "$userId:$productId"
    val userId: String,
    val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val fullName: String,
    val phone: String,
    val line1: String,
    val line2: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val label: String = "Home",
    val isDefault: Boolean = false
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val customerName: String,
    val customerPhone: String,
    val itemsSummary: String,
    val itemCount: Int,
    val primaryImageKey: String,
    val subtotalInr: Int,
    val discountInr: Int,
    val shippingFeeInr: Int,
    val totalInr: Int,
    val couponCode: String = "",
    val paymentMethod: String,
    val paymentStatus: String, // "Paid", "Pending COD", "Refunded"
    val orderStatus: String, // OrderStatus.name
    val courierName: String = "Delhivery Luxury Express",
    val trackingNumber: String = "",
    val deliveryAddress: String,
    val pinCode: String,
    val deliverySpeed: String = "Standard",
    val cancellationReason: String = "",
    val returnReason: String = "",
    val refundStatus: String = "None", // "None", "Initiated", "Refunded"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val title: String,
    val festivalOffer: String,
    val discountPercent: Int,
    val maxDiscountInr: Int,
    val minOrderInr: Int,
    val isActive: Boolean = true
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val verifiedPurchase: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val subject: String,
    val message: String,
    val status: String = "Open", // "Open", "In Progress", "Resolved"
    val adminReply: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val targetUserId: String, // "ALL" for broadcast or specific userId
    val title: String,
    val body: String,
    val category: String = "Offer", // "Offer", "Order", "Alert"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val freeShippingThresholdInr: Int = 2999,
    val standardShippingFeeInr: Int = 149,
    val expressShippingFeeInr: Int = 299,
    val codFeeInr: Int = 79,
    val maintenanceMode: Boolean = false,
    val aboutUsContent: String,
    val contactEmail: String,
    val contactPhone: String,
    val contactAddress: String,
    val privacyPolicy: String,
    val termsConditions: String,
    val returnRefundPolicy: String,
    val shippingPolicy: String
)

data class ColorOption(
    val name: String,
    val hex: Long
)

fun parseColorOptions(colorsCsv: String): List<ColorOption> {
    return colorsCsv.split(",")
        .mapNotNull { entry ->
            val parts = entry.trim().split(":")
            if (parts.size == 2) {
                val hexClean = parts[1].trim().removePrefix("#")
                val hexLong = hexClean.toLongOrNull(16)?.let {
                    if (hexClean.length == 6) 0xFF000000L or it else it
                } ?: 0xFFE09BAEL
                ColorOption(parts[0].trim(), hexLong)
            } else if (entry.isNotBlank()) {
                ColorOption(entry.trim(), 0xFFE09BAEL)
            } else null
        }
        .ifEmpty { listOf(ColorOption("Blush Rose", 0xFFE09BAEL)) }
}

fun parseSizes(sizesCsv: String): List<String> =
    sizesCsv.split(",").map { it.trim() }.filter { it.isNotBlank() }
        .ifEmpty { listOf("XS", "S", "M", "L", "XL", "XXL") }
