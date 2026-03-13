package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModel.Factory(db.transactionDao())
    )

    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("home", "accounts", "reports", "search")
    val itemLabels = listOf("Home", "Accounts", "Reports", "Search")
    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.PieChart, Icons.Default.Search)
    
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Good evening",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "User",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "U",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Premium",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Premium",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
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
            NavigationBar {
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
            FloatingActionButton(
                onClick = { 
                    viewModel.clearError()
                    navController.navigate("add_transaction")
                }
            ) { 
                Icon(Icons.Default.Add, contentDescription = "Add") 
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(viewModel) }
                composable("accounts") { AccountsScreen(viewModel, snackbarHostState) }
                composable("reports") { ReportsScreen(viewModel) }
                composable("search") { SearchScreen() }
                composable("add_transaction") { AddTransactionScreen(navController, viewModel, snackbarHostState) }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: WalletViewModel) {
    val totalBalance by viewModel.totalBalance.collectAsState()
    val thisMonthIncome by viewModel.thisMonthIncome.collectAsState()
    val thisMonthExpense by viewModel.thisMonthExpense.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TotalBalanceCard(
            balance = totalBalance,
            income = thisMonthIncome,
            expense = thisMonthExpense
        )
        
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            item {
                OverviewCard(
                    title = "Budgets",
                    icon = Icons.Default.PieChart,
                    mainCount = "1",
                    mainLabel = "Budget",
                    bottomValue = "₹500.00 left",
                    progress = 0.3f
                )
            }
            item {
                OverviewCard(
                    title = "Assets",
                    icon = Icons.Default.AccountBalanceWallet,
                    mainCount = "2",
                    mainLabel = "Assets",
                    bottomValue = "₹1,000.00 total",
                    progress = null
                )
            }
            item {
                OverviewCard(
                    title = "Bill Splitter",
                    icon = Icons.Default.Receipt,
                    mainCount = "0",
                    mainLabel = "Bills",
                    bottomValue = "₹0.00 owed",
                    progress = 0.0f
                )
            }
            item {
                OverviewCard(
                    title = "Loans",
                    icon = Icons.Default.CreditCard,
                    mainCount = "0",
                    mainLabel = "Loans",
                    bottomValue = "₹0.00 left",
                    progress = null
                )
            }
        }
    }
}



@Composable
fun SearchScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Search Screen Content")
    }
}
