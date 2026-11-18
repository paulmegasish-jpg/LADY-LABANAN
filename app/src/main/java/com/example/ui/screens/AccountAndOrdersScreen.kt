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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AddressEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.SupportTicketEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.GoldHairlineDivider
import com.example.ui.components.formatInr
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneLight
import com.example.ui.theme.DeepRose
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite

@Composable
fun AccountAndOrdersScreen(
    strings: AppStrings,
    currentUser: UserEntity?,
    currentLanguage: AppLanguage,
    orders: List<OrderEntity>,
    addresses: List<AddressEntity>,
    tickets: List<SupportTicketEntity>,
    onLanguageChange: (AppLanguage) -> Unit,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onSaveAddress: (String, String, String, String, String, String, String, String, Boolean) -> Unit,
    onDeleteAddress: (String) -> Unit,
    onRequestCancel: (OrderEntity, String) -> Unit,
    onRequestReturn: (OrderEntity, String) -> Unit,
    onSubmitTicket: (String, String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPolicyPage: (String) -> Unit
) {
    var showAddAddress by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var cancelTargetOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var returnTargetOrder by remember { mutableStateOf<OrderEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianBlack),
                border = BorderStroke(1.dp, ChampagneGold)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(ChampagneGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.name ?: "Guest Patron",
                                style = MaterialTheme.typography.titleLarge,
                                color = PureWhite
                            )
                            Text(
                                text = if (currentUser != null) "${currentUser.email} • +91 ${currentUser.phone}" else "Sign in for orders, wishlist & pan-India tracking",
                                style = MaterialTheme.typography.bodySmall,
                                color = ChampagneGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (currentUser == null) {
                            Button(
                                onClick = onSignInClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(strings.signIn)
                            }
                        } else {
                            if (currentUser.role == UserRole.ADMIN.name) {
                                Button(
                                    onClick = onOpenAdminDashboard,
                                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("open_admin_dashboard_button")
                                ) {
                                    Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Admin Control")
                                }
                            }
                            OutlinedButton(
                                onClick = onSignOutClick,
                                border = BorderStroke(1.dp, ChampagneGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("account_sign_out_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.signOut)
                            }
                        }
                    }
                }
            }
        }

        // 2. Language Switcher (English, Hindi, Bengali)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Language, contentDescription = null, tint = DeepRose)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Language / भाषा / ভাষা", style = MaterialTheme.typography.titleSmall)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.entries.forEach { lang ->
                            FilterChip(
                                selected = currentLanguage == lang,
                                onClick = { onLanguageChange(lang) },
                                label = { Text(lang.nativeLabel) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Actions: Notifications & Concierge Support
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.notificationsTitle)
                }
                OutlinedButton(
                    onClick = { showSupportDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.HeadsetMic, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Support")
                }
            }
        }

        // 4. Orders & Pan-India Tracking
        if (currentUser != null) {
            item {
                Text(strings.myOrders, style = MaterialTheme.typography.headlineMedium)
            }
            if (orders.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "No orders placed yet. Your luxury orders and AWB tracking will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    OrderTrackingCard(
                        order = order,
                        onCancelClick = { cancelTargetOrder = order },
                        onReturnClick = { returnTargetOrder = order }
                    )
                }
            }

            // 5. Saved Delivery Addresses
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.savedAddresses, style = MaterialTheme.typography.headlineMedium)
                    TextButton(onClick = { showAddAddress = true }) { Text("+ Add New") }
                }
            }
            items(addresses, key = { it.id }) { addr ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = DeepRose)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${addr.fullName} • ${addr.label}", style = MaterialTheme.typography.titleSmall)
                            Text("${addr.line1}, ${addr.city}, ${addr.state} - ${addr.pinCode}", style = MaterialTheme.typography.bodySmall)
                            Text("+91 ${addr.phone}", style = MaterialTheme.typography.labelSmall, color = DeepRose)
                        }
                        IconButton(onClick = { onDeleteAddress(addr.id) }) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete address", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // 6. Support Tickets History
            if (tickets.isNotEmpty()) {
                item {
                    Text(strings.customerSupport, style = MaterialTheme.typography.headlineMedium)
                }
                items(tickets, key = { it.id }) { ticket ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${ticket.id}: ${ticket.subject}", style = MaterialTheme.typography.titleSmall)
                                Surface(color = ChampagneLight, shape = RoundedCornerShape(50)) {
                                    Text(ticket.status, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(ticket.message, style = MaterialTheme.typography.bodySmall)
                            if (ticket.adminReply.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(color = ChampagneLight.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Text("Atelier Concierge: ${ticket.adminReply}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Store & Policy Pages
        item {
            GoldHairlineDivider(paddingVertical = 6.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Policy, contentDescription = null, tint = DeepRose)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.storePoliciesTitle, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            val pages = listOf(
                "about" to strings.aboutUs,
                "contact" to strings.contactUs,
                "privacy" to strings.privacyPolicy,
                "terms" to strings.termsConditions,
                "returns" to strings.returnPolicy,
                "shipping" to strings.shippingPolicy
            )
            pages.forEach { (key, title) ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { onOpenPolicyPage(key) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
                }
            }
        }
    }

    if (showAddAddress) {
        AddAddressDialog(
            onDismiss = { showAddAddress = false },
            onSave = { name, phone, l1, l2, city, state, pin, label ->
                onSaveAddress(name, phone, l1, l2, city, state, pin, label, true)
                showAddAddress = false
            }
        )
    }

    if (showSupportDialog) {
        var subject by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("Contact Lady Lebanan Concierge") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject / Order ID") }, singleLine = true)
                    OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("How can we assist you?") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitTicket(subject, message)
                        showSupportDialog = false
                    },
                    enabled = currentUser != null && subject.isNotBlank() && message.isNotBlank()
                ) { Text("Send") }
            },
            dismissButton = {
                TextButton(onClick = { showSupportDialog = false }) { Text("Close") }
            }
        )
    }

    cancelTargetOrder?.let { target ->
        var reason by remember { mutableStateOf("Ordered by mistake / Size change") }
        AlertDialog(
            onDismissRequest = { cancelTargetOrder = null },
            title = { Text("Cancel Order ${target.id}?") },
            text = {
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason for cancellation") })
            },
            confirmButton = {
                Button(onClick = {
                    onRequestCancel(target, reason)
                    cancelTargetOrder = null
                }) { Text("Confirm Cancel") }
            },
            dismissButton = { TextButton(onClick = { cancelTargetOrder = null }) { Text("Keep Order") } }
        )
    }

    returnTargetOrder?.let { target ->
        var reason by remember { mutableStateOf("Size fit issue / Exchange requested") }
        AlertDialog(
            onDismissRequest = { returnTargetOrder = null },
            title = { Text("Request Return & Refund (${target.id})") },
            text = {
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason for return") })
            },
            confirmButton = {
                Button(onClick = {
                    onRequestReturn(target, reason)
                    returnTargetOrder = null
                }) { Text("Submit Return") }
            },
            dismissButton = { TextButton(onClick = { returnTargetOrder = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun OrderTrackingCard(
    order: OrderEntity,
    onCancelClick: () -> Unit,
    onReturnClick: () -> Unit
) {
    val status = runCatching { OrderStatus.valueOf(order.orderStatus) }.getOrDefault(OrderStatus.PLACED)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.9.dp, ChampagneGold.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    color = if (status == OrderStatus.CANCELLED) MaterialTheme.colorScheme.errorContainer else ChampagneLight,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = status.displayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = ObsidianBlack,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Text(order.itemsSummary, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Total: ${formatInr(order.totalInr)} • ${order.paymentMethod} (${order.paymentStatus})",
                style = MaterialTheme.typography.labelMedium,
                color = DeepRose
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${order.courierName} • AWB: ${order.trackingNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (status.stepIndex in 0..5) {
                val steps = listOf("Placed", "Confirmed", "Packed", "Shipped", "Out", "Delivered")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    steps.forEachIndexed { idx, label ->
                        val reached = idx <= status.stepIndex
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = label,
                                tint = if (reached) EmeraldSuccess else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(label, style = MaterialTheme.typography.labelSmall, color = if (reached) ObsidianBlack else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (order.refundStatus != "None") {
                Text("Refund Status: ${order.refundStatus}", style = MaterialTheme.typography.labelMedium, color = EmeraldSuccess)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (status in listOf(OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PACKED)) {
                    OutlinedButton(onClick = onCancelClick, modifier = Modifier.weight(1f)) {
                        Text("Cancel Order")
                    }
                }
                if (status == OrderStatus.DELIVERED) {
                    OutlinedButton(onClick = onReturnClick, modifier = Modifier.weight(1f)) {
                        Text("Return & Refund")
                    }
                }
            }
        }
    }
}
