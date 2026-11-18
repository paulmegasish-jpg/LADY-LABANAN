package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ProductEntity
import com.example.ui.theme.BlushPetal
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneLight
import com.example.ui.theme.DeepRose
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import java.text.NumberFormat
import java.util.Locale

fun formatInr(amount: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${formatter.format(amount)}"
}

fun drawableResForKey(key: String): Int = when (key) {
    "img_hero_bridal_saree" -> R.drawable.img_hero_bridal_saree
    "img_hero_festive_kurti" -> R.drawable.img_hero_festive_kurti
    "img_product_dress_western" -> R.drawable.img_product_dress_western
    "img_product_coord_accessories" -> R.drawable.img_product_coord_accessories
    "ic_brand_emblem" -> R.drawable.ic_brand_emblem
    else -> R.drawable.img_hero_bridal_saree
}

@Composable
fun BrandMediaImage(
    imageKey: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (imageKey.startsWith("content://") || imageKey.startsWith("file://") || imageKey.startsWith("http")) {
        AsyncImage(
            model = imageKey,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            placeholder = painterResource(id = R.drawable.img_hero_bridal_saree),
            error = painterResource(id = R.drawable.img_hero_bridal_saree)
        )
    } else {
        Image(
            painter = painterResource(id = drawableResForKey(imageKey)),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

@Composable
fun LadyLebananLogo(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    darkSurface: Boolean = false
) {
    val textColor = if (darkSurface) PureWhite else MaterialTheme.colorScheme.onBackground
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 34.dp else 44.dp)
                .clip(CircleShape)
                .border(1.2.dp, ChampagneGold, CircleShape)
                .background(ObsidianBlack),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_brand_emblem),
                contentDescription = "Lady Lebanan Crest",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "LADY LEBANAN",
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium,
                color = textColor,
                letterSpacing = 2.2.sp,
                fontWeight = FontWeight.Bold
            )
            if (!compact) {
                Text(
                    text = "ATELIER • INDIA",
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold,
                    letterSpacing = 2.6.sp
                )
            }
        }
    }
}

@Composable
fun LuxuryProductCard(
    product: ProductEntity,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discountPct = if (product.mrpInr > product.priceInr) {
        (((product.mrpInr - product.priceInr) * 100f) / product.mrpInr).toInt()
    } else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
            .clickable(onClick = onProductClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.78f)
                    .background(BlushPetal.copy(alpha = 0.35f))
            ) {
                BrandMediaImage(
                    imageKey = product.imageKey,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize()
                )
                // Top gradient overlay for crisp icon visibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(ObsidianBlack.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                )

                // Discount / Festival badge
                if (discountPct > 0 || product.festivalTag.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(50),
                        color = ObsidianBlack.copy(alpha = 0.86f),
                        border = BorderStroke(0.6.dp, ChampagneGold)
                    ) {
                        Text(
                            text = if (discountPct > 0) "-$discountPct%" else product.festivalTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Wishlist heart with >=48dp touch target
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .minimumInteractiveComponentSize()
                        .testTag("wishlist_toggle_${product.id}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PureWhite.copy(alpha = 0.92f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isWishlisted) "Remove from wishlist" else "Save to wishlist",
                            tint = if (isWishlisted) DeepRose else ObsidianBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Rating pill
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50),
                    color = PureWhite.copy(alpha = 0.92f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${product.rating}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ObsidianBlack
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatInr(product.priceInr),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (product.mrpInr > product.priceInr) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatInr(product.mrpInr),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                if (product.stockCount in 1..5) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Only ${product.stockCount} left",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (product.stockCount == 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sold Out",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(2.dp)
                    .background(ChampagneGold)
            )
        }
        if (actionLabel != null && onActionClick != null) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onActionClick),
                color = ChampagneLight.copy(alpha = 0.7f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = ObsidianBlack,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun GoldHairlineDivider(paddingVertical: Dp = 12.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingVertical)
            .height(0.8.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, ChampagneGold.copy(alpha = 0.65f), Color.Transparent)
                )
            )
    )
}
