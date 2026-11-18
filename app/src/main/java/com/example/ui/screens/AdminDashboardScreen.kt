package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
import com.example.ui.components.BrandMediaImage
import com.example.ui.components.formatInr
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneLight
import com.example.ui.theme.DeepRose
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AdminAnalytics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentUser: UserEntity?,
    analytics: AdminAnalytics,
    products: List<ProductEntity>,
    categories: List<CategoryEntity>,
    banners: List<BannerEntity>,
    coupons: List<CouponEntity>,
    orders: List<OrderEntity>,
    users: List<UserEntity>,
    tickets: List<SupportTicketEntity>,
    settings: AppSettingsEntity,
    onBack: () -> Unit,
    onSaveProduct: (String?, String, String, String, String, Int, Int, String, String, Int, String, String, String, Boolean, Boolean, String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onUpdateOrderStatus: (OrderEntity, OrderStatus, String, String) -> Unit,
    onSaveCoupon: (String, String, String, Int, Int, Int, Boolean) -> Unit,
    onDeleteCoupon: (String) -> Unit,
    onSaveBanner: (String?, String, String, String, String, String, String) -> Unit,
    onDeleteBanner: (String) -> Unit,
    onSaveCategory: (String?, String, String, String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onReplyTicket: (SupportTicketEntity, String, String) -> Unit,
    onSendBroadcast: (String, String) -> Unit,
    onSaveSettings: (AppSettingsEntity) -> Unit
) {
    // Strict Role-Based Access Guard
    if (currentUser?.role != UserRole.ADMIN.name) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(56.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Unauthorized Access", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Only the authorized Lady Lebanan Owner account can access the Admin Dashboard.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) { Text("Return to Store") }
            }
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddProduct by remember { mutableStateOf(false) }

    val tabTitles = listOf("Analytics", "Products", "Orders & Refunds", "Marketing", "Store & Support")

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBlack,
                    titleContentColor = ChampagneGold,
                    navigationIconContentColor = PureWhite
                ),
                title = {
                    Column {
                        Text("LADY LEBANAN ADMIN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Owner Control Center", style = MaterialTheme.typography.labelSmall, color = PureWhite)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = {
                        editingProduct = null
                        showAddProduct = true
                    },
                    containerColor = ObsidianBlack,
                    contentColor = ChampagneGold,
                    modifier = Modifier.testTag("admin_add_product_fab")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Product")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DeepRose,
                edgePadding = 12.dp
            ) {
                tabTitles.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                        modifier = Modifier.testTag("admin_tab_$idx")
                    )
                }
            }

            when (selectedTab) {
                0 -> AdminAnalyticsTab(analytics = analytics)
                1 -> AdminProductsTab(
                    products = products,
                    onEdit = {
                        editingProduct = it
                        showAddProduct = true
                    },
                    onDelete = onDeleteProduct
                )
                2 -> AdminOrdersTab(
                    orders = orders,
                    onUpdateOrder = onUpdateOrderStatus
                )
                3 -> AdminMarketingTab(
                    banners = banners,
                    coupons = coupons,
                    categories = categories,
                    onSaveBanner = onSaveBanner,
                    onDeleteBanner = onDeleteBanner,
                    onSaveCoupon = onSaveCoupon,
                    onDeleteCoupon = onDeleteCoupon,
                    onSaveCategory = onSaveCategory,
                    onDeleteCategory = onDeleteCategory,
                    onSendBroadcast = onSendBroadcast
                )
                4 -> AdminStoreAndSupportTab(
                    users = users,
                    tickets = tickets,
                    settings = settings,
                    productsCount = products.size,
                    ordersCount = orders.size,
                    onReplyTicket = onReplyTicket,
                    onSaveSettings = onSaveSettings
                )
            }
        }
    }

    if (showAddProduct) {
        AdminProductEditorDialog(
            existing = editingProduct,
            categories = categories.map { it.name },
            onDismiss = { showAddProduct = false },
            onSave = { title, cat, desc, fabric, price, mrp, sizes, colors, stock, imgKey, vidUri, vidTitle, feat, trend, festTag ->
                onSaveProduct(editingProduct?.id, title, cat, desc, fabric, price, mrp, sizes, colors, stock, imgKey, vidUri, vidTitle, feat, trend, festTag)
                showAddProduct = false
            }
        )
    }
}

