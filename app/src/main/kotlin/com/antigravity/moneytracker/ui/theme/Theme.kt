package com.antigravity.moneytracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class AppThemeType {
    LIQUID_GLASS,
    MATERIAL_EXPRESSIVE
}

// Liquid Glass Schemes
private val LiquidDarkColorScheme = darkColorScheme(
    primary = LiquidPeach,
    secondary = LiquidPink,
    tertiary = LiquidAccent,
    background = LiquidDarkBackground,
    surface = LiquidDarkSurface
)

private val LiquidLightColorScheme = lightColorScheme(
    primary = LiquidAccent,
    secondary = LiquidPink,
    tertiary = LiquidPeach,
    background = LiquidWarmWhite,
    surface = LiquidLightSurface
)

// Material 3 Standard Schemes
private val MaterialDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val MaterialLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun MoneyTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    themeType: AppThemeType = AppThemeType.LIQUID_GLASS, // Default to Liquid Glass
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        AppThemeType.LIQUID_GLASS -> {
            if (darkTheme) LiquidDarkColorScheme else LiquidLightColorScheme
        }
        AppThemeType.MATERIAL_EXPRESSIVE -> {
             if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) MaterialDarkColorScheme else MaterialLightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
