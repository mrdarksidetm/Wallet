package com.darkside.wallet.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.ui.theme.GoogleSansFlex
import kotlin.math.min

data class PaletteColors(
    val top: Color,
    val bottomLeft: Color,
    val bottomRight: Color
)

fun getVariantColors(variant: String, isDark: Boolean): PaletteColors {
    return when (variant) {
        "tonalSpot" -> PaletteColors(
            top = if (isDark) Color(0xFF9BB1E8) else Color(0xFFD3DFFF),
            bottomLeft = if (isDark) Color(0xFFCBBCD6) else Color(0xFFE8D5EC),
            bottomRight = if (isDark) Color(0xFF42567D) else Color(0xFF677799)
        )
        "vibrant" -> PaletteColors(
            top = if (isDark) Color(0xFFFFB0C8) else Color(0xFFFFD8E4),
            bottomLeft = if (isDark) Color(0xFFFFB787) else Color(0xFFFFDCC1),
            bottomRight = if (isDark) Color(0xFFB01D56) else Color(0xFFE91E63)
        )
        "expressive" -> PaletteColors(
            top = if (isDark) Color(0xFFFFB596) else Color(0xFFFFDBCA),
            bottomLeft = if (isDark) Color(0xFFC7BFFF) else Color(0xFFE5DEFF),
            bottomRight = if (isDark) Color(0xFFB36700) else Color(0xFFFF9800)
        )
        "rainbow" -> PaletteColors(
            top = if (isDark) Color(0xFFFFB0D0) else Color(0xFFFFD8E6),
            bottomLeft = if (isDark) Color(0xFFA3DDB3) else Color(0xFFC4EED0),
            bottomRight = if (isDark) Color(0xFF7B1FA2) else Color(0xFF9C27B0)
        )
        "fruitSalad" -> PaletteColors(
            top = if (isDark) Color(0xFFA1DECA) else Color(0xFFC3EEDD),
            bottomLeft = if (isDark) Color(0xFFFFB1C1) else Color(0xFFFFD8DF),
            bottomRight = if (isDark) Color(0xFF007A50) else Color(0xFF00B074)
        )
        "fidelity" -> PaletteColors(
            top = if (isDark) Color(0xFFB8C4F6) else Color(0xFFDFE2F9),
            bottomLeft = if (isDark) Color(0xFFC4B2EE) else Color(0xFFE1D3F8),
            bottomRight = if (isDark) Color(0xFF303F9F) else Color(0xFF3F51B5)
        )
        "content" -> PaletteColors(
            top = if (isDark) Color(0xFFA8C8FF) else Color(0xFFD7E2FF),
            bottomLeft = if (isDark) Color(0xFFB9C9DF) else Color(0xFFD9E2F1),
            bottomRight = if (isDark) Color(0xFF1976D2) else Color(0xFF2196F3)
        )
        "neutral" -> PaletteColors(
            top = if (isDark) Color(0xFFC7C6CA) else Color(0xFFE3E2E6),
            bottomLeft = if (isDark) Color(0xFFC6C6CD) else Color(0xFFE2E2E9),
            bottomRight = if (isDark) Color(0xFF5A5C63) else Color(0xFF75787F)
        )
        else -> PaletteColors( // monochrome
            top = if (isDark) Color(0xFFBDBDBD) else Color(0xFFE0E0E0),
            bottomLeft = if (isDark) Color(0xFF757575) else Color(0xFFBDBDBD),
            bottomRight = if (isDark) Color(0xFF212121) else Color(0xFF424242)
        )
    }
}

@Composable
fun PaletteCanvas(
    colors: PaletteColors,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width / 2f, size.height / 2f)
        val rect = Rect(center = center, radius = radius)

        // Top half (180 to 360 deg)
        drawArc(
            color = colors.top,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = rect.topLeft,
            size = rect.size
        )

        // Bottom right quarter (0 to 90 deg)
        drawArc(
            color = colors.bottomRight,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = rect.topLeft,
            size = rect.size
        )

        // Bottom left quarter (90 to 180 deg)
        drawArc(
            color = colors.bottomLeft,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = rect.topLeft,
            size = rect.size
        )
    }
}

