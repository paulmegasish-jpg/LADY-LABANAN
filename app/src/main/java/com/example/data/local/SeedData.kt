package com.example.data.local

import com.example.data.model.AddressEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole

object SeedData {
    const val OWNER_EMAIL = "owner@ladylebanan.in"
    const val OWNER_PASS = "Lebanan@Admin1"
    const val OWNER_UID = "admin_owner_01"
    const val DEMO_CUSTOMER_UID = "cust_ananya_01"

    val defaultUsers = listOf(
        UserEntity(
            id = OWNER_UID,
            name = "Aarohi Sen (Founder & Owner)",
            email = OWNER_EMAIL,
            phone = "9830011223",
            passwordHash = OWNER_PASS,
            role = UserRole.ADMIN.name,
            isOtpVerified = true
        ),
        UserEntity(
            id = DEMO_CUSTOMER_UID,
            name = "Ananya Chatterjee",
            email = "ananya.c@example.in",
            phone = "9876543210",
            passwordHash = "Ananya@123",
            role = UserRole.CUSTOMER.name,
            isOtpVerified = true
        )
    )

    val defaultCategories = listOf(
        CategoryEntity("cat_sarees", "Sarees", "Banarasi & Kanjivaram Silk", "img_hero_bridal_saree", 1),
        CategoryEntity("cat_kurtis", "Kurtis", "Embroidered Festive Sets", "img_hero_festive_kurti", 2),
        CategoryEntity("cat_dresses", "Dresses", "Evening Satin & Midi Silhouettes", "img_product_dress_western", 3),
        CategoryEntity("cat_tops", "Tops", "Silk Blouses & Tailored Tops", "img_product_dress_western", 4),
        CategoryEntity("cat_jeans", "Jeans", "High-Rise Sculpted Denim", "img_product_coord_accessories", 5),
        CategoryEntity("cat_western", "Western Wear", "Modern Atelier Tailoring", "img_product_dress_western", 6),
        CategoryEntity("cat_ethnic", "Ethnic Wear", "Heritage Bridal & Anarkalis", "img_hero_festive_kurti", 7),
        CategoryEntity("cat_party", "Party Wear", "Cocktail Velvet & Sequins", "img_product_dress_western", 8),
        CategoryEntity("cat_coord", "Co-ord Sets", "Ivory Linen & Resort Sets", "img_product_coord_accessories", 9),
        CategoryEntity("cat_accessories", "Women's Accessories", "Quilted Bags & Kundan Jewels", "img_product_coord_accessories", 10)
    )

    val defaultBanners = listOf(
        BannerEntity(
            id = "banner_1",
            title = "The Royal Blush Bridal Edit",
            subtitle = "Handwoven Banarasi & Organza Sarees crafted with pure champagne zari.",
            ctaText = "Shop Sarees",
            targetCategory = "Sarees",
            imageKey = "img_hero_bridal_saree",
            festivalBadge = "WEDDING SEASON 2026"
        ),
        BannerEntity(
            id = "banner_2",
            title = "Diwali & Pujo Atelier Collection",
            subtitle = "Exquisite Chanderi Anarkalis & Zardozi Kurtis with pan-India express shipping.",
            ctaText = "Explore Kurtis",
            targetCategory = "Kurtis",
            imageKey = "img_hero_festive_kurti",
            festivalBadge = "USE CODE DIWALI20"
        ),
        BannerEntity(
            id = "banner_3",
            title = "Midnight Champagne Soirée",
            subtitle = "Satin wrap dresses & tailored resort co-ord sets for effortless glamour.",
            ctaText = "Shop Party Wear",
            targetCategory = "Party Wear",
            imageKey = "img_product_dress_western",
            festivalBadge = "NEW ARRIVALS"
        )
    )

