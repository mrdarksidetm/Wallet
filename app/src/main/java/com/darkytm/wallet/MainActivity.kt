package com.darkytm.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkytm.wallet.data.repository.WalletRepository
import com.darkytm.wallet.navigation.WalletNavGraph
import com.darkytm.wallet.ui.WalletViewModel
import com.darkytm.wallet.ui.WalletViewModelFactory
import com.darkytm.wallet.ui.theme.WalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as WalletApplication
        val repository = app.repository

        setContent {
            WalletApp(repository = repository)
        }
    }
}

@Composable
private fun WalletApp(repository: WalletRepository) {
    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(repository)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    WalletTheme(
        themeMode = state.themeMode,
        paletteStyle = state.paletteStyle,
        dynamicColor = state.isDynamicColor
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            WalletNavGraph(viewModel = viewModel)
        }
    }
}
