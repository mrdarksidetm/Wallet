package com.mrdarksidetm.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mrdarksidetm.wallet.ui.MainAppScreen
import com.mrdarksidetm.wallet.ui.theme.WalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalletTheme {
                MainAppScreen()
            }
        }
    }
}
