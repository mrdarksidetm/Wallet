package com.mrdarksidetm.wallet.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun PaisaNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home") {
            PlaceholderScreen("Home Screen")
        }
        composable("Accounts") {
            PlaceholderScreen("Accounts Screen")
        }
        composable("Reports") {
            PlaceholderScreen("Reports Screen")
        }
        composable("Search") {
            PlaceholderScreen("Search Screen")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
