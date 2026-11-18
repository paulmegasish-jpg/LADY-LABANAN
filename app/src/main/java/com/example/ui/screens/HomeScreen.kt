package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.BannerEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.ProductEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.BrandMediaImage
import com.example.ui.components.GoldHairlineDivider
import com.example.ui.components.LadyLebananLogo
import com.example.ui.components.LuxuryProductCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BlushPetal
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneLight
import com.example.ui.theme.DeepRose
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite

@Composable
fun HomeScreen(
    strings: AppStrings,
    currentLanguage: AppLanguage,
    banners: List<BannerEntity>,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    coupons: List<CouponEntity>,
    wishlistIds: Set<String>,
    unreadNotificationCount: Int,
    onLanguageChange: (AppLanguage) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenCategory: (String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
    onOpenWishlist: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPolicyPage: (String) -> Unit,
    onCopyCoupon: (String) -> Unit
) {
    val activeBanners = banners.filter { it.isActive }
    val featuredProducts = products.filter { it.isFeatured }.ifEmpty { products.take(4) }
    val trendingProducts = products.filter { it.isTrending }.ifEmpty { products.take(4) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1. Luxury Brand Top Header
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LadyLebananLogo(compact = true)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Language Selector Pill (EN / हिं / বাং)
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = ChampagneLight.copy(alpha = 0.7f),
                                border = BorderStroke(0.7.dp, ChampagneGold.copy(alpha = 0.6f))
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                                    AppLanguage.entries.forEach { lang ->
                                        val selected = lang == currentLanguage
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .clickable { onLanguageChange(lang) }
                                                .testTag("lang_chip_${lang.code}"),
                                            color = if (selected) ObsidianBlack else Color.Transparent,
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = lang.shortLabel,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (selected) ChampagneGold else ObsidianBlack,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            IconButton(
                                onClick = onOpenNotifications,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("home_notifications_button")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotificationCount > 0) {
                                            Badge(containerColor = DeepRose) {
                                                Text("$unreadNotificationCount", color = PureWhite)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.NotificationsNone,
                                        contentDescription = strings.notificationsTitle
                                    )
                                }
                            }
                            IconButton(
                                onClick = onOpenWishlist,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("home_wishlist_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FavoriteBorder,
                                    contentDescription = strings.navWishlist
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar Trigger
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onOpenSearch)
                            .testTag("home_search_bar"),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = DeepRose
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.searchHint,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Editorial Banner Carousel
        if (activeBanners.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                val pagerState = rememberPagerState(pageCount = { activeBanners.size })
                Column {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        pageSpacing = 12.dp
                    ) { page ->
                        val banner = activeBanners[page]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(225.dp)
                                .clickable { onOpenCategory(banner.targetCategory) },
                            shape = RoundedCornerShape(22.dp),
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                BrandMediaImage(
                                    imageKey = banner.imageKey,
                                    contentDescription = banner.title,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    ObsidianBlack.copy(alpha = 0.85f),
                                                    ObsidianBlack.copy(alpha = 0.45f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (banner.festivalBadge.isNotBlank()) {
                                        Surface(
                                            color = ChampagneGold,
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = banner.festivalBadge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ObsidianBlack,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }

                                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                                        Text(
                                            text = banner.title,
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = PureWhite,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = banner.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BlushPetal,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { onOpenCategory(banner.targetCategory) },
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = PureWhite,
                                                contentColor = ObsidianBlack
                                            ),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(banner.ctaText, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(activeBanners.size) { index ->
                            val active = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(width = if (active) 20.dp else 7.dp, height = 7.dp)
                                    .clip(CircleShape)
                                    .background(if (active) DeepRose else ChampagneGold.copy(alpha = 0.45f))
                            )
                        }
                    }
                }
            }
        }

        // 3. Pan-India Luxury Assurance Strip
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = ObsidianBlack,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Verified, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pan-India Delivery • 19,000+ PINs • Free Shipping > ₹2,999 • COD",
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // 4. 10 Women's Clothing Categories
        item {
            Spacer(modifier = Modifier.height(22.dp))
            SectionHeader(
                title = strings.categoriesTitle,
                actionLabel = strings.viewAll,
                onActionClick = { onOpenCategory("All") },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories.filter { it.isActive }, key = { it.id }) { cat ->
                    val localizedName = strings.categoryNames[cat.name] ?: cat.name
                    Column(
                        modifier = Modifier
                            .width(92.dp)
                            .clickable { onOpenCategory(cat.name) }
                            .testTag("category_tile_${cat.name}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(78.dp),
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, ChampagneGold),
                            color = BlushPetal
                        ) {
                            BrandMediaImage(
                                imageKey = cat.imageKey,
                                contentDescription = localizedName,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = localizedName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 5. Festival Couture Coupons
        val activeCoupons = coupons.filter { it.isActive }
        if (activeCoupons.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(22.dp))
                SectionHeader(
                    title = strings.festivalOffersTitle,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeCoupons, key = { it.code }) { coupon ->
                        Card(
                            modifier = Modifier
                                .width(235.dp)
                                .clickable { onCopyCoupon(coupon.code) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ChampagneLight),
                            border = BorderStroke(1.dp, ChampagneGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = DeepRose)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = coupon.code,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = ObsidianBlack,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${coupon.discountPercent}% off up to ₹${coupon.maxDiscountInr}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DeepRose
                                    )
                                    Text(
                                        text = coupon.festivalOffer,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ObsidianBlack.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Signature Atelier Edits
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = strings.featuredEditsTitle,
                actionLabel = strings.viewAll,
                onActionClick = { onOpenCategory("All") },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredProducts, key = { it.id }) { product ->
                    LuxuryProductCard(
                        product = product,
                        isWishlisted = wishlistIds.contains(product.id),
                        onProductClick = { onOpenProduct(product.id) },
                        onToggleWishlist = { onToggleWishlist(product.id) },
                        modifier = Modifier.width(176.dp)
                    )
                }
            }
        }

        // 7. Trending Across India
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = strings.trendingNowTitle,
                actionLabel = strings.viewAll,
                onActionClick = { onOpenCategory("All") },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trendingProducts, key = { "trend_${it.id}" }) { product ->
                    LuxuryProductCard(
                        product = product,
                        isWishlisted = wishlistIds.contains(product.id),
                        onProductClick = { onOpenProduct(product.id) },
                        onToggleWishlist = { onToggleWishlist(product.id) },
                        modifier = Modifier.width(176.dp)
                    )
                }
            }
        }

        // 8. Maison & Store Policies Footer
        item {
            Spacer(modifier = Modifier.height(24.dp))
            GoldHairlineDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = strings.storePoliciesTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                val policyLinks = listOf(
                    "about" to strings.aboutUs,
                    "contact" to strings.contactUs,
                    "privacy" to strings.privacyPolicy,
                    "terms" to strings.termsConditions,
                    "returns" to strings.returnPolicy,
                    "shipping" to strings.shippingPolicy
                )
                policyLinks.chunked(2).forEach { pair ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        pair.forEach { (key, label) ->
                            TextButton(
                                onClick = { onOpenPolicyPage(key) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DeepRose
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
