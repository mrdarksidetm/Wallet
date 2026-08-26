package com.darkytm.wallet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkytm.wallet.ui.WalletViewModel
import com.darkytm.wallet.ui.screens.AddEntryScreen
import com.darkytm.wallet.ui.screens.HomeScreen

object WalletDestinations {
    const val HOME = "home"
    const val ADD = "add"
}

@Composable
fun WalletNavGraph(
    viewModel: WalletViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = WalletDestinations.HOME
    ) {
        composable(WalletDestinations.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(WalletDestinations.ADD) }
            )
        }
        composable(WalletDestinations.ADD) {
            AddEntryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
