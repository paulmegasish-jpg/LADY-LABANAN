package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneLight
import com.example.ui.theme.DeepRose
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ObsidianBlack

@Composable
fun AdminMarketingTab(
    banners: List<BannerEntity>,
    coupons: List<CouponEntity>,
    categories: List<CategoryEntity>,
    onSaveBanner: (String?, String, String, String, String, String, String) -> Unit,
    onDeleteBanner: (String) -> Unit,
    onSaveCoupon: (String, String, String, Int, Int, Int, Boolean) -> Unit,
    onDeleteCoupon: (String) -> Unit,
    onSaveCategory: (String?, String, String, String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onSendBroadcast: (String, String) -> Unit
) {
    var notifTitle by remember { mutableStateOf("") }
    var notifBody by remember { mutableStateOf("") }
    var showAddCoupon by remember { mutableStateOf(false) }
    var showAddBanner by remember { mutableStateOf(false) }
    var showAddCategory by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Push Notification Broadcaster
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.9.dp, ChampagneGold)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Campaign, contentDescription = null, tint = DeepRose)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast Push Notification", style = MaterialTheme.typography.titleMedium)
                    }
                    OutlinedTextField(value = notifTitle, onValueChange = { notifTitle = it }, label = { Text("Alert Title (e.g. Flash Festive Sale!)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notifBody, onValueChange = { notifBody = it }, label = { Text("Message to all customers") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                    Button(
                        onClick = {
                            onSendBroadcast(notifTitle, notifBody)
                            notifTitle = ""
                            notifBody = ""
                        },
                        enabled = notifTitle.isNotBlank() && notifBody.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Send Push Notification") }
                }
            }
        }

        // 2. Festival Coupons
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Discount Coupons & Festival Offers", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showAddCoupon = true }) { Text("+ Add Coupon") }
            }
        }
        items(coupons, key = { it.code }) { c ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ChampagneLight.copy(alpha = 0.45f)),
                border = BorderStroke(0.8.dp, ChampagneGold)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${c.code} • ${c.discountPercent}% OFF (Max ₹${c.maxDiscountInr})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${c.festivalOffer} • Min order ₹${c.minOrderInr}", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { onDeleteCoupon(c.code) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete coupon", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // 3. Homepage Banners
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Homepage Hero Banners", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showAddBanner = true }) { Text("+ Add Banner") }
            }
        }
        items(banners, key = { it.id }) { b ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(b.title, style = MaterialTheme.typography.titleSmall)
                        Text("${b.festivalBadge} -> ${b.targetCategory}", style = MaterialTheme.typography.bodySmall, color = DeepRose)
                    }
                    IconButton(onClick = { onDeleteBanner(b.id) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete banner", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // 4. Categories Manager
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Store Categories (${categories.size})", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showAddCategory = true }) { Text("+ Add Category") }
            }
        }
        items(categories, key = { it.id }) { cat ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cat.name, style = MaterialTheme.typography.titleSmall)
                        Text(cat.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onDeleteCategory(cat.id) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete category", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showAddCoupon) {
        var code by remember { mutableStateOf("") }
        var title by remember { mutableStateOf("") }
        var fest by remember { mutableStateOf("Festival Couture Offer") }
        var pct by remember { mutableStateOf("15") }
        var maxDisc by remember { mutableStateOf("1500") }
        var minOrd by remember { mutableStateOf("1999") }
        AlertDialog(
            onDismissRequest = { showAddCoupon = false },
            title = { Text("Create Festival Coupon") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("Coupon Code (e.g. PUJO25)") }, singleLine = true)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Offer Title") }, singleLine = true)
                    OutlinedTextField(value = fest, onValueChange = { fest = it }, label = { Text("Festival Name") }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = pct, onValueChange = { pct = it.filter { c -> c.isDigit() } }, label = { Text("% Off") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = maxDisc, onValueChange = { maxDisc = it.filter { c -> c.isDigit() } }, label = { Text("Max ₹") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = minOrd, onValueChange = { minOrd = it.filter { c -> c.isDigit() } }, label = { Text("Min ₹") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSaveCoupon(code, title, fest, pct.toIntOrNull() ?: 15, maxDisc.toIntOrNull() ?: 1500, minOrd.toIntOrNull() ?: 1999, true)
                    showAddCoupon = false
                }, enabled = code.isNotBlank()) { Text("Save Coupon") }
            },
            dismissButton = { TextButton(onClick = { showAddCoupon = false }) { Text("Cancel") } }
        )
    }

    if (showAddBanner) {
        var title by remember { mutableStateOf("") }
        var subtitle by remember { mutableStateOf("") }
        var badge by remember { mutableStateOf("FESTIVE EDIT") }
        var targetCat by remember { mutableStateOf("Sarees") }
        var imgKey by remember { mutableStateOf("img_hero_bridal_saree") }
        AlertDialog(
            onDismissRequest = { showAddBanner = false },
            title = { Text("Add Homepage Banner") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Banner Headline") }, singleLine = true)
                    OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, minLines = 2)
                    OutlinedTextField(value = badge, onValueChange = { badge = it }, label = { Text("Badge Text") }, singleLine = true)
                    OutlinedTextField(value = targetCat, onValueChange = { targetCat = it }, label = { Text("Target Category") }, singleLine = true)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("img_hero_bridal_saree" to "Saree", "img_hero_festive_kurti" to "Kurti", "img_product_dress_western" to "Dress")) { (k, lbl) ->
                            FilterChip(selected = imgKey == k, onClick = { imgKey = k }, label = { Text(lbl) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSaveBanner(null, title, subtitle, "Shop Now", targetCat, imgKey, badge)
                    showAddBanner = false
                }, enabled = title.isNotBlank()) { Text("Publish Banner") }
            },
            dismissButton = { TextButton(onClick = { showAddBanner = false }) { Text("Cancel") } }
        )
    }

    if (showAddCategory) {
        var name by remember { mutableStateOf("") }
        var subtitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategory = false },
            title = { Text("Add Women's Category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Category Name") }, singleLine = true)
                    OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSaveCategory(null, name, subtitle, "img_hero_festive_kurti")
                    showAddCategory = false
                }, enabled = name.isNotBlank()) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddCategory = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AdminStoreAndSupportTab(
    users: List<UserEntity>,
    tickets: List<SupportTicketEntity>,
    settings: AppSettingsEntity,
    productsCount: Int,
    ordersCount: Int,
    onReplyTicket: (SupportTicketEntity, String, String) -> Unit,
    onSaveSettings: (AppSettingsEntity) -> Unit
) {
    var freeThresh by remember(settings) { mutableStateOf(settings.freeShippingThresholdInr.toString()) }
    var stdFee by remember(settings) { mutableStateOf(settings.standardShippingFeeInr.toString()) }
    var expFee by remember(settings) { mutableStateOf(settings.expressShippingFeeInr.toString()) }
    var codFee by remember(settings) { mutableStateOf(settings.codFeeInr.toString()) }
    var aboutText by remember(settings) { mutableStateOf(settings.aboutUsContent) }
    var contactEmail by remember(settings) { mutableStateOf(settings.contactEmail) }
    var maintenance by remember(settings) { mutableStateOf(settings.maintenanceMode) }

    var replyTarget by remember { mutableStateOf<SupportTicketEntity?>(null) }
    var showBackupDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Delivery & App Settings
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Delivery Charges & Store Content", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = freeThresh, onValueChange = { freeThresh = it.filter { c -> c.isDigit() } }, label = { Text("Free Ship >= ₹") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = stdFee, onValueChange = { stdFee = it.filter { c -> c.isDigit() } }, label = { Text("Std ₹") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = expFee, onValueChange = { expFee = it.filter { c -> c.isDigit() } }, label = { Text("Express ₹") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = codFee, onValueChange = { codFee = it.filter { c -> c.isDigit() } }, label = { Text("COD Fee ₹") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = contactEmail, onValueChange = { contactEmail = it }, label = { Text("Support Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = aboutText, onValueChange = { aboutText = it }, label = { Text("About Us / Website Content") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Maintenance Mode", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = maintenance, onCheckedChange = { maintenance = it })
                    }
                    Button(
                        onClick = {
                            onSaveSettings(
                                settings.copy(
                                    freeShippingThresholdInr = freeThresh.toIntOrNull() ?: 2999,
                                    standardShippingFeeInr = stdFee.toIntOrNull() ?: 149,
                                    expressShippingFeeInr = expFee.toIntOrNull() ?: 299,
                                    codFeeInr = codFee.toIntOrNull() ?: 79,
                                    contactEmail = contactEmail,
                                    aboutUsContent = aboutText,
                                    maintenanceMode = maintenance
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Save Store & Delivery Settings") }

                    OutlinedButton(onClick = { showBackupDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Backup, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Store Backup & Monitoring Snapshot")
                    }
                }
            }
        }

        // 2. Customer Support Tickets
        item {
            Text("Customer Support Tickets (${tickets.size})", style = MaterialTheme.typography.titleMedium)
        }
        items(tickets, key = { it.id }) { t ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${t.id} • ${t.userName}", style = MaterialTheme.typography.titleSmall)
                        Surface(color = ChampagneLight, shape = RoundedCornerShape(50)) {
                            Text(t.status, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }
                    Text(t.subject, style = MaterialTheme.typography.labelLarge, color = DeepRose)
                    Text(t.message, style = MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = { replyTarget = t },
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = ChampagneGold)
                    ) { Text("Reply / Resolve") }
                }
            }
        }

        // 3. Registered Customers Directory
        val customers = users.filter { it.role == UserRole.CUSTOMER.name }
        item {
            Text("Registered Customers (${customers.size})", style = MaterialTheme.typography.titleMedium)
        }
        items(customers, key = { it.id }) { cust ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cust.name, style = MaterialTheme.typography.titleSmall)
                        Text("${cust.email} • +91 ${cust.phone}", style = MaterialTheme.typography.bodySmall)
                    }
                    if (cust.isOtpVerified) {
                        Text("OTP Verified", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess)
                    }
                }
            }
        }
    }

    replyTarget?.let { t ->
        var reply by remember(t) { mutableStateOf(t.adminReply) }
        var status by remember(t) { mutableStateOf("Resolved") }
        AlertDialog(
            onDismissRequest = { replyTarget = null },
            title = { Text("Reply to ${t.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(t.message, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = reply, onValueChange = { reply = it }, label = { Text("Concierge Reply") }, minLines = 2)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("In Progress", "Resolved").forEach { st ->
                            FilterChip(selected = status == st, onClick = { status = st }, label = { Text(st) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onReplyTicket(t, reply, status)
                    replyTarget = null
                }) { Text("Send Reply") }
            },
            dismissButton = { TextButton(onClick = { replyTarget = null }) { Text("Cancel") } }
        )
    }

    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Store Health & Local Snapshot") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Database Engine: Room SQLite + DataStore Preferences (On-Device)")
                    Text("Catalog Products: $productsCount")
                    Text("Total Orders: $ordersCount")
                    Text("Registered Customers: ${users.count { it.role == UserRole.CUSTOMER.name }}")
                    Text("Free Shipping Threshold: ₹${settings.freeShippingThresholdInr}")
                    Text("Status: Healthy • Automatic transactional persistence active.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showBackupDialog = false }) { Text("Done") }
            }
        )
    }
}
