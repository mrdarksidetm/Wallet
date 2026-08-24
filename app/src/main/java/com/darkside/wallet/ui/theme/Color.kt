package com.darkside.wallet.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Atelier Design Tokens (1:1 with Flutter AppColors)
val Primary = Color(0xFF0061A4)
val PrimaryDim = Color(0xFF004689)
val Tertiary = Color(0xFF535F7E)

// Surface Philosophy (Light)
val Surface = Color(0xFFFBF9F9)
val SurfaceContainerLow = Color(0xFFF5F3F4)
val SurfaceContainer = Color(0xFFEFEDEE)
val SurfaceContainerHigh = Color(0xFFE9E8E9)
val SurfaceContainerHighest = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF313234)

// Dark Theme Atelier Design Tokens
val BackgroundDark = Color(0xFF1A1C1E)
val SurfaceDark = Color(0xFF1A1C1E)
val OnSurfaceDark = Color(0xFFE2E2E6)
val PrimaryDark = Color(0xFFA9C7FF)
val OnPrimaryDark = Color(0xFF003062)
val PrimaryContainerDark = Color(0xFF004689)
val OnPrimaryContainerDark = Color(0xFFD6E3FF)
val SecondaryDark = Color(0xFFBEC6DC)
val TertiaryDark = Color(0xFFDDBCE0)
val SurfaceContainerDark = Color(0xFF1F1F23)
val SurfaceContainerHighestDark = Color(0xFF2A2A2E)
val OutlineDark = Color(0xFF8E9099)

// Utility Colors
val Income = Color(0xFF10B981)
val Expense = Color(0xFFEF4444)
val TextMutedLight = Color(0xFF94A3B8)
val TextMutedDark = Color(0xFF64748B)

/**
 * Extension to parse hex color strings, matching Flutter StringColorExtension.
 */
fun String.parseHexColor(): Color {
    return try {
        var hex = this.trim().uppercase()
        hex = hex.replace("#", "").replace("0X", "")

        if (hex.length == 3) {
            hex = hex.map { "$it$it" }.joinToString("")
        } else if (hex.length == 4) {
            hex = hex.map { "$it$it" }.joinToString("")
        }

        if (hex.length == 6) {
            hex = "FF$hex"
        }

        if (hex.length > 8) {
            hex = hex.substring(hex.length - 8)
        }

        Color(android.graphics.Color.parseColor("#$hex"))
    } catch (e: Exception) {
        Color(0xFF0061A4) // Fallback to Primary Blue
    }
}