    val defaultProducts = listOf(
        ProductEntity(
            id = "prod_saree_01",
            title = "Gulabi Zari Banarasi Silk Saree",
            category = "Sarees",
            description = "Handloom pure katan Banarasi silk saree in signature Lady Lebanan blush pink with antique champagne gold zari kadhua motifs and unstitched brocade blouse piece.",
            fabricCare = "100% Pure Mulberry Katan Silk • Dry Clean Only • Store in muslin wrap",
            priceInr = 8999,
            mrpInr = 12499,
            sizesCsv = "Free Size,XS,S,M,L,XL,XXL",
            colorsCsv = "Blush Rose:#E09BAE,Champagne Gold:#C9A461,Ivory Pearl:#F7F1EE",
            stockCount = 14,
            imageKey = "img_hero_bridal_saree",
            extraImagesCsv = "img_hero_bridal_saree,img_hero_festive_kurti",
            videoHighlightTitle = "6-Yard Zari Pallu Drape Motion",
            rating = 4.9,
            reviewCount = 2,
            isFeatured = true,
            isTrending = true,
            festivalTag = "Bridal Couture"
        ),
        ProductEntity(
            id = "prod_kurti_01",
            title = "Noor Zardozi Ivory & Blush Anarkali Set",
            category = "Kurtis",
            description = "Three-piece designer Chanderi silk kurti kurta with hand-embroidered resham and champagne gota patti neckline, paired with churidar and scalloped organza dupatta.",
            fabricCare = "Pure Chanderi Silk with Mulmul Lining • Gentle Dry Clean",
            priceInr = 4499,
            mrpInr = 5999,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Ivory Blush:#F6DCE5,Rose Quartz:#C26D85,Obsidian Noir:#121013",
            stockCount = 22,
            imageKey = "img_hero_festive_kurti",
            extraImagesCsv = "img_hero_festive_kurti,img_hero_bridal_saree",
            videoHighlightTitle = "Anarkali Twirl & Dupatta Flow",
            rating = 4.8,
            reviewCount = 1,
            isFeatured = true,
            isTrending = true,
            festivalTag = "Festive Edit"
        ),
        ProductEntity(
            id = "prod_dress_01",
            title = "Aurelia Blush Satin Wrap Midi Dress",
            category = "Dresses",
            description = "Bias-cut liquid satin evening dress featuring a sculpted sweetheart drape, champagne gold belt clasp, and fluid mid-calf silhouette ideal for cocktail evenings.",
            fabricCare = "Italian Hammered Satin • Cold Hand Wash or Dry Clean",
            priceInr = 3799,
            mrpInr = 4999,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Blush Satin:#E09BAE,Obsidian Black:#121013,Champagne:#C9A461",
            stockCount = 19,
            imageKey = "img_product_dress_western",
            extraImagesCsv = "img_product_dress_western,img_product_coord_accessories",
            videoHighlightTitle = "Cocktail Satin Sheen Walk",
            rating = 4.9,
            reviewCount = 1,
            isFeatured = true,
            isTrending = true,
            festivalTag = "Evening Luxe"
        ),
        ProductEntity(
            id = "prod_coord_01",
            title = "Ivory Linen Tailored Co-ord Blazer Set",
            category = "Co-ord Sets",
            description = "Relaxed double-breasted linen-blend blazer paired with high-waist pleated wide trousers, finished with champagne mother-of-pearl buttons.",
            fabricCare = "European Flax Linen & Viscose • Steam Press Low",
            priceInr = 5299,
            mrpInr = 6899,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Ivory Cream:#FAF5F0,Soft Blush:#F6DCE5,Jet Black:#121013",
            stockCount = 16,
            imageKey = "img_product_coord_accessories",
            extraImagesCsv = "img_product_coord_accessories,img_product_dress_western",
            videoHighlightTitle = "Atelier Power Tailoring Reel",
            rating = 4.7,
            reviewCount = 0,
            isFeatured = true,
            isTrending = false,
            festivalTag = "Resort Luxe"
        ),
        ProductEntity(
            id = "prod_party_01",
            title = "velveteen Rose Sequined Cocktail Gown",
            category = "Party Wear",
            description = "Floor-grazing rose quartz evening gown adorned with micro champagne sequins and pleated cape sleeves for grand receptions.",
            fabricCare = "Georgette & Micro Sequin Mesh • Dry Clean Only",
            priceInr = 6999,
            mrpInr = 9499,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Deep Rose:#9C3E5C,Midnight Black:#121013,Champagne:#C9A461",
            stockCount = 9,
            imageKey = "img_product_dress_western",
            extraImagesCsv = "img_product_dress_western,img_hero_bridal_saree",
            videoHighlightTitle = "Sequin Shimmer Showcase",
            rating = 4.9,
            reviewCount = 0,
            isFeatured = false,
            isTrending = true,
            festivalTag = "Reception Edit"
        ),
        ProductEntity(
            id = "prod_ethnic_01",
            title = "Maharani Organza Lehenga & Choli Set",
            category = "Ethnic Wear",
            description = "Hand-painted floral organza lehenga skirt with pearl-tasseled blouse and embroidered dupatta crafted by Kolkata & Jaipur karigars.",
            fabricCare = "Pure Silk Organza • Dry Clean Only",
            priceInr = 11499,
            mrpInr = 14999,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Blush Pink:#E09BAE,Champagne Gold:#C9A461",
            stockCount = 6,
            imageKey = "img_hero_festive_kurti",
            extraImagesCsv = "img_hero_festive_kurti,img_hero_bridal_saree",
            videoHighlightTitle = "360° Heritage Flare Reel",
            rating = 5.0,
            reviewCount = 0,
            isFeatured = true,
            isTrending = false,
            festivalTag = "Shaadi Couture"
        ),
        ProductEntity(
            id = "prod_top_01",
            title = "Camellia Silk Tie-Neck Atelier Blouse",
            category = "Tops",
            description = "Lustrous mulberry silk-blend blouse with billowy bishop sleeves and detachable blush bow tie, effortless from boardroom to dinner.",
            fabricCare = "Silk Viscose Satin • Gentle Cold Wash",
            priceInr = 2199,
            mrpInr = 2899,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Pearl White:#FFFFFF,Blush Pink:#F6DCE5,Obsidian:#121013",
            stockCount = 30,
            imageKey = "img_product_dress_western",
            extraImagesCsv = "img_product_dress_western",
            videoHighlightTitle = "Silk Drape Detail",
            rating = 4.6,
            reviewCount = 0,
            isFeatured = false,
            isTrending = true,
            festivalTag = ""
        ),
        ProductEntity(
            id = "prod_jeans_01",
            title = "Sculpted High-Rise Straight Ivory Denim",
            category = "Jeans",
            description = "Premium stretch-cotton high-rise straight jeans with champagne gold hardware rivets and tailored ankle-grazing hem.",
            fabricCare = "98% Organic Cotton 2% Elastane • Machine Wash Cold",
            priceInr = 2699,
            mrpInr = 3499,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Ecru Ivory:#FAF5F0,Jet Black:#121013,Vintage Indigo:#2D3E5E",
            stockCount = 28,
            imageKey = "img_product_coord_accessories",
            extraImagesCsv = "img_product_coord_accessories",
            videoHighlightTitle = "360° Fit & Stretch Check",
            rating = 4.7,
            reviewCount = 0,
            isFeatured = false,
            isTrending = false,
            festivalTag = ""
        ),
        ProductEntity(
            id = "prod_western_01",
            title = "Parisian Blush Trench & Slip Ensemble",
            category = "Western Wear",
            description = "Structured lightweight crepe duster trench paired with minimalist tailored separates in Lady Lebanan's signature dusty rose.",
            fabricCare = "Korean Crepe • Dry Clean Recommended",
            priceInr = 4899,
            mrpInr = 6299,
            sizesCsv = "XS,S,M,L,XL,XXL",
            colorsCsv = "Dusty Blush:#D98DA4,Charcoal:#262125",
            stockCount = 12,
            imageKey = "img_product_dress_western",
            extraImagesCsv = "img_product_dress_western,img_product_coord_accessories",
            videoHighlightTitle = "Runway Walk Preview",
            rating = 4.8,
            reviewCount = 0,
            isFeatured = false,
            isTrending = true,
            festivalTag = "Autumn Edit"
        ),
        ProductEntity(
            id = "prod_acc_01",
            title = "Blush Quilted Clutch & Pearl Drop Suite",
            category = "Women's Accessories",
            description = "Hand-quilted vegan calfskin mini top-handle bag with champagne chain strap, bundled with baroque pearl & gold statement earrings.",
            fabricCare = "Wipe with soft dry cloth • Store in dust bag",
            priceInr = 2999,
            mrpInr = 3999,
            sizesCsv = "One Size",
            colorsCsv = "Blush Pink:#E09BAE,Champagne Gold:#C9A461,Obsidian:#121013",
            stockCount = 25,
            imageKey = "img_product_coord_accessories",
            extraImagesCsv = "img_product_coord_accessories",
            videoHighlightTitle = "Clasp & Gold Finish Close-up",
            rating = 4.9,
            reviewCount = 0,
            isFeatured = true,
            isTrending = true,
            festivalTag = "Gift Box Included"
        )
    )

