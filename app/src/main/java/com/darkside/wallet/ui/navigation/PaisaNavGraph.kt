package com.darkside.wallet.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darkside.wallet.ui.*
import com.darkside.wallet.ui.screens.*

@Composable
fun PaisaNavGraph(
    navController: NavHostController,
    viewModel: WalletViewModel,
    snackbarHostState: SnackbarHostState
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToSubMenu = { title ->
                    when (title) {
                        "Budgets" -> navController.navigate("budgets")
                        "Assets" -> navController.navigate("assets")
                        "Bill Splitter" -> navController.navigate("bill_splitter")
                        "Goals" -> navController.navigate("goals")
                        "Recurring" -> navController.navigate("recurring")
                        "Labels" -> navController.navigate("labels")
                        "Categories" -> navController.navigate("categories")
                        else -> navController.navigate("home_submenu/$title")
                    }
                },
                onLoansClick = { navController.navigate("loans") }
            )
        }
        composable("budgets") {
            BudgetsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("goals") {
            GoalsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("recurring") {
            RecurringScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("labels") {
            LabelsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("categories") {
            CategoriesScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("assets") {
            AssetsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("bill_splitter") {
            BillSplitterScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
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
        composable("heatmap") { HeatmapScreen() }
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
