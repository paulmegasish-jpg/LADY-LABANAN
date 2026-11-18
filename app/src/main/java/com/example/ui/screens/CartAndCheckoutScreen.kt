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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AddressEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CouponEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PaymentMethod
import com.example.data.service.PinCodeService
import com.example.i18n.AppStrings
import com.example.ui.components.BrandMediaImage
import com.example.ui.components.GoldHairlineDivider
import com.example.ui.components.formatInr
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneLight
import com.example.ui.theme.DeepRose
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.CartLineItem

@Composable
fun CartAndCheckoutScreen(
    strings: AppStrings,
    isSignedIn: Boolean,
    cartLines: List<CartLineItem>,
    addresses: List<AddressEntity>,
    coupons: List<CouponEntity>,
    appliedCoupon: CouponEntity?,
    settings: AppSettingsEntity,
    lastPlacedOrder: OrderEntity?,
    onRequireSignIn: () -> Unit,
    onUpdateQuantity: (Long, Int) -> Unit,
    onApplyCoupon: (String, Int) -> Unit,
    onRemoveCoupon: () -> Unit,
    onSaveAddress: (String, String, String, String, String, String, String, String, Boolean) -> Unit,
    onPlaceOrder: (AddressEntity, String, Boolean, String) -> Unit,
    onDismissOrderSuccess: () -> Unit,
    onExploreShop: () -> Unit
) {
    var couponInput by remember { mutableStateOf("") }
    var showCheckoutSheet by remember { mutableStateOf(false) }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    val subtotal = cartLines.sumOf { it.product.priceInr * it.cartItem.quantity }
    val discount = if (appliedCoupon != null && subtotal >= appliedCoupon.minOrderInr) {
        ((subtotal * appliedCoupon.discountPercent) / 100).coerceAtMost(appliedCoupon.maxDiscountInr)
    } else 0
    val standardShipping = if (subtotal >= settings.freeShippingThresholdInr || subtotal == 0) 0 else settings.standardShippingFeeInr

    if (!isSignedIn) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = DeepRose, modifier = Modifier.size(60.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Sign in to access your Shopping Bag", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Verify with Mobile OTP or Email to manage your cart, saved Indian addresses, and order tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRequireSignIn, modifier = Modifier.testTag("cart_signin_button")) {
                    Text(strings.signIn)
                }
            }
        }
        return
    }

    if (cartLines.isEmpty() && lastPlacedOrder == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(14.dp))
                Text(strings.emptyBagTitle, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    strings.emptyBagSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(onClick = onExploreShop) {
                    Text("Explore Collections")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Shopping Bag (${cartLines.sumOf { it.cartItem.quantity }})",
                style = MaterialTheme.typography.headlineLarge
            )
            if (subtotal < settings.freeShippingThresholdInr && subtotal > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add ${formatInr(settings.freeShippingThresholdInr - subtotal)} more for complimentary Pan-India shipping!",
                    style = MaterialTheme.typography.labelMedium,
                    color = DeepRose
                )
            }
        }

        items(cartLines, key = { it.cartItem.id }) { line ->
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
                        imageKey = line.product.imageKey,
                        contentDescription = line.product.title,
                        modifier = Modifier
                            .size(width = 84.dp, height = 104.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(line.product.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Size: ${line.cartItem.selectedSize} • ${line.cartItem.selectedColor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formatInr(line.product.priceInr * line.cartItem.quantity),
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepRose,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateQuantity(line.cartItem.id, line.cartItem.quantity - 1) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${line.cartItem.quantity}",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { onUpdateQuantity(line.cartItem.id, line.cartItem.quantity + 1) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { onUpdateQuantity(line.cartItem.id, 0) }) {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "Remove item", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Coupon section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = DeepRose)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apply Boutique / Festival Coupon", style = MaterialTheme.typography.titleSmall)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (appliedCoupon != null) {
                        Surface(color = ChampagneLight, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${appliedCoupon.code} applied (-${formatInr(discount)})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ObsidianBlack
                                )
                                TextButton(onClick = onRemoveCoupon) { Text("Remove") }
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it.uppercase() },
                                placeholder = { Text("Enter code e.g. DIWALI20") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { onApplyCoupon(couponInput, subtotal) },
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text(strings.applyCoupon)
                            }
                        }
                        if (coupons.any { it.isActive }) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(coupons.filter { it.isActive }) { c ->
                                    FilterChip(
                                        selected = false,
                                        onClick = {
                                            couponInput = c.code
                                            onApplyCoupon(c.code, subtotal)
                                        },
                                        label = { Text("${c.code} (${c.discountPercent}% off)") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Price Summary + Checkout Button
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriceRow(strings.subtotal, formatInr(subtotal))
                    if (discount > 0) {
                        PriceRow(strings.discount, "-${formatInr(discount)}", highlightColor = EmeraldSuccess)
                    }
                    PriceRow(
                        strings.shippingFee,
                        if (standardShipping == 0) strings.freeText else formatInr(standardShipping),
                        highlightColor = if (standardShipping == 0) EmeraldSuccess else null
                    )
                    GoldHairlineDivider(paddingVertical = 4.dp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(strings.totalAmount, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = formatInr((subtotal - discount + standardShipping).coerceAtLeast(0)),
                            style = MaterialTheme.typography.titleLarge,
                            color = DeepRose,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showCheckoutSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("proceed_to_checkout_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(strings.checkout, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }

    if (showCheckoutSheet) {
        CheckoutDialog(
            strings = strings,
            subtotal = subtotal,
            discount = discount,
            settings = settings,
            addresses = addresses,
            onDismiss = { showCheckoutSheet = false },
            onAddNewAddress = { showAddAddressDialog = true },
            onConfirmOrder = { addr, payMethod, isCod, speed ->
                showCheckoutSheet = false
                onPlaceOrder(addr, payMethod, isCod, speed)
            }
        )
    }

    if (showAddAddressDialog) {
        AddAddressDialog(
            onDismiss = { showAddAddressDialog = false },
            onSave = { name, phone, l1, l2, city, state, pin, label ->
                onSaveAddress(name, phone, l1, l2, city, state, pin, label, true)
                showAddAddressDialog = false
            }
        )
    }

    if (lastPlacedOrder != null) {
        AlertDialog(
            onDismissRequest = onDismissOrderSuccess,
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(48.dp)) },
            title = { Text(strings.orderConfirmed, style = MaterialTheme.typography.headlineMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Thank you for choosing LADY LEBANAN.", fontWeight = FontWeight.SemiBold)
                    Text("Order ID: ${lastPlacedOrder.id}")
                    Text("Courier: ${lastPlacedOrder.courierName}")
                    Text("Tracking AWB: ${lastPlacedOrder.trackingNumber}")
                    Text("Payment: ${lastPlacedOrder.paymentMethod} (${lastPlacedOrder.paymentStatus})")
                    Text("Total Paid: ${formatInr(lastPlacedOrder.totalInr)}")
                }
            },
            confirmButton = {
                Button(onClick = onDismissOrderSuccess) { Text("View My Orders") }
            }
        )
    }
}

@Composable
private fun PriceRow(label: String, value: String, highlightColor: androidx.compose.ui.graphics.Color? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = highlightColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CheckoutDialog(
    strings: AppStrings,
    subtotal: Int,
    discount: Int,
    settings: AppSettingsEntity,
    addresses: List<AddressEntity>,
    onDismiss: () -> Unit,
    onAddNewAddress: () -> Unit,
    onConfirmOrder: (AddressEntity, String, Boolean, String) -> Unit
) {
    var selectedAddrId by remember(addresses) { mutableStateOf(addresses.firstOrNull()?.id ?: "") }
    var deliverySpeed by remember { mutableStateOf("Standard") }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.UPI) }
    var upiVpa by remember { mutableStateOf("ananya@okicici") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf("HDFC Bank") }
    var bankMenuExpanded by remember { mutableStateOf(false) }

    val selectedAddr = addresses.firstOrNull { it.id == selectedAddrId } ?: addresses.firstOrNull()
    val isCod = selectedPayment == PaymentMethod.COD
    val shippingFee = when {
        deliverySpeed == "Express" -> settings.expressShippingFeeInr
        subtotal >= settings.freeShippingThresholdInr -> 0
        else -> settings.standardShippingFeeInr
    } + if (isCod) settings.codFeeInr else 0
    val finalTotal = (subtotal - discount + shippingFee).coerceAtLeast(0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Indian Express Checkout", style = MaterialTheme.typography.headlineMedium) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Delivery Address", style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = onAddNewAddress) { Text("+ Add New") }
                }
                if (addresses.isEmpty()) {
                    OutlinedButton(onClick = onAddNewAddress, modifier = Modifier.fillMaxWidth()) {
                        Text("Add Delivery Address (PIN Required)")
                    }
                } else {
                    addresses.forEach { addr ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAddrId = addr.id },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedAddr?.id == addr.id) DeepRose else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = selectedAddr?.id == addr.id, onClick = { selectedAddrId = addr.id })
                                Column {
                                    Text("${addr.fullName} (${addr.label})", style = MaterialTheme.typography.labelLarge)
                                    Text("${addr.line1}, ${addr.city}, ${addr.state} - ${addr.pinCode}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                Text("Delivery Speed", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = deliverySpeed == "Standard",
                        onClick = { deliverySpeed = "Standard" },
                        label = {
                            val feeLabel = if (subtotal >= settings.freeShippingThresholdInr) "Free" else formatInr(settings.standardShippingFeeInr)
                            Text("Standard ($feeLabel)")
                        }
                    )
                    FilterChip(
                        selected = deliverySpeed == "Express",
                        onClick = { deliverySpeed = "Express" },
                        label = { Text("Express Air (${formatInr(settings.expressShippingFeeInr)})") }
                    )
                }

                Text(strings.paymentMethodTitle, style = MaterialTheme.typography.titleSmall)
                PaymentMethod.entries.forEach { method ->
                    val chosen = selectedPayment == method
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPayment = method }
                            .testTag("pay_method_${method.name}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (chosen) ChampagneLight.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (chosen) DeepRose else MaterialTheme.colorScheme.outline)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = chosen, onClick = { selectedPayment = method })
                            Icon(
                                imageVector = when (method) {
                                    PaymentMethod.UPI, PaymentMethod.GOOGLE_PAY, PaymentMethod.PHONE_PE -> Icons.Filled.QrCode2
                                    PaymentMethod.CARD -> Icons.Filled.CreditCard
                                    PaymentMethod.NET_BANKING -> Icons.Filled.AccountBalance
                                    PaymentMethod.COD -> Icons.Filled.Payments
                                },
                                contentDescription = null,
                                tint = DeepRose,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(method.label, style = MaterialTheme.typography.labelLarge)
                                Text(method.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                when (selectedPayment) {
                    PaymentMethod.UPI, PaymentMethod.GOOGLE_PAY, PaymentMethod.PHONE_PE -> {
                        OutlinedTextField(
                            value = upiVpa,
                            onValueChange = { upiVpa = it },
                            label = { Text("UPI ID / VPA") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PaymentMethod.CARD -> {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 16) cardNumber = it.filter { ch -> ch.isDigit() } },
                            label = { Text("16-Digit Card Number (Visa/RuPay/MC)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { if (it.length <= 5) cardExpiry = it },
                            label = { Text("Expiry MM/YY") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PaymentMethod.NET_BANKING -> {
                        Box {
                            OutlinedButton(onClick = { bankMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Bank: $selectedBank")
                            }
                            DropdownMenu(expanded = bankMenuExpanded, onDismissRequest = { bankMenuExpanded = false }) {
                                listOf("HDFC Bank", "ICICI Bank", "State Bank of India", "Axis Bank", "Kotak Mahindra Bank").forEach { bank ->
                                    DropdownMenuItem(text = { Text(bank) }, onClick = {
                                        selectedBank = bank
                                        bankMenuExpanded = false
                                    })
                                }
                            }
                        }
                    }
                    PaymentMethod.COD -> {
                        Text(
                            text = "COD handling fee: ${formatInr(settings.codFeeInr)} applied.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DeepRose
                        )
                    }
                }

                Surface(color = ObsidianBlack, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Payable Total", color = PureWhite, style = MaterialTheme.typography.titleSmall)
                        Text(formatInr(finalTotal), color = ChampagneGold, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val addr = selectedAddr ?: return@Button
                    val detailLabel = when (selectedPayment) {
                        PaymentMethod.NET_BANKING -> "${selectedPayment.label} ($selectedBank)"
                        else -> selectedPayment.label
                    }
                    onConfirmOrder(addr, detailLabel, isCod, deliverySpeed)
                },
                enabled = selectedAddr != null,
                colors = ButtonDefaults.buttonColors(containerColor = DeepRose),
                modifier = Modifier.testTag("confirm_pay_order_button")
            ) {
                Text("${strings.placeOrder} (${formatInr(finalTotal)})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddAddressDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var line1 by remember { mutableStateOf("") }
    var line2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("West Bengal") }
    var pinCode by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("Home") }
    var stateExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Pan-India Delivery Address") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Recipient Full Name") }, singleLine = true)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10) phone = it.filter { ch -> ch.isDigit() } },
                    label = { Text("10-Digit Mobile") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                OutlinedTextField(value = line1, onValueChange = { line1 = it }, label = { Text("Flat / House No., Building, Street") }, singleLine = true)
                OutlinedTextField(value = line2, onValueChange = { line2 = it }, label = { Text("Landmark / Area (Optional)") }, singleLine = true)
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City / District") }, singleLine = true)
                Box {
                    OutlinedButton(onClick = { stateExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("State: $state")
                    }
                    DropdownMenu(expanded = stateExpanded, onDismissRequest = { stateExpanded = false }) {
                        PinCodeService.indianStates.forEach { st ->
                            DropdownMenuItem(text = { Text(st) }, onClick = {
                                state = st
                                stateExpanded = false
                            })
                        }
                    }
                }
                OutlinedTextField(
                    value = pinCode,
                    onValueChange = { if (it.length <= 6) pinCode = it.filter { ch -> ch.isDigit() } },
                    label = { Text("6-Digit PIN Code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Home", "Work", "Boutique").forEach { tag ->
                        FilterChip(selected = label == tag, onClick = { label = tag }, label = { Text(tag) })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(fullName, phone, line1, line2, city, state, pinCode, label) },
                enabled = fullName.isNotBlank() && phone.length == 10 && line1.isNotBlank() && city.isNotBlank() && pinCode.length == 6
            ) {
                Text("Save Address")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