    val defaultCoupons = listOf(
        CouponEntity(
            code = "DIWALI20",
            title = "Festive Sparkle 20% Off",
            festivalOffer = "Diwali & Festive Couture Sale",
            discountPercent = 20,
            maxDiscountInr = 2500,
            minOrderInr = 2499,
            isActive = true
        ),
        CouponEntity(
            code = "LEBANAN15",
            title = "Welcome Atelier 15% Off",
            festivalOffer = "First Boutique Purchase",
            discountPercent = 15,
            maxDiscountInr = 1500,
            minOrderInr = 1499,
            isActive = true
        ),
        CouponEntity(
            code = "BRIDAL10",
            title = "Shaadi & Saree 10% Off",
            festivalOffer = "Wedding Collection Exclusive",
            discountPercent = 10,
            maxDiscountInr = 3000,
            minOrderInr = 4999,
            isActive = true
        )
    )

    val defaultReviews = listOf(
        ReviewEntity(
            id = "rev_1",
            productId = "prod_saree_01",
            userId = DEMO_CUSTOMER_UID,
            userName = "Ananya Chatterjee",
            rating = 5,
            comment = "The blush pink silk sheen and gold zari work look genuinely royal. Delivered to Kolkata in 2 days with luxury gift boxing!",
            verifiedPurchase = true
        ),
        ReviewEntity(
            id = "rev_2",
            productId = "prod_saree_01",
            userId = "cust_meera_02",
            userName = "Meera Nair",
            rating = 5,
            comment = "Wore this to my sister's sangeet in Mumbai—zero color bleed and drapes effortlessly.",
            verifiedPurchase = true
        ),
        ReviewEntity(
            id = "rev_3",
            productId = "prod_kurti_01",
            userId = DEMO_CUSTOMER_UID,
            userName = "Ananya Chatterjee",
            rating = 5,
            comment = "Pure Chanderi comfort with intricate zardozi neckline. True to Indian size chart.",
            verifiedPurchase = true
        ),
        ReviewEntity(
            id = "rev_4",
            productId = "prod_dress_01",
            userId = "cust_riya_03",
            userName = "Riya Kapoor",
            rating = 5,
            comment = "The satin wrap silhouette is so flattering and feels luxurious against the skin.",
            verifiedPurchase = true
        )
    )