@Composable
fun DynamicColorVariantCard(
    label: String,
    variantKey: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    isDark: Boolean,
    onSelect: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val colors = remember(variantKey, isDark) { getVariantColors(variantKey, isDark) }

    Box(
        modifier = Modifier
            .width(84.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) colorScheme.primaryContainer.copy(alpha = 0.35f)
                else colorScheme.surfaceContainerLow
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colorScheme.primary else colorScheme.outlineVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(enabled = isEnabled) { onSelect() }
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                PaletteCanvas(
                    colors = colors,
                    modifier = Modifier.fillMaxSize()
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .shadow(4.dp, CircleShape)
                            .background(colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Selected",
                            tint = colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                ),
                color = if (isSelected) colorScheme.primary else colorScheme.onSurface,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizationScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val useDynamicColor by viewModel.useDynamicColor.collectAsStateWithLifecycle()
    val colorSchemeVariant by viewModel.colorSchemeVariant.collectAsStateWithLifecycle()

    val useGoogleSansFlex by viewModel.useGoogleSansFlex.collectAsStateWithLifecycle()
    val fontGrade by viewModel.fontGrade.collectAsStateWithLifecycle()
    val fontWeight by viewModel.fontWeight.collectAsStateWithLifecycle()
    val fontWidth by viewModel.fontWidth.collectAsStateWithLifecycle()
    val fontRoundness by viewModel.fontRoundness.collectAsStateWithLifecycle()
    val fontOpticalSize by viewModel.fontOpticalSize.collectAsStateWithLifecycle()

    val vibrateOnTransaction by viewModel.vibrateOnTransaction.collectAsStateWithLifecycle()
    val shouldRestartOnCurrencyChange by viewModel.shouldRestartOnCurrencyChange.collectAsStateWithLifecycle()

    val variants = listOf(
        "tonalSpot" to "Tonal Spot",
        "vibrant" to "Vibrant",
        "expressive" to "Expressive",
        "rainbow" to "Rainbow",
        "fruitSalad" to "Fruit Salad",
        "fidelity" to "Fidelity",
        "content" to "Content",
        "neutral" to "Neutral",
        "monochrome" to "Monochrome"
    )

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Preferences",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    AppBackButton(onBack = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            // Blueprint Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(colorScheme.primaryContainer, colorScheme.primary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeColor = Color.White.copy(alpha = 0.15f)
                        val step = 24.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(strokeColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                            x += step
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(strokeColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                            y += step
                        }
                    }
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null,
                        tint = colorScheme.onPrimary.copy(alpha = 0.85f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            // Editorial Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "App Craft",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = GoogleSansFlex,
                            fontWeight = FontWeight.Black,
                            fontSize = 44.sp,
                            letterSpacing = (-1).sp
                        ),
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tailor every detail of your experience. From the depth of the typography to the behavior of the hardware.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // 1. Theme & Style Section
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionHeaderTitle("THEME & STYLE")
                    Spacer(modifier = Modifier.height(14.dp))

                    // 3-way connected Segmented Button
                    ConnectedThemeModeSelector(
                        selectedMode = themeMode,
                        onModeSelected = { viewModel.setThemeMode(it) }
                    )
                }
            }

            // 2. Dynamic Color & Properties Section
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
                    SectionHeaderTitle("DYNAMIC COLOR AND PROPERTIES")
                    Spacer(modifier = Modifier.height(14.dp))

                    PersonalizationToggleTile(
                        icon = Icons.Rounded.Palette,
                        title = "Dynamic Color",
                        subtitle = "Use Material You dynamic palettes from your wallpaper",
                        checked = useDynamicColor,
                        onCheckedChange = { viewModel.setUseDynamicColor(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(variants) { (key, label) ->
                            DynamicColorVariantCard(
                                label = label,
                                variantKey = key,
                                isSelected = colorSchemeVariant == key,
                                isEnabled = useDynamicColor,
                                isDark = isDark,
                                onSelect = { viewModel.setColorSchemeVariant(key) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Info Callout
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Color scheme variants are active when Dynamic Color is enabled.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. Typography Section
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionHeaderTitle("TYPOGRAPHY")
                    Spacer(modifier = Modifier.height(14.dp))

                    PersonalizationToggleTile(
                        icon = Icons.Rounded.FontDownload,
                        title = "Google Sans Flex",
                        subtitle = "Enable variable weight and width optimizations",
                        checked = useGoogleSansFlex,
                        onCheckedChange = { viewModel.toggleGoogleSans(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Type Tester Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Wallet Sample",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "The quick brown fox jumps over the lazy dog.",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = if (useGoogleSansFlex) GoogleSansFlex else null,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Variable font weight and width optimizations applied dynamically.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = colorScheme.onSurfaceVariant,
                                    fontFamily = if (useGoogleSansFlex) GoogleSansFlex else null
                                )
                            )
                        }
                    }

                    AnimatedVisibility(visible = useGoogleSansFlex) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            SliderTile(
                                label = "Grade",
                                code = "GRAD",
                                value = fontGrade,
                                min = -200f,
                                max = 150f,
                                displayValue = fontGrade.toInt().toString(),
                                onValueChange = { viewModel.updateGrade(it) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SliderTile(
                                label = "Weight",
                                code = "wght",
                                value = fontWeight,
                                min = 100f,
                                max = 1000f,
                                displayValue = fontWeight.toInt().toString(),
                                onValueChange = { viewModel.updateWeight(it) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SliderTile(
                                label = "Width",
                                code = "wdth",
                                value = fontWidth,
                                min = 50f,
                                max = 150f,
                                displayValue = "${fontWidth.toInt()}%",
                                onValueChange = { viewModel.updateWidth(it) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SliderTile(
                                label = "Roundness",
                                code = "ROND",
                                value = fontRoundness,
                                min = 0f,
                                max = 100f,
                                displayValue = "${fontRoundness.toInt()}%",
                                onValueChange = { viewModel.updateFontRoundness(it) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SliderTile(
                                label = "Optical Size",
                                code = "opsz",
                                value = fontOpticalSize,
                                min = 8f,
                                max = 144f,
                                displayValue = "${fontOpticalSize.toInt()}pt",
                                onValueChange = { viewModel.updateOpticalSize(it) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                FilledTonalButton(
                                    onClick = { viewModel.resetTypography() },
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                                ) {
                                    Icon(Icons.Rounded.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reset Typography", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Feedback & Behavior Section
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
                    SectionHeaderTitle("FEEDBACK & BEHAVIOR")
                    Spacer(modifier = Modifier.height(14.dp))

                    PersonalizationToggleTile(
                        icon = Icons.Rounded.Vibration,
                        title = "Vibrate on Transaction",
                        subtitle = "Only vibrates when a new transaction is successfully saved",
                        checked = vibrateOnTransaction,
                        onCheckedChange = { viewModel.toggleVibrateOnTransaction(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PersonalizationToggleTile(
                        icon = Icons.Rounded.RestartAlt,
                        title = "Restart on Currency Change",
                        subtitle = "Automatically restart app to apply new currency settings",
                        checked = shouldRestartOnCurrencyChange,
                        onCheckedChange = { viewModel.toggleRestartOnCurrencyChange(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeaderTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Black
        ),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    )
}

@Composable
fun ConnectedThemeModeSelector(
    selectedMode: Int,
    onModeSelected: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val modes = listOf(
        Triple(0, "System", Icons.Rounded.BrightnessAuto),
        Triple(1, "Light", Icons.Rounded.LightMode),
        Triple(2, "Dark", Icons.Rounded.DarkMode)
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            modes.forEach { (mode, label, icon) ->
                val isSelected = selectedMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) colorScheme.primary else Color.Transparent)
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                            ),
                            color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalizationToggleTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun SliderTile(
    label: String,
    code: String,
    value: Float,
    min: Float,
    max: Float,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.6f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = code,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = value.coerceIn(min, max),
                onValueChange = onValueChange,
                valueRange = min..max,
                colors = SliderDefaults.colors(
                    thumbColor = colorScheme.primary,
                    activeTrackColor = colorScheme.primary,
                    inactiveTrackColor = colorScheme.surfaceContainerHighest
                )
            )
        }
    }
}
