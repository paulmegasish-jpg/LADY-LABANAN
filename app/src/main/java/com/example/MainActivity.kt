package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.local.SessionPreferences
import com.example.data.repository.StoreRepository
import com.example.data.service.NotificationHelper
import com.example.ui.screens.AccountAndOrdersScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CartAndCheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PolicyDetailScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepRose
import com.example.ui.theme.LadyLebananTheme
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.StoreViewModel

object Routes {
    const val HOME = "home"
    const val SHOP = "shop"
    const val BAG = "bag"
    const val WISHLIST = "wishlist"
    const val ACCOUNT = "account"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val ADMIN = "admin_dashboard"
    const val NOTIFICATIONS = "notifications"
    const val POLICY = "policy/{pageKey}"

    fun productDetail(productId: String) = "product_detail/$productId"
    fun policy(pageKey: String) = "policy/$pageKey"
}

private data class NavTabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.ensureChannel(this)
        setContent {
            LadyLebananTheme {
                LadyLebananApp()
            }
        }
    }
}

@Composable
fun LadyLebananApp() {
    val context = LocalContext.current.applicationContext
    val repository = remember {
        StoreRepository(
            appContext = context,
            db = AppDatabase.getInstance(context),
            prefs = SessionPreferences(context)
        )
    }
    val storeVm: StoreViewModel = viewModel(factory = StoreViewModel.Factory(repository))
    val adminVm: AdminViewModel = viewModel(factory = AdminViewModel.Factory(repository))

    val strings by storeVm.strings.collectAsStateWithLifecycle()
    val language by storeVm.language.collectAsStateWithLifecycle()
    val currentUser by storeVm.currentUser.collectAsStateWithLifecycle()
    val isGuest by storeVm.isGuestMode.collectAsStateWithLifecycle()
    val isAdmin by storeVm.isAdmin.collectAsStateWithLifecycle()
    val authError by storeVm.authError.collectAsStateWithLifecycle()
    val generatedOtp by storeVm.generatedOtp.collectAsStateWithLifecycle()
    val statusBanner by storeVm.statusBanner.collectAsStateWithLifecycle()
    val adminFeedback by adminVm.adminFeedback.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusBanner) {
        statusBanner?.let {
            snackbarHostState.showSnackbar(it)
            storeVm.clearStatusBanner()
        }
    }
    LaunchedEffect(adminFeedback) {
        adminFeedback?.let {
            snackbarHostState.showSnackbar(it)
            adminVm.clearFeedback()
        }
    }

    // Request POST_NOTIFICATIONS runtime permission cleanly on Android 13+
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    LaunchedEffect(currentUser, isGuest) {
        if ((currentUser != null || isGuest) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (currentUser == null && !isGuest) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { inner ->
            Box(modifier = Modifier.padding(inner)) {
                AuthScreen(
                    strings = strings,
                    authError = authError,
                    generatedOtp = generatedOtp,
                    onLoginCustomer = { email, pass -> storeVm.loginCustomer(email, pass) },
                    onRegisterCustomer = { name, email, phone, pass -> storeVm.registerCustomer(name, email, phone, pass) },
                    onSendOtp = { phone -> storeVm.sendMobileOtp(phone) },
                    onVerifyOtp = { phone, otp, name -> storeVm.verifyMobileOtp(phone, otp, name) },
                    onLoginAdmin = { email, pass -> storeVm.loginAdmin(email, pass) },
                    onContinueGuest = { storeVm.continueAsGuest() },
                    onDismissError = { storeVm.clearAuthError() }
                )
            }
        }
        return
    }

    val banners by storeVm.banners.collectAsStateWithLifecycle()
    val categories by storeVm.categories.collectAsStateWithLifecycle()
    val products by storeVm.products.collectAsStateWithLifecycle()
    val filteredProducts by storeVm.filteredProducts.collectAsStateWithLifecycle()
    val filterState by storeVm.filterState.collectAsStateWithLifecycle()
    val coupons by storeVm.coupons.collectAsStateWithLifecycle()
    val settings by storeVm.settings.collectAsStateWithLifecycle()
    val cartLines by storeVm.cartLines.collectAsStateWithLifecycle()
    val wishlistIds by storeVm.wishlistProductIds.collectAsStateWithLifecycle()
    val addresses by storeVm.addresses.collectAsStateWithLifecycle()
    val userOrders by storeVm.userOrders.collectAsStateWithLifecycle()
    val userTickets by storeVm.userTickets.collectAsStateWithLifecycle()
    val notifications by storeVm.notifications.collectAsStateWithLifecycle()
    val appliedCoupon by storeVm.appliedCoupon.collectAsStateWithLifecycle()
    val pinResult by storeVm.pinResult.collectAsStateWithLifecycle()
    val lastPlacedOrder by storeVm.lastPlacedOrder.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val cartBadgeCount = cartLines.sumOf { it.cartItem.quantity }
    val unreadNotifCount = notifications.count { !it.isRead }

    val topLevelTabs = buildList {
        add(NavTabItem(Routes.HOME, strings.navHome, Icons.Filled.Home, Icons.Outlined.Home))
        add(NavTabItem(Routes.SHOP, strings.navShop, Icons.Filled.Storefront, Icons.Outlined.Storefront))
        add(NavTabItem(Routes.BAG, strings.navBag, Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, cartBadgeCount))
        add(NavTabItem(Routes.WISHLIST, strings.navWishlist, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder))
        add(NavTabItem(Routes.ACCOUNT, strings.navAccount, Icons.Filled.Person, Icons.Outlined.Person))
        if (isAdmin) {
            add(NavTabItem(Routes.ADMIN, strings.navAdmin, Icons.Filled.AdminPanelSettings, Icons.Filled.AdminPanelSettings))
        }
    }
    val showMainChrome = currentRoute in topLevelTabs.map { it.route }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (showMainChrome && !useNavRail) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        topLevelTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (tab.badgeCount > 0) {
                                                Badge(containerColor = DeepRose) {
                                                    Text("${tab.badgeCount}", color = PureWhite)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label,
                                            tint = if (tab.route == Routes.ADMIN && selected) ChampagneGold else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                label = { Text(tab.label, maxLines = 1) },
                                modifier = Modifier.testTag("nav_tab_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showMainChrome && useNavRail) {
                    NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                        topLevelTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (tab.badgeCount > 0) {
                                                Badge(containerColor = DeepRose) {
                                                    Text("${tab.badgeCount}", color = PureWhite)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    }
                                },
                                label = { Text(tab.label) }
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(Routes.HOME) {
                        HomeScreen(
                            strings = strings,
                            currentLanguage = language,
                            banners = banners,
                            categories = categories,
                            products = products,
                            coupons = coupons,
                            wishlistIds = wishlistIds,
                            unreadNotificationCount = unreadNotifCount,
                            onLanguageChange = { storeVm.setLanguage(it) },
                            onOpenSearch = {
                                navController.navigate(Routes.SHOP)
                            },
                            onOpenCategory = { catName ->
                                storeVm.selectCategoryFilter(catName)
                                navController.navigate(Routes.SHOP)
                            },
                            onOpenProduct = { productId ->
                                navController.navigate(Routes.productDetail(productId))
                            },
                            onToggleWishlist = { productId ->
                                storeVm.toggleWishlist(productId) { storeVm.signOut() }
                            },
                            onOpenWishlist = { navController.navigate(Routes.WISHLIST) },
                            onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                            onOpenPolicyPage = { pageKey -> navController.navigate(Routes.policy(pageKey)) },
                            onCopyCoupon = { code ->
                                storeVm.applyCoupon(code, 5000)
                                navController.navigate(Routes.BAG)
                            }
                        )
                    }

                    composable(Routes.SHOP) {
                        ShopScreen(
                            strings = strings,
                            categories = categories,
                            products = filteredProducts,
                            filterState = filterState,
                            wishlistIds = wishlistIds,
                            onQueryChange = { storeVm.updateSearchQuery(it) },
                            onSelectCategory = { storeVm.selectCategoryFilter(it) },
                            onApplyFilters = { sz, maxPrice, sort -> storeVm.updateFilters(sz, maxPrice, sort) },
                            onResetFilters = { storeVm.resetFilters() },
                            onOpenProduct = { productId -> navController.navigate(Routes.productDetail(productId)) },
                            onToggleWishlist = { productId ->
                                storeVm.toggleWishlist(productId) { storeVm.signOut() }
                            }
                        )
                    }

                    composable(Routes.BAG) {
                        CartAndCheckoutScreen(
                            strings = strings,
                            isSignedIn = currentUser != null,
                            cartLines = cartLines,
                            addresses = addresses,
                            coupons = coupons,
                            appliedCoupon = appliedCoupon,
                            settings = settings,
                            lastPlacedOrder = lastPlacedOrder,
                            onRequireSignIn = { storeVm.signOut() },
                            onUpdateQuantity = { id, qty -> storeVm.updateCartQuantity(id, qty) },
                            onApplyCoupon = { code, sub -> storeVm.applyCoupon(code, sub) },
                            onRemoveCoupon = { storeVm.removeCoupon() },
                            onSaveAddress = { n, p, l1, l2, c, s, pin, lbl, def ->
                                storeVm.saveAddress(n, p, l1, l2, c, s, pin, lbl, def)
                            },
                            onPlaceOrder = { addr, payMethod, isCod, speed ->
                                storeVm.placeOrder(addr, payMethod, isCod, speed) {}
                            },
                            onDismissOrderSuccess = {
                                storeVm.clearLastPlacedOrder()
                                navController.navigate(Routes.ACCOUNT)
                            },
                            onExploreShop = { navController.navigate(Routes.SHOP) }
                        )
                    }

                    composable(Routes.WISHLIST) {
                        val wishlistedProducts = products.filter { wishlistIds.contains(it.id) }
                        WishlistScreen(
                            strings = strings,
                            wishlistedProducts = wishlistedProducts,
                            onOpenProduct = { navController.navigate(Routes.productDetail(it)) },
                            onToggleWishlist = { pid -> storeVm.toggleWishlist(pid) { storeVm.signOut() } },
                            onExploreShop = { navController.navigate(Routes.SHOP) }
                        )
                    }

                    composable(Routes.ACCOUNT) {
                        AccountAndOrdersScreen(
                            strings = strings,
                            currentUser = currentUser,
                            currentLanguage = language,
                            orders = userOrders,
                            addresses = addresses,
                            tickets = userTickets,
                            onLanguageChange = { storeVm.setLanguage(it) },
                            onSignInClick = { storeVm.signOut() },
                            onSignOutClick = { storeVm.signOut() },
                            onOpenAdminDashboard = {
                                if (isAdmin) navController.navigate(Routes.ADMIN)
                            },
                            onSaveAddress = { n, p, l1, l2, c, s, pin, lbl, def ->
                                storeVm.saveAddress(n, p, l1, l2, c, s, pin, lbl, def)
                            },
                            onDeleteAddress = { storeVm.deleteAddress(it) },
                            onRequestCancel = { ord, reason -> storeVm.requestOrderCancellation(ord, reason) },
                            onRequestReturn = { ord, reason -> storeVm.requestOrderReturn(ord, reason) },
                            onSubmitTicket = { subj, msg -> storeVm.submitSupportTicket(subj, msg) },
                            onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                            onOpenPolicyPage = { pageKey -> navController.navigate(Routes.policy(pageKey)) }
                        )
                    }

                    composable(
                        route = Routes.PRODUCT_DETAIL,
                        arguments = listOf(navArgument("productId") { type = NavType.StringType })
                    ) { entry ->
                        val productId = entry.arguments?.getString("productId") ?: ""
                        val product = products.firstOrNull { it.id == productId }
                        val reviews by storeVm.observeProductReviews(productId).collectAsStateWithLifecycle(initialValue = emptyList())
                        if (product != null) {
                            ProductDetailScreen(
                                strings = strings,
                                product = product,
                                reviews = reviews,
                                isWishlisted = wishlistIds.contains(product.id),
                                pinResult = pinResult,
                                onBack = { navController.popBackStack() },
                                onCheckPin = { storeVm.checkPinCode(it) },
                                onAddToCart = { size, color ->
                                    storeVm.addToCart(product, size, color) { storeVm.signOut() }
                                },
                                onToggleWishlist = { storeVm.toggleWishlist(product.id) { storeVm.signOut() } },
                                onSubmitReview = { rating, comment -> storeVm.submitReview(product.id, rating, comment) }
                            )
                        }
                    }

                    composable(Routes.ADMIN) {
                        val analytics by adminVm.analytics.collectAsStateWithLifecycle()
                        val allOrders by adminVm.allOrders.collectAsStateWithLifecycle()
                        val allUsers by adminVm.allUsers.collectAsStateWithLifecycle()
                        val allTickets by adminVm.allTickets.collectAsStateWithLifecycle()
                        AdminDashboardScreen(
                            currentUser = currentUser,
                            analytics = analytics,
                            products = products,
                            categories = categories,
                            banners = banners,
                            coupons = coupons,
                            orders = allOrders,
                            users = allUsers,
                            tickets = allTickets,
                            settings = settings,
                            onBack = { navController.popBackStack() },
                            onSaveProduct = { id, t, cat, desc, fab, pr, mrp, sz, col, stk, img, vid, vTitle, feat, trend, fest ->
                                adminVm.saveProduct(id, t, cat, desc, fab, pr, mrp, sz, col, stk, img, vid, vTitle, feat, trend, fest)
                            },
                            onDeleteProduct = { adminVm.deleteProduct(it) },
                            onUpdateOrderStatus = { ord, st, cour, awb -> adminVm.updateOrderStatus(ord, st, cour, awb) },
                            onSaveCoupon = { c, t, f, pct, max, min, act -> adminVm.saveCoupon(c, t, f, pct, max, min, act) },
                            onDeleteCoupon = { adminVm.deleteCoupon(it) },
                            onSaveBanner = { id, t, sub, cta, tgt, img, bdg -> adminVm.saveBanner(id, t, sub, cta, tgt, img, bdg) },
                            onDeleteBanner = { adminVm.deleteBanner(it) },
                            onSaveCategory = { id, n, sub, img -> adminVm.saveCategory(id, n, sub, img) },
                            onDeleteCategory = { adminVm.deleteCategory(it) },
                            onReplyTicket = { tkt, rep, st -> adminVm.replyTicket(tkt, rep, st) },
                            onSendBroadcast = { t, b -> adminVm.sendPushBroadcast(t, b) },
                            onSaveSettings = { adminVm.updateSettings(it) }
                        )
                    }

                    composable(Routes.NOTIFICATIONS) {
                        NotificationsScreen(
                            strings = strings,
                            notifications = notifications,
                            onMarkRead = { storeVm.markNotificationRead(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Routes.POLICY,
                        arguments = listOf(navArgument("pageKey") { type = NavType.StringType })
                    ) { entry ->
                        val pageKey = entry.arguments?.getString("pageKey") ?: "about"
                        PolicyDetailScreen(
                            strings = strings,
                            pageKey = pageKey,
                            settings = settings,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