    val defaultAddresses = listOf(
        AddressEntity(
            id = "addr_1",
            userId = DEMO_CUSTOMER_UID,
            fullName = "Ananya Chatterjee",
            phone = "9876543210",
            line1 = "14B Park Street Mansions",
            line2 = "Opposite Allen Park",
            city = "Kolkata",
            state = "West Bengal",
            pinCode = "700016",
            label = "Home",
            isDefault = true
        )
    )

    val defaultOrders = listOf(
        OrderEntity(
            id = "LL-2026-8412",
            userId = DEMO_CUSTOMER_UID,
            customerName = "Ananya Chatterjee",
            customerPhone = "9876543210",
            itemsSummary = "Gulabi Zari Banarasi Silk Saree (Free Size, Blush Rose) × 1",
            itemCount = 1,
            primaryImageKey = "img_hero_bridal_saree",
            subtotalInr = 8999,
            discountInr = 1800,
            shippingFeeInr = 0,
            totalInr = 7199,
            couponCode = "DIWALI20",
            paymentMethod = PaymentMethod.UPI.label,
            paymentStatus = "Paid",
            orderStatus = OrderStatus.SHIPPED.name,
            courierName = "BlueDart Luxury Air",
            trackingNumber = "BD78492011IN",
            deliveryAddress = "14B Park Street Mansions, Kolkata, West Bengal - 700016",
            pinCode = "700016",
            deliverySpeed = "Express"
        )
    )

