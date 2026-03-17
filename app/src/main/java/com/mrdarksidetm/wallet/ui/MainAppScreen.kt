package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import android.content.Context
import androidx.compose.foundation.shape.CircleShape
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
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountBalanceWallet, 
                            contentDescription = "App Logo", 
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp).size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Good evening",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                },
                actions = {
                    Icon(
                        Icons.Rounded.Star, 
                        contentDescription = "Premium", 
                        tint = Color(0xFFFFD700), 
                        modifier = Modifier.padding(end = 12.dp).size(28.dp)
                    )
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = itemLabels[index]) },
                        label = { Text(itemLabels[index]) },
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
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedItem == 0 || selectedItem == 1) {
                FloatingActionButton(
                    onClick = { 
                        viewModel.clearError()
                        if (selectedItem == 0) navController.navigate("add_transaction")
                        else navController.navigate("add_account")
                    }
                ) { 
                    Icon(
                        if (selectedItem == 0) Icons.Default.Add else Icons.Default.AccountBalanceWallet, 
                        contentDescription = "Add"
                    ) 
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { 
                    val scope = rememberCoroutineScope()
                    HomeScreen(
                        viewModel = viewModel,
                        onLoansClick = { navController.navigate("loans") },
                        onPlaceholderClick = { feature ->
                            scope.launch {
                                snackbarHostState.showSnackbar("$feature: Feature coming soon")
                            }
                        }
                    ) 
                }
                composable("accounts") { AccountsScreen(viewModel, snackbarHostState) }
                composable("reports") { ReportsScreen(viewModel) }
                composable(
                    "search",
                    enterTransition = { slideInVertically(initialOffsetY = { it }) },
                    exitTransition = { slideOutVertically(targetOffsetY = { it }) }
                ) { SearchScreen() }
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
                    SettingsScreen(onBack = { navController.popBackStack() })
                }
                composable("loans") {
                    LoanScreen(viewModel, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: WalletViewModel, onLoansClick: () -> Unit, onPlaceholderClick: (String) -> Unit) {
    val totalBalance by viewModel.totalBalance.collectAsState()
    val thisMonthIncome by viewModel.thisMonthIncome.collectAsState()
    val thisMonthExpense by viewModel.thisMonthExpense.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()

    androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            TotalBalanceCard(
                balance = totalBalance,
                income = thisMonthIncome,
                expense = thisMonthExpense
            )
        }
        
        item {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        item {
            val menuItems = listOf(
                "Budgets" to Icons.Default.PieChart,
                "Assets" to Icons.Default.AccountBalanceWallet,
                "Bill Splitter" to Icons.Default.Receipt,
                "Loans" to Icons.Default.CreditCard,
                "Goals" to Icons.Rounded.Star,
                "Labels*" to Icons.Default.List,
                "Analytics*" to Icons.Default.PieChart,
                "Recurring" to Icons.Default.List,
                "Categories*" to Icons.Default.List,
                "Weekly*" to Icons.Default.List,
                "Places*" to Icons.Default.List,
                "Person*" to Icons.Default.List,
                "Calendar heatmap" to Icons.Default.List,
                "Trend" to Icons.Default.List,
                "Recent txns*" to Icons.Default.List
            )

            Column(modifier = Modifier.padding(16.dp)) {
                val chunkedItems = menuItems.chunked(2)
                chunkedItems.forEachIndexed { index, rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        for (menuItem in rowItems) {
                            OverviewCard(
                                title = menuItem.first,
                                icon = menuItem.second,
                                mainCount = if (menuItem.first == "Loans" || menuItem.first == "Assets" || menuItem.first == "Budgets") "1" else "0",
                                mainLabel = menuItem.first.replace("*", ""),
                                bottomValue = "-",
                                progress = null,
                                onClick = { if (menuItem.first == "Loans") onLoansClick() else onPlaceholderClick(menuItem.first) },
                                modifier = Modifier.weight(1f).height(120.dp)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    if (index < chunkedItems.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        item {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(recentTransactions.take(10)) { transaction ->
            TransactionItem(transaction = transaction, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // padding for bottom bar
        }
    }
}

@Composable
fun SearchScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Search Transactions", style = MaterialTheme.typography.titleLarge)
            Text("Feature coming soon", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
