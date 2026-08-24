package com.darkside.wallet.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darkside.wallet.data.AppDatabase
import com.darkside.wallet.data.domain.*
import com.darkside.wallet.ui.navigation.WalletNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val sharedPreferences = remember { context.getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE) }

    // Repositories
    val accountRepository = remember { AccountRepository(db.accountDao()) }
    val transactionRepository = remember { TransactionRepository(db.transactionDao()) }
    val categoryRepository = remember { CategoryRepository(db.categoryDao()) }
    val personRepository = remember { PersonRepository(db.personDao()) }
    val loanRepository = remember { LoanRepository(db.loanDao()) }
    val budgetRepository = remember { BudgetRepository(db.budgetDao()) }
    val goalRepository = remember { GoalRepository(db.goalDao()) }
    val recurringRepository = remember { RecurringRepository(db.recurringDao()) }
    val labelRepository = remember { LabelRepository(db.labelDao()) }

    // Services
    val transactionService = remember {
        TransactionService(db, db.transactionDao(), db.accountDao())
    }

    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModel.Factory(
            accountRepository,
            transactionRepository,
            categoryRepository,
            personRepository,
            loanRepository,
            budgetRepository,
            goalRepository,
            recurringRepository,
            labelRepository,
            transactionService,
            sharedPreferences
        )
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }

    val isDynamicToolbarEnabled by viewModel.isDynamicToolbarEnabled.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            val showBottomBar = currentDestination in listOf("home", "accounts", "reports", "people", "search") 
            if (showBottomBar && !isDynamicToolbarEnabled) {
                NavigationBar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    tonalElevation = 0.dp
                ) {
                    val navItems = listOf("home", "accounts", "reports", "people", "search")
                    val navIcons = listOf(Icons.Default.Home, Icons.Default.AccountBalanceWallet, Icons.Default.PieChart, Icons.Default.Group, Icons.Default.Search)
                    val navLabels = listOf("Home", "Accounts", "Reports", "People", "Search")

                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(navIcons[index], contentDescription = navLabels[index]) },
                            label = { Text(navLabels[index], style = MaterialTheme.typography.labelSmall) },    
                            selected = currentDestination == item,
                            onClick = {
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
            }
        },
        floatingActionButton = {
            val showFab = currentDestination in listOf("home", "accounts")
            val isExpressiveActive = isDynamicToolbarEnabled && currentDestination in listOf("home", "accounts", "reports", "people", "search")

            if (showFab && !isDynamicToolbarEnabled) {
                FloatingActionButton(
                    onClick = {
                        viewModel.clearError()
                        if (currentDestination == "home") navController.navigate("add_transaction")
                        else navController.navigate("add_account")
                    },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        if (currentDestination == "home") Icons.Default.Add else Icons.Default.AccountBalanceWallet,
                        contentDescription = "Add"
                    )
                }
            } else if (isExpressiveActive) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val navItems = listOf("home", "accounts", "reports", "people", "search")
                        val navIcons = listOf(Icons.Default.Home, Icons.Default.AccountBalanceWallet, Icons.Default.PieChart, Icons.Default.Group, Icons.Default.Search)

                        navItems.forEachIndexed { index, item ->
                            IconButton(
                                onClick = {
                                    navController.navigate(item) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            ) {
                                Icon(
                                    navIcons[index],
                                    contentDescription = null,
                                    tint = if (currentDestination == item) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (currentDestination in listOf("home", "accounts")) {
                            FloatingActionButton(
                                onClick = {
                                    viewModel.clearError()
                                    if (currentDestination == "home") navController.navigate("add_transaction")
                                    else navController.navigate("add_account")
                                },
                                shape = RoundedCornerShape(20.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                            ) {
                                Icon(
                                    if (currentDestination == "home") Icons.Default.Add else Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Add"
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            WalletNavGraph(
                navController = navController,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}
