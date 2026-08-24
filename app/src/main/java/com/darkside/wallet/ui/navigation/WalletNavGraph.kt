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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.darkside.wallet.ui.*
import com.darkside.wallet.ui.screens.*
import com.darkside.wallet.ui.settings.*

@Composable
fun WalletNavGraph(
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
                        "Activity History", "Calendar heatmap" -> navController.navigate("activity_heatmap")
                        "Recent transactions" -> navController.navigate("home_submenu/Recent transactions")
                        else -> navController.navigate("home_submenu/$title")
                    }
                },
                onLoansClick = { navController.navigate("loans") }
            )
        }
        composable("budgets") {
            BudgetsScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("goals") {
            GoalsScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("recurring") {
            RecurringScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("labels") {
            LabelsScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("categories") {
            CategoriesScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("assets") {
            AssetsScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("bill_splitter") {
            BillSplitterScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("accounts") {
            AccountsScreen(
                viewModel = viewModel, 
                snackbarHostState = snackbarHostState,
                onAccountClick = { accountId ->
                    navController.navigate("account_details/$accountId")
                },
                onAddAccountClick = { navController.navigate("add_account") }
            )
        }
        composable("reports") {
            ReportsScreen(
                viewModel = viewModel,
                onCategoryClick = { categoryId ->
                    navController.navigate("category_details/$categoryId")
                }
            )
        }
        composable("people") {
            PeopleScreen(
                viewModel = viewModel, 
                snackbarHostState = snackbarHostState
            )
        }
        composable("search") {
            SearchScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("home_submenu/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Menu"
            HomeSubMenuScreen(title = title, onBack = { navController.popBackStack() })
        }
        composable("add_transaction") {
            AddTransactionScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("add_account") {
            AddAccountScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }, 
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel, 
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGeneral = { navController.navigate("general_settings") },
                onNavigateToPersonalization = { navController.navigate("personalization") },
                onNavigateToPrivacySecurity = { navController.navigate("privacy_security") },
                onNavigateToBackupRestore = { navController.navigate("backup_restore") },
                onNavigateToAbout = { navController.navigate("about") },
                onNavigateToLogcat = { navController.navigate("logcat") },
                onNavigateToEditProfile = { navController.navigate("edit_profile") },
                onNavigateToCurrencySelection = { navController.navigate("currency_selection") },
                onNavigateToCategories = { navController.navigate("categories") },
                onNavigateToFeedback = { navController.navigate("feedback") },
                onNavigateToPrivacyPolicy = { navController.navigate("privacy_policy") },
                onNavigateToTermsOfUse = { navController.navigate("terms_of_use") }
            )
        }
        composable("general_settings") {
            GeneralSettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEditProfile = { navController.navigate("edit_profile") },
                onNavigateToCurrencySelection = { navController.navigate("currency_selection") },
                onNavigateToCategories = { navController.navigate("categories") },
                onNavigateToFeedback = { navController.navigate("feedback") }
            )
        }
        composable("personalization") {
            PersonalizationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("privacy_security") {
            PrivacySecurityScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToPrivacyPolicy = { navController.navigate("privacy_policy") },
                onNavigateToTermsOfUse = { navController.navigate("terms_of_use") }
            )
        }
        composable("backup_restore") {
            BackupRestoreScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("about") {
            AboutScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToPrivacyPolicy = { navController.navigate("privacy_policy") }
            )
        }
        composable("logcat") {
            LogcatScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("privacy_policy") {
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("terms_of_use") {
            TermsOfUseScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("edit_profile") {
            EditProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("currency_selection") {
            CurrencySelectionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("feedback") {
            FeedbackScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("loans") {
            LoanScreen(
                viewModel = viewModel, 
                onBack = { navController.popBackStack() }
            )
        }
        composable("activity_heatmap") {
            ActivityHeatmapScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("heatmap") {
            ActivityHeatmapScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "category_details/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
            CategoryDetailsScreen(
                categoryId = categoryId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "account_details/{accountId}",
            arguments = listOf(navArgument("accountId") { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong("accountId") ?: 0L
            AccountDetailsScreen(
                accountId = accountId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
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
