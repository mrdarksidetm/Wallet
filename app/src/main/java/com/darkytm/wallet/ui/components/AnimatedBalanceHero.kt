package com.darkytm.wallet.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkytm.wallet.util.CurrencyUtils
import kotlin.math.roundToInt

@Composable
fun AnimatedBalanceHero(
    totalBalance: Double,
    monthlyIncome: Double,
    monthlyExpense: Double,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This reflects your total combined net worth across all cash accounts, banks, and savings targets. It updates in real-time with every transaction.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Dynamic infinite animation for canvas organic blobs
    val infiniteTransition = rememberInfiniteTransition(label = "heroBlobTransition")
    val blobAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blobMovement"
    )

    val colorScheme = MaterialTheme.colorScheme
    val blob1Color = colorScheme.primary.copy(alpha = 0.28f)
    val blob2Color = colorScheme.tertiary.copy(alpha = 0.24f)

    val isDark = (0.299f * colorScheme.background.red + 0.587f * colorScheme.background.green + 0.114f * colorScheme.background.blue) < 0.5f
    val baseSurfaceColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surfaceContainerHigh

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(baseSurfaceColor)
            .border(
                width = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = if (isDark) 0.35f else 0.5f),
                shape = RoundedCornerShape(32.dp)
            )
    ) {
        // --- NATIVE CANVAS BLOB BACKGROUND ---
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height

            // Blob 1: Top-Left oscillating
            val x1 = -20f + (80f * blobAnim)
            val y1 = -20f + (50f * blobAnim)
            val radius1 = width * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blob1Color, Color.Transparent),
                    center = Offset(x1, y1),
                    radius = radius1
                ),
                center = Offset(x1, y1),
                radius = radius1
            )

            // Blob 2: Bottom-Right oscillating
            val x2 = width + 20f - (70f * blobAnim)
            val y2 = height + 20f - (45f * blobAnim)
            val radius2 = width * 0.5f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blob2Color, Color.Transparent),
                    center = Offset(x2, y2),
                    radius = radius2
                ),
                center = Offset(x2, y2),
                radius = radius2
            )
        }

        // --- FOREGROUND HERO CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header row with label, info, and visibility eye toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total balance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Balance Info",
                            tint = colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Balance Visibility",
                        tint = colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rolling / Obscured Total Balance
            if (isBalanceVisible) {
                Text(
                    text = CurrencyUtils.formatAmount(totalBalance),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    color = colorScheme.onSurface
                )
            } else {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(6) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(colorScheme.onSurface.copy(alpha = 0.8f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // This month spending progress
            val spentRatio = if (monthlyIncome > 0) (monthlyExpense / monthlyIncome).coerceIn(0.0, 1.0).toFloat() else 0f
            val spentPercent = if (monthlyIncome > 0) ((monthlyExpense / monthlyIncome) * 100).roundToInt() else 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This month",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface.copy(alpha = 0.85f)
                )

                if (isBalanceVisible && monthlyIncome > 0) {
                    Text(
                        text = "$spentPercent% spent",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (spentRatio > 0.9f) colorScheme.error else colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (isBalanceVisible && monthlyIncome > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { spentRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (spentRatio > 0.9f) colorScheme.error else colorScheme.primary,
                    trackColor = colorScheme.onSurface.copy(alpha = 0.08f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mini Stat Pills: Income & Expense
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatPill(
                    label = "Income",
                    amount = monthlyIncome,
                    color = Color(0xFF10B981),
                    isIncome = true,
                    isVisible = isBalanceVisible,
                    modifier = Modifier.weight(1f)
                )

                MiniStatPill(
                    label = "Expense",
                    amount = monthlyExpense,
                    color = Color(0xFFEF4444),
                    isIncome = false,
                    isVisible = isBalanceVisible,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniStatPill(
    label: String,
    amount: Double,
    color: Color,
    isIncome: Boolean,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isVisible) CurrencyUtils.formatAmount(amount) else "••••••",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
            }
        }
    }
}
