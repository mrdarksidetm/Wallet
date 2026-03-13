package com.mrdarksidetm.wallet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mrdarksidetm.wallet.R

// =============================================================================
// Google Sans Flex Variable Font — FontVariation API
// =============================================================================
//
// Google Sans Flex is a VARIABLE font (.ttf). Unlike static fonts which ship
// separate files per weight (Light.ttf, Regular.ttf, Bold.ttf), a variable
// font contains ALL weights inside a single file. The Compose FontVariation API
// lets us request specific axis values at runtime from that single file.
//
// The FontVariation.Setting() function takes two arguments:
//   1. The 4-character OpenType axis tag (String) — identifies WHICH axis to set.
//   2. The Float value — the position along that axis.
//
// Standard axes (registered by OpenType spec):
//   "wght" = Weight axis. Maps to FontWeight values (100–900).
//            100 = Thin, 400 = Regular, 700 = Bold, 900 = Black.
//
// Custom axes (font-specific, defined by the type designer):
//   "ROND" = Roundedness axis. This is a CUSTOM axis unique to Google Sans Flex.
//            Value range: 0 (sharp, squared terminals) to 100 (fully rounded).
//            Setting ROND=100 gives the font its signature soft, friendly aesthetic.
//
// By creating multiple Font() entries with the SAME .ttf resource but DIFFERENT
// "wght" values, Compose can interpolate the variable font to produce any weight
// we need — without shipping multiple font files. This keeps APK size minimal.
// =============================================================================

// FontVariation.Settings factory: creates a Font entry for a specific weight
// while always keeping ROND=100 for maximum roundedness.
private fun googleSansFlexFont(weight: FontWeight, wghtValue: Float): Font {
    return Font(
        resId = R.font.google_sans_flex,
        weight = weight,
        variationSettings = FontVariation.Settings(
            // "wght" axis: controls the stroke thickness (weight) of the glyphs.
            // We pass the numeric weight value (e.g., 400f for Regular, 700f for Bold).
            FontVariation.Setting("wght", wghtValue),

            // "ROND" axis: controls the roundedness of stroke terminals and corners.
            // 0f = sharp/squared edges, 100f = fully rounded terminals.
            // We lock this to 100f across ALL weights for a consistent premium feel.
            FontVariation.Setting("ROND", 100f)
        )
    )
}

// Build the FontFamily by declaring each Material weight we use.
// Each call produces a Font from the SAME .ttf file but with different "wght" values.
// The fallback Noto Sans (static font) is appended at the end. If the system cannot
// render Google Sans Flex for any reason, Compose walks down the list and uses Noto Sans.
val AppFontFamily = FontFamily(
    // Google Sans Flex variable instances — one per weight tier
    googleSansFlexFont(weight = FontWeight.Normal, wghtValue = 400f),   // Regular text
    googleSansFlexFont(weight = FontWeight.Medium, wghtValue = 500f),   // Titles, labels
    googleSansFlexFont(weight = FontWeight.SemiBold, wghtValue = 600f), // Headlines
    googleSansFlexFont(weight = FontWeight.Bold, wghtValue = 700f),     // Hero numbers

    // Noto Sans static fallback — loaded from a bundled .ttf resource.
    // No FontVariation needed here since Noto Sans is NOT a variable font.
    Font(resId = R.font.noto_sans, weight = FontWeight.Normal),
    Font(resId = R.font.noto_sans, weight = FontWeight.Medium),
    Font(resId = R.font.noto_sans, weight = FontWeight.SemiBold),
    Font(resId = R.font.noto_sans, weight = FontWeight.Bold)
)

// =============================================================================
// Material 3 Typography Scale
// =============================================================================
// Every text role in the M3 spec is mapped to AppFontFamily so the entire app
// renders in Google Sans Flex (rounded) with Noto Sans as the safety net.
// Font sizes, line heights, and letter spacing follow the official M3 type scale.
// =============================================================================

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)