    val defaultNotifications = listOf(
        NotificationEntity(
            id = "notif_1",
            targetUserId = "ALL",
            title = "Festive Couture Edit Live!",
            body = "Enjoy 20% off on Banarasi Sarees & Zardozi Kurtis with code DIWALI20. Complimentary pan-India shipping above ₹2,999.",
            category = "Offer"
        ),
        NotificationEntity(
            id = "notif_2",
            targetUserId = DEMO_CUSTOMER_UID,
            title = "Order LL-2026-8412 Shipped",
            body = "Your Gulabi Zari Banarasi Silk Saree has been dispatched via BlueDart Luxury Air (AWB: BD78492011IN).",
            category = "Order"
        )
    )

    val defaultSettings = AppSettingsEntity(
        id = 1,
        freeShippingThresholdInr = 2999,
        standardShippingFeeInr = 149,
        expressShippingFeeInr = 299,
        codFeeInr = 79,
        maintenanceMode = false,
        aboutUsContent = "Founded in India, LADY LEBANAN is a single-owner women's luxury fashion house blending heritage Indian craftsmanship—Banarasi weaves, Chanderi zardozi, Kolkata handloom—with contemporary Western tailoring. Every garment is curated, quality-checked, and dispatched directly from our atelier to discerning women across all Indian states.",
        contactEmail = "concierge@ladylebanan.in",
        contactPhone = "+91 98300 11223",
        contactAddress = "Lady Lebanan Flagship Atelier, 22 Ballygunge Circular Road, Kolkata, West Bengal 700019",
        privacyPolicy = "Lady Lebanan respects your privacy. Personal details (name, Indian mobile number, delivery addresses, and order history) are used solely to fulfill orders, verify OTP logins, provide courier tracking, and improve your shopping experience. Payment card/UPI details are processed via PCI-DSS compliant Indian gateways and never stored in plain text.",
        termsConditions = "All prices on Lady Lebanan are listed in Indian Rupees (INR ₹) inclusive of applicable GST. Orders are accepted subject to inventory availability. Promotional coupon codes cannot be combined unless specified. Unauthorized reproduction of Lady Lebanan designs or brand marks is prohibited.",
        returnRefundPolicy = "We offer a 7-day hassle-free return and exchange policy from the date of delivery for unworn, unwashed garments with original tags and luxury packaging intact. Refunds for prepaid UPI/Card/NetBanking orders are credited to the original payment source within 3–5 business days after quality check; COD refunds are processed via UPI/bank transfer.",
        shippingPolicy = "Lady Lebanan ships across 19,000+ serviceable Indian PIN codes via BlueDart & Delhivery. Complimentary standard shipping applies on orders above ₹2,999. Metro deliveries arrive in 2–4 business days; Tier-2/3 cities and North-East/J&K arrive in 4–7 business days."
    )
}
