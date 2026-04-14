package com.darkside.wallet.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darkside.wallet.ui.screens.HeatmapScreen
import com.darkside.wallet.ui.screens.SearchScreen

@Composable
fun PaisaNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home") { PlaceholderScreen("Home Screen") }
        composable("Accounts") { PlaceholderScreen("Accounts Screen") }
        composable("Reports") { PlaceholderScreen("Reports Screen") }
        
        // Phase 2 Routes
        composable("search") { SearchScreen() }
        composable("heatmap") { HeatmapScreen() }
        composable("insights") { PlaceholderScreen("Insights") }
        composable("budgets") { PlaceholderScreen("Budgets") }
        composable("bill-splitter") { PlaceholderScreen("Bill Splitter") }
        composable("goals") { PlaceholderScreen("Goals") }
        composable("loans") { PlaceholderScreen("Loans") }
        composable("recurring") { PlaceholderScreen("Recurring Transactions") }
        composable("settings") { PlaceholderScreen("Settings") }
        composable("export") { PlaceholderScreen("CSV Export") }
        composable("contacts") { PlaceholderScreen("People / Contacts") }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
