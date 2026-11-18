package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.parseColorOptions
import com.example.data.model.parseSizes
import com.example.data.service.PinServiceabilityResult
import com.example.i18n.AppStrings
import com.example.ui.components.BrandMediaImage
import com.example.ui.components.GoldHairlineDivider
import com.example.ui.components.formatInr
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneLight
import com.example.ui.theme.DeepRose
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    strings: AppStrings,
    product: ProductEntity,
    reviews: List<ReviewEntity>,
    isWishlisted: Boolean,
    pinResult: PinServiceabilityResult?,
    onBack: () -> Unit,
    onCheckPin: (String) -> Unit,
    onAddToCart: (size: String, color: String) -> Unit,
    onToggleWishlist: () -> Unit,
    onSubmitReview: (rating: Int, comment: String) -> Unit
) {
    val sizes = remember(product.sizesCsv) { parseSizes(product.sizesCsv) }
    val colors = remember(product.colorsCsv) { parseColorOptions(product.colorsCsv) }
    var selectedSize by remember(product.id) { mutableStateOf(sizes.firstOrNull() ?: "M") }
    var selectedColor by remember(product.id) { mutableStateOf(colors.firstOrNull()?.name ?: "Blush Rose") }
    var pinInput by remember { mutableStateOf("") }
    var showSizeGuide by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showVideoReel by remember { mutableStateOf(false) }

    val mediaItems = remember(product.id, product.imageKey, product.extraImagesCsv) {
        val extras = product.extraImagesCsv.split(",").map { it.trim() }.filter { it.isNotBlank() }
        (listOf(product.imageKey) + extras).distinct()
    }
    val pagerState = rememberPagerState(pageCount = { mediaItems.size + 1 }) // Last slide is Video Lookbook Reel

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleWishlist, modifier = Modifier.testTag("detail_wishlist_button")) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) DeepRose else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 10.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onToggleWishlist,
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Save",
                            tint = DeepRose
                        )
                    }
                    Button(
                        onClick = { onAddToCart(selectedSize, selectedColor) },
                        enabled = product.stockCount > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("detail_add_to_bag_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.ShoppingBag, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (product.stockCount > 0) "${strings.addToBag} • ${formatInr(product.priceInr)}" else "Out of Stock",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Product Image & Video Reel Pager
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        if (page < mediaItems.size) {
                            BrandMediaImage(
                                imageKey = mediaItems[page],
                                contentDescription = product.title,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Lookbook Video Slide
                            val infiniteTransition = rememberInfiniteTransition(label = "reel")
                            val zoom by infiniteTransition.animateFloat(
                                initialValue = 1.0f,
                                targetValue = 1.08f,
                                animationSpec = infiniteRepeatable(tween(3500), RepeatMode.Reverse),
                                label = "zoom"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ObsidianBlack)
                                    .clickable { showVideoReel = true },
                                contentAlignment = Alignment.Center
                            ) {
                                BrandMediaImage(
                                    imageKey = product.imageKey,
                                    contentDescription = product.videoHighlightTitle,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .scale(zoom)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(ObsidianBlack.copy(alpha = 0.45f))
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayCircle,
                                        contentDescription = "Play Product Video",
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = product.videoHighlightTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = if (product.videoUri.isNotBlank()) "Tap to play uploaded runway video" else "Tap to preview 360° atelier lookbook reel",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ChampagneGold
                                    )
                                }
                            }
                        }
                    }

                    // Indicator Dots
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(ObsidianBlack.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(mediaItems.size + 1) { idx ->
                            val isVideo = idx == mediaItems.size
                            Box(
                                modifier = Modifier
                                    .size(if (pagerState.currentPage == idx) 10.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            pagerState.currentPage == idx -> ChampagneGold
                                            isVideo -> DeepRose
                                            else -> PureWhite.copy(alpha = 0.6f)
                                        }
                                    )
                            )
                        }
                    }
                }
            }

            // 2. Title, Price, Stock
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (product.festivalTag.isNotBlank()) {
                            Surface(color = ChampagneLight, shape = RoundedCornerShape(50)) {
                                Text(
                                    text = product.festivalTag.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ObsidianBlack,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating} (${reviews.size} reviews)",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = product.title, style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatInr(product.priceInr),
                            style = MaterialTheme.typography.headlineMedium,
                            color = DeepRose,
                            fontWeight = FontWeight.Bold
                        )
                        if (product.mrpInr > product.priceInr) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = formatInr(product.mrpInr),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            val pct = (((product.mrpInr - product.priceInr) * 100f) / product.mrpInr).toInt()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$pct% OFF • Incl. GST",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    GoldHairlineDivider()

                    // 3. Color Selector
                    Text("${strings.selectColor}: $selectedColor", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(colors) { colorOpt ->
                            val isSelected = selectedColor == colorOpt.name
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) DeepRose else MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable { selectedColor = colorOpt.name }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(colorOpt.hex))
                                        .border(0.6.dp, ObsidianBlack.copy(alpha = 0.3f), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(colorOpt.name, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Size Selector (XS, S, M, L, XL, XXL)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${strings.selectSize}: $selectedSize", style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { showSizeGuide = true }) {
                            Icon(Icons.Filled.Straighten, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.sizeGuide)
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sizes) { sz ->
                            val selected = selectedSize == sz
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedSize = sz }
                                    .testTag("size_pill_$sz"),
                                color = if (selected) ObsidianBlack else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, if (selected) ChampagneGold else MaterialTheme.colorScheme.outline),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = sz,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) ChampagneGold else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }

                    GoldHairlineDivider()

                    // 5. Pan-India PIN Code Serviceability Checker
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = DeepRose)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.pinCheckerTitle, style = MaterialTheme.typography.titleSmall)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = pinInput,
                                    onValueChange = { if (it.length <= 6) pinInput = it.filter { ch -> ch.isDigit() } },
                                    placeholder = { Text(strings.pinCheckerHint, style = MaterialTheme.typography.bodySmall) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("pin_checker_input")
                                )
                                Button(
                                    onClick = { onCheckPin(pinInput) },
                                    modifier = Modifier
                                        .height(52.dp)
                                        .testTag("pin_checker_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(strings.checkPin)
                                }
                            }
                            if (pinResult != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = if (pinResult.isServiceable) EmeraldSurface else MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (pinResult.isServiceable) {
                                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text(
                                            text = pinResult.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (pinResult.isServiceable) EmeraldSuccess else MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    GoldHairlineDivider()

                    // 6. Description & Craftsmanship
                    Text("Atelier Details & Craftsmanship", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(product.description, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = ChampagneLight.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = product.fabricCare,
                            style = MaterialTheme.typography.bodySmall,
                            color = ObsidianBlack,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    GoldHairlineDivider()

                    // 7. Verified Customer Reviews
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${strings.reviewsTitle} (${reviews.size})", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { showReviewDialog = true }) {
                            Text(strings.writeReview)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (reviews.isEmpty()) {
                        Text(
                            text = "Be the first patron to review this silhouette.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        reviews.forEach { rev ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(rev.userName, style = MaterialTheme.typography.titleSmall)
                                        Row {
                                            repeat(rev.rating) {
                                                Icon(Icons.Filled.Star, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(rev.comment, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSizeGuide) {
        AlertDialog(
            onDismissRequest = { showSizeGuide = false },
            title = { Text("Lady Lebanan Indian Size Guide") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("XS: Bust 32\" | Waist 26\" | Hip 35\"")
                    Text("S:  Bust 34\" | Waist 28\" | Hip 37\"")
                    Text("M:  Bust 36\" | Waist 30\" | Hip 39\"")
                    Text("L:  Bust 38\" | Waist 32\" | Hip 41\"")
                    Text("XL: Bust 40\" | Waist 34\" | Hip 43\"")
                    Text("XXL: Bust 42\" | Waist 36\" | Hip 45\"")
                }
            },
            confirmButton = {
                TextButton(onClick = { showSizeGuide = false }) { Text("Close") }
            }
        )
    }

    if (showReviewDialog) {
        var rating by remember { mutableIntStateOf(5) }
        var comment by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Review ${product.title}") },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { rating = star }) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= rating) ChampagneGold else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Share fit, fabric & styling feedback") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSubmitReview(rating, comment)
                    showReviewDialog = false
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showVideoReel) {
        AlertDialog(
            onDismissRequest = { showVideoReel = false },
            title = { Text(product.videoHighlightTitle) },
            text = {
                if (product.videoUri.isNotBlank()) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(Uri.parse(product.videoUri))
                                val controller = MediaController(ctx)
                                controller.setAnchorView(this)
                                setMediaController(controller)
                                start()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BrandMediaImage(
                            imageKey = product.imageKey,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Editorial Runway Drape Showcase • Upload custom MP4 video clips directly from Owner Admin Dashboard.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVideoReel = false }) { Text("Close") }
            }
        )
    }
}
