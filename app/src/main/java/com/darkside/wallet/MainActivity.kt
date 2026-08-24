package com.darkside.wallet

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.darkside.wallet.ui.MainAppScreen
import com.darkside.wallet.ui.theme.WalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = remember { getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE) }
            var themeMode by remember { mutableIntStateOf(prefs.getInt("theme_mode", 0)) }
            var useDynamicColor by remember { mutableStateOf(prefs.getBoolean("use_dynamic_color", true)) }

            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == "theme_mode") {
                        themeMode = prefs.getInt("theme_mode", 0)
                    } else if (key == "use_dynamic_color") {
                        useDynamicColor = prefs.getBoolean("use_dynamic_color", true)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

            val darkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            WalletTheme(
                darkTheme = darkTheme,
                dynamicColor = useDynamicColor
            ) {
                MainAppScreen()
            }
        }
    }
}
