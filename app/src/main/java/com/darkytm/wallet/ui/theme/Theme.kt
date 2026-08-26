package com.darkytm.wallet.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode {
    LIGHT,
    DARK,
    AMOLED,
    SYSTEM
}

fun getWarmColorScheme(
    isDark: Boolean,
    isAmoled: Boolean,
    style: PaletteStyle
): ColorScheme {
    val base = when (style) {
        PaletteStyle.EXPRESSIVE -> if (isDark) {
            darkColorScheme(
                primary = ExpressivePrimaryDark,
                onPrimary = ExpressiveOnPrimaryDark,
                primaryContainer = ExpressivePrimaryContainerDark,
                onPrimaryContainer = ExpressiveOnPrimaryContainerDark,
                secondary = ExpressiveSecondaryDark,
                onSecondary = ExpressiveOnSecondaryDark,
                secondaryContainer = ExpressiveSecondaryContainerDark,
                onSecondaryContainer = ExpressiveOnSecondaryContainerDark,
                tertiary = ExpressiveTertiaryDark,
                onTertiary = ExpressiveOnTertiaryDark,
                tertiaryContainer = ExpressiveTertiaryContainerDark,
                onTertiaryContainer = ExpressiveOnTertiaryContainerDark,
                background = ExpressiveBackgroundDark,
                onBackground = ExpressiveOnBackgroundDark,
                surface = ExpressiveSurfaceDark,
                onSurface = ExpressiveOnSurfaceDark,
                surfaceVariant = ExpressiveSurfaceVariantDark,
                onSurfaceVariant = ExpressiveOnSurfaceVariantDark,
                outline = ExpressiveOutlineDark
            )
        } else {
            lightColorScheme(
                primary = ExpressivePrimaryLight,
                onPrimary = ExpressiveOnPrimaryLight,
                primaryContainer = ExpressivePrimaryContainerLight,
                onPrimaryContainer = ExpressiveOnPrimaryContainerLight,
                secondary = ExpressiveSecondaryLight,
                onSecondary = ExpressiveOnSecondaryLight,
                secondaryContainer = ExpressiveSecondaryContainerLight,
                onSecondaryContainer = ExpressiveOnSecondaryContainerLight,
                tertiary = ExpressiveTertiaryLight,
                onTertiary = ExpressiveOnTertiaryLight,
                tertiaryContainer = ExpressiveTertiaryContainerLight,
                onTertiaryContainer = ExpressiveOnTertiaryContainerLight,
                background = ExpressiveBackgroundLight,
                onBackground = ExpressiveOnBackgroundLight,
                surface = ExpressiveSurfaceLight,
                onSurface = ExpressiveOnSurfaceLight,
                surfaceVariant = ExpressiveSurfaceVariantLight,
                onSurfaceVariant = ExpressiveOnSurfaceVariantLight,
                outline = ExpressiveOutlineLight
            )
        }

        PaletteStyle.TONAL_SPOT -> if (isDark) {
            darkColorScheme(
                primary = TonalSpotPrimaryDark,
                secondary = TonalSpotSecondaryDark,
                tertiary = TonalSpotTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = TonalSpotPrimaryLight,
                secondary = TonalSpotSecondaryLight,
                tertiary = TonalSpotTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.VIBRANT -> if (isDark) {
            darkColorScheme(
                primary = VibrantPrimaryDark,
                secondary = VibrantSecondaryDark,
                tertiary = VibrantTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = VibrantPrimaryLight,
                secondary = VibrantSecondaryLight,
                tertiary = VibrantTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.RAINBOW -> if (isDark) {
            darkColorScheme(
                primary = RainbowPrimaryDark,
                secondary = RainbowSecondaryDark,
                tertiary = RainbowTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = RainbowPrimaryLight,
                secondary = RainbowSecondaryLight,
                tertiary = RainbowTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.FRUIT_SALAD -> if (isDark) {
            darkColorScheme(
                primary = FruitSaladPrimaryDark,
                secondary = FruitSaladSecondaryDark,
                tertiary = FruitSaladTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = FruitSaladPrimaryLight,
                secondary = FruitSaladSecondaryLight,
                tertiary = FruitSaladTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.SPRITZ -> if (isDark) {
            darkColorScheme(
                primary = SpritzPrimaryDark,
                secondary = SpritzSecondaryDark,
                tertiary = SpritzTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = SpritzPrimaryLight,
                secondary = SpritzSecondaryLight,
                tertiary = SpritzTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.FIDELITY -> if (isDark) {
            darkColorScheme(
                primary = FidelityPrimaryDark,
                secondary = FidelitySecondaryDark,
                tertiary = FidelityTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = FidelityPrimaryLight,
                secondary = FidelitySecondaryLight,
                tertiary = FidelityTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.CONTENT -> if (isDark) {
            darkColorScheme(
                primary = ContentPrimaryDark,
                secondary = ContentSecondaryDark,
                tertiary = ContentTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = ContentPrimaryLight,
                secondary = ContentSecondaryLight,
                tertiary = ContentTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.MONOCHROME -> if (isDark) {
            darkColorScheme(
                primary = MonochromePrimaryDark,
                secondary = MonochromeSecondaryDark,
                tertiary = MonochromeTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = MonochromePrimaryLight,
                secondary = MonochromeSecondaryLight,
                tertiary = MonochromeTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }

        PaletteStyle.NEUTRAL -> if (isDark) {
            darkColorScheme(
                primary = NeutralPrimaryDark,
                secondary = NeutralSecondaryDark,
                tertiary = NeutralTertiaryDark,
                background = ExpressiveBackgroundDark,
                surface = ExpressiveSurfaceDark
            )
        } else {
            lightColorScheme(
                primary = NeutralPrimaryLight,
                secondary = NeutralSecondaryLight,
                tertiary = NeutralTertiaryLight,
                background = ExpressiveBackgroundLight,
                surface = ExpressiveSurfaceLight
            )
        }
    }

    return if (isAmoled) {
        base.copy(
            background = BackgroundAmoled,
            surface = SurfaceAmoled,
            surfaceVariant = SurfaceVariantAmoled
        )
    } else {
        base
    }
}

/**
 * Root theme supporting all 10 Material 3 dynamic color scheme variants,
 * Light, Dark, AMOLED, and Google Sans Flex typography.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WalletTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    paletteStyle: PaletteStyle = PaletteStyle.EXPRESSIVE,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.SYSTEM -> isSystemDark
    }
    val isAmoled = themeMode == ThemeMode.AMOLED

    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamic = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (isAmoled) {
                dynamic.copy(
                    background = BackgroundAmoled,
                    surface = SurfaceAmoled,
                    surfaceVariant = SurfaceVariantAmoled
                )
            } else {
                dynamic
            }
        }
        else -> getWarmColorScheme(isDark = isDark, isAmoled = isAmoled, style = paletteStyle)
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = WalletTypography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
