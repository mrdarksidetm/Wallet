package com.darkside.wallet.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darkside.wallet.data.AppDatabase
import com.darkside.wallet.data.domain.*
import com.darkside.wallet.ui.navigation.PaisaNavGraph

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
            transactionService,
            sharedPreferences
        )
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            val showBottomBar = currentDestination in listOf("home", "accounts", "reports", "search")
            if (showBottomBar) {
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
            if (showFab) {
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
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            PaisaNavGraph(
                navController = navController,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}