@Composable
private fun AdminAnalyticsTab(analytics: AdminAnalytics) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard("Total Revenue", formatInr(analytics.totalRevenueInr), Modifier.weight(1f), highlight = true)
                KpiCard("Total Orders", "${analytics.totalOrders}", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard("Active Orders", "${analytics.activeOrders}", Modifier.weight(1f))
                KpiCard("Patrons", "${analytics.customerCount}", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            KpiCard("Average Order Value (AOV)", formatInr(analytics.avgOrderValueInr), Modifier.fillMaxWidth())
        }

        // Custom Compose Canvas Revenue Bar Chart
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent Order Revenue Pulse", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (analytics.recentOrderAmounts.isEmpty()) {
                        Text("No order data yet.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        val maxVal = (analytics.recentOrderAmounts.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            val barCount = analytics.recentOrderAmounts.size
                            val slotWidth = size.width / barCount
                            val barWidth = (slotWidth * 0.55f).coerceAtMost(64f)
                            analytics.recentOrderAmounts.forEachIndexed { idx, (_, amount) ->
                                val barHeight = (amount.toFloat() / maxVal) * (size.height - 20f)
                                val left = idx * slotWidth + (slotWidth - barWidth) / 2f
                                drawRoundRect(
                                    color = if (idx == barCount - 1) DeepRose else ChampagneGold,
                                    topLeft = Offset(left, size.height - barHeight),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            analytics.recentOrderAmounts.forEach { (orderShort, amount) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("#$orderShort", style = MaterialTheme.typography.labelSmall)
                                    Text(formatInr(amount), style = MaterialTheme.typography.labelSmall, color = DeepRose)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Low Stock Inventory Alerts
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = DeepRose)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Low Stock Alerts (<= 8 units)", style = MaterialTheme.typography.titleMedium)
                    }
                    if (analytics.lowStockProducts.isEmpty()) {
                        Text("All products have healthy inventory.", style = MaterialTheme.typography.bodySmall, color = EmeraldSuccess)
                    } else {
                        analytics.lowStockProducts.forEach { prod ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(prod.title, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text("${prod.stockCount} left", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, modifier: Modifier = Modifier, highlight: Boolean = false) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (highlight) ObsidianBlack else MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (highlight) ChampagneGold else MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = if (highlight) ChampagneGold else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = if (highlight) PureWhite else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun AdminProductsTab(
    products: List<ProductEntity>,
    onEdit: (ProductEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(products, key = { it.id }) { prod ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrandMediaImage(
                        imageKey = prod.imageKey,
                        contentDescription = prod.title,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(prod.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                        Text("${prod.category} • Stock: ${prod.stockCount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${formatInr(prod.priceInr)} (MRP ${formatInr(prod.mrpInr)})", style = MaterialTheme.typography.labelMedium, color = DeepRose)
                    }
                    IconButton(onClick = { onEdit(prod) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit product")
                    }
                    IconButton(onClick = { onDelete(prod.id) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete product", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrdersTab(
    orders: List<OrderEntity>,
    onUpdateOrder: (OrderEntity, OrderStatus, String, String) -> Unit
) {
    var editingOrder by remember { mutableStateOf<OrderEntity?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            val st = runCatching { OrderStatus.valueOf(order.orderStatus) }.getOrDefault(OrderStatus.PLACED)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${order.id} • ${order.customerName}", style = MaterialTheme.typography.titleSmall)
                        Surface(color = ChampagneLight, shape = RoundedCornerShape(50)) {
                            Text(st.displayLabel, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                    Text(order.itemsSummary, style = MaterialTheme.typography.bodySmall)
                    Text("${formatInr(order.totalInr)} • ${order.paymentMethod} (${order.paymentStatus})", style = MaterialTheme.typography.labelMedium, color = DeepRose)
                    Text("Ship to PIN ${order.pinCode} • ${order.courierName} (${order.trackingNumber})", style = MaterialTheme.typography.labelSmall)
                    if (order.cancellationReason.isNotBlank()) {
                        Text("Cancel Reason: ${order.cancellationReason}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                    if (order.returnReason.isNotBlank()) {
                        Text("Return Reason: ${order.returnReason} • Refund: ${order.refundStatus}", style = MaterialTheme.typography.bodySmall, color = DeepRose)
                    }
                    Button(
                        onClick = { editingOrder = order },
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = ChampagneGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manage Status / Courier / Refund")
                    }
                }
            }
        }
    }

    editingOrder?.let { target ->
        var selectedStatus by remember(target) {
            mutableStateOf(runCatching { OrderStatus.valueOf(target.orderStatus) }.getOrDefault(OrderStatus.CONFIRMED))
        }
        var courier by remember(target) { mutableStateOf(target.courierName) }
        var awb by remember(target) { mutableStateOf(target.trackingNumber) }
        var statusMenuOpen by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingOrder = null },
            title = { Text("Manage ${target.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box {
                        OutlinedButton(onClick = { statusMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Status: ${selectedStatus.displayLabel}")
                        }
                        DropdownMenu(expanded = statusMenuOpen, onDismissRequest = { statusMenuOpen = false }) {
                            OrderStatus.entries.forEach { st ->
                                DropdownMenuItem(text = { Text(st.displayLabel) }, onClick = {
                                    selectedStatus = st
                                    statusMenuOpen = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = courier, onValueChange = { courier = it }, label = { Text("Courier Partner") }, singleLine = true)
                    OutlinedTextField(value = awb, onValueChange = { awb = it }, label = { Text("AWB Tracking Number") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateOrder(target, selectedStatus, courier, awb)
                    editingOrder = null
                }) { Text("Save & Notify Customer") }
            },
            dismissButton = {
                TextButton(onClick = { editingOrder = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminProductEditorDialog(
    existing: ProductEntity?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, Int, String, String, Int, String, String, String, Boolean, Boolean, String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: categories.firstOrNull() ?: "Sarees") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var fabricCare by remember { mutableStateOf(existing?.fabricCare ?: "Pure Silk Atelier Craft • Dry Clean Only") }
    var priceStr by remember { mutableStateOf(existing?.priceInr?.toString() ?: "3999") }
    var mrpStr by remember { mutableStateOf(existing?.mrpInr?.toString() ?: "4999") }
    var sizesCsv by remember { mutableStateOf(existing?.sizesCsv ?: "XS,S,M,L,XL,XXL") }
    var colorsCsv by remember { mutableStateOf(existing?.colorsCsv ?: "Blush Rose:#E09BAE,Champagne Gold:#C9A461,Obsidian:#121013") }
    var stockStr by remember { mutableStateOf(existing?.stockCount?.toString() ?: "15") }
    var imageKey by remember { mutableStateOf(existing?.imageKey ?: "img_hero_bridal_saree") }
    var videoUri by remember { mutableStateOf(existing?.videoUri ?: "") }
    var videoTitle by remember { mutableStateOf(existing?.videoHighlightTitle ?: "Runway Drape Preview") }
    var isFeatured by remember { mutableStateOf(existing?.isFeatured ?: true) }
    var isTrending by remember { mutableStateOf(existing?.isTrending ?: true) }
    var festivalTag by remember { mutableStateOf(existing?.festivalTag ?: "") }
    var catMenuExpanded by remember { mutableStateOf(false) }

    // Zero-Permission Android Photo Picker for Images & Videos
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            imageKey = uri.toString()
        }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            videoUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Upload New Luxury Product" else "Edit Product") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") }, singleLine = true)
                Box {
                    OutlinedButton(onClick = { catMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Category: $category")
                    }
                    DropdownMenu(expanded = catMenuExpanded, onDismissRequest = { catMenuExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = {
                                category = cat
                                catMenuExpanded = false
                            })
                        }
                    }
                }
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, minLines = 2)
                OutlinedTextField(value = fabricCare, onValueChange = { fabricCare = it }, label = { Text("Fabric & Care") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Price ₹") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = mrpStr,
                        onValueChange = { mrpStr = it.filter { c -> c.isDigit() } },
                        label = { Text("MRP ₹") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f)
                    )
                }
                OutlinedTextField(value = sizesCsv, onValueChange = { sizesCsv = it }, label = { Text("Sizes CSV (XS,S,M,L,XL,XXL)") }, singleLine = true)
                OutlinedTextField(value = colorsCsv, onValueChange = { colorsCsv = it }, label = { Text("Colors (Name:#HEX,...)") }, singleLine = true)
                OutlinedTextField(value = festivalTag, onValueChange = { festivalTag = it }, label = { Text("Festival / Collection Tag") }, singleLine = true)

                // Zero-permission Photo Picker & Video Picker buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Photo")
                    }
                    OutlinedButton(
                        onClick = { videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Video")
                    }
                }
                Text("Media Preset:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(
                        listOf(
                            "img_hero_bridal_saree" to "Saree",
                            "img_hero_festive_kurti" to "Kurti",
                            "img_product_dress_western" to "Dress",
                            "img_product_coord_accessories" to "Co-ord"
                        )
                    ) { (key, label) ->
                        FilterChip(selected = imageKey == key, onClick = { imageKey = key }, label = { Text(label) })
                    }
                }
                if (videoUri.isNotBlank()) {
                    Text("Video attached: $videoUri", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess)
                }
                OutlinedTextField(value = videoTitle, onValueChange = { videoTitle = it }, label = { Text("Video Reel Title") }, singleLine = true)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    Text("Featured")
                    Spacer(modifier = Modifier.width(12.dp))
                    Checkbox(checked = isTrending, onCheckedChange = { isTrending = it })
                    Text("Trending")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        title, category, description, fabricCare,
                        priceStr.toIntOrNull() ?: 2999,
                        mrpStr.toIntOrNull() ?: 3999,
                        sizesCsv, colorsCsv,
                        stockStr.toIntOrNull() ?: 10,
                        imageKey, videoUri, videoTitle, isFeatured, isTrending, festivalTag
                    )
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) { Text("Save Product") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
