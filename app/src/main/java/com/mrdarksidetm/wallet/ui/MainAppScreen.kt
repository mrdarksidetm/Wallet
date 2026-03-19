package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrdarksidetm.wallet.data.AppDatabase
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val sharedPreferences = remember { context.getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE) }
    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModel.Factory(
            db.accountDao(),
            db.transactionDao(), 
            db.categoryDao(),
            db.personDao(),
            db.loanDao(),
            sharedPreferences
        )
    )

    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("home", "accounts", "reports", "search")
    val itemLabels = listOf("Home", "Accounts", "Reports", "Search")
    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.PieChart, Icons.Default.Search)
    
    val snackbarHostState = remember { SnackbarHostState() }
    val userName by viewModel.userName.collectAsState()
    val userPhotoPath by viewModel.userPhotoPath.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .clip(RoundedCornerShape(32.dp)),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 0.dp
            ) {
                val navItems = listOf("home", "accounts", "reports", "search")
                val navIcons = listOf(Icons.Default.Home, Icons.Default.AccountBalanceWallet, Icons.Default.PieChart, Icons.Default.Search)
                val navLabels = listOf("Home", "Accounts", "Reports", "Search")

                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(navIcons[index], contentDescription = navLabels[index]) },
                        label = { Text(navLabels[index], style = MaterialTheme.typography.labelSmall) },
                        selected = selectedItem == index,
                        onClick = { 
                            selectedItem = index 
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) { 
                                    saveState = true 
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (selectedItem == 0 || selectedItem == 1) {
                FloatingActionButton(
                    onClick = { 
                        viewModel.clearError()
                        if (selectedItem == 0) navController.navigate("add_transaction")
                        else navController.navigate("add_account")
                    },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        if (selectedItem == 0) Icons.Default.Add else Icons.Default.AccountBalanceWallet,
                        contentDescription = "Add"
                    )
                }
            }
        }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { 
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToSettings = { navController.navigate("settings") },
                        onNavigateToSubMenu = { title -> navController.navigate("home_submenu/$title") },
                        onLoansClick = { navController.navigate("loans") }
                    ) 
                }
                composable("accounts") {
                    AccountsScreen(viewModel = viewModel, snackbarHostState = snackbarHostState)
                }
                composable("reports") {
                    ReportsScreen(viewModel = viewModel)
                }
                composable("search") {
                    SearchScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                }
                composable("home_submenu/{title}") { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title") ?: "Menu"
                    HomeSubMenuScreen(title = title, onBack = { navController.popBackStack() })
                }
                composable("add_transaction") { 
                    AddTransactionScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) 
                }
                composable("add_account") {
                    AddAccountScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                }
                composable("profile") {
                    ProfileScreen(viewModel, onBack = { navController.popBackStack() }, onSettingsClick = { navController.navigate("settings") })
                }
                composable("settings") {
                    SettingsScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
                }
                composable("loans") {
                    LoanScreen(viewModel, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun HomeSubMenuScreen(title: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Sub-menu content for $title", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Search Transactions", style = MaterialTheme.typography.titleLarge)
            Text("Feature coming soon", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}
