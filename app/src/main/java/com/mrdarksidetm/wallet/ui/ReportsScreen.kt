package com.mrdarksidetm.wallet.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReportsScreen(viewModel: WalletViewModel) {
    // Safely collect the reactive StateFlows from the ViewModel.
    // These will automatically recompose the UI whenever the database changes.
    val income by viewModel.thisMonthIncome.collectAsState()
    val expense by viewModel.thisMonthExpense.collectAsState()

    // Total is the sum of both flows. Used as the denominator for angle math.
    val total = income + expense

    // Indian Rupee currency formatter for clean display (e.g., ₹1,234.56).
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // =========================================================================
    // Animation Engine
    // =========================================================================
    // We use a boolean flag + LaunchedEffect to trigger the chart animation
    // exactly once when the screen first composes. The flag flips from false
    // to true, causing the animateFloatAsState targets to jump from 0 to their
    // calculated values, which the spring physics then interpolate smoothly.
    // =========================================================================
    var animationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        // This block runs once after the first composition frame.
        // Setting animationPlayed = true causes the sweep angle targets below
        // to change from 0f to their calculated values, kicking off the animation.
        animationPlayed = true
    }

    // =========================================================================
    // Sweep Angle Calculation (Geometry)
    // =========================================================================
    // A full circle = 360 degrees. We divide the circle proportionally:
    //   incomeSweep  = (income / total) * 360  → the green arc's angular width
    //   expenseSweep = (expense / total) * 360 → the red arc's angular width
    //
    // Together they sum to 360°, forming a complete donut.
    // If total == 0, both targets stay at 0f (the empty-state gray ring is drawn).
    //
    // animateFloatAsState smoothly interpolates from the current value to the
    // target using a spring animation with:
    //   DampingRatioMediumBouncy = 0.5f → a gentle overshoot for visual delight
    //   StiffnessLow = 200f → slow, graceful motion (not snappy)
    // =========================================================================
    val incomeSweep by animateFloatAsState(
        targetValue = if (animationPlayed && total > 0) ((income / total) * 360f).toFloat() else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "income_sweep"
    )

    val expenseSweep by animateFloatAsState(
        targetValue = if (animationPlayed && total > 0) ((expense / total) * 360f).toFloat() else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "expense_sweep"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen header — uses the Material 3 typography scale (Google Sans Flex via Type.kt)
        Text(
            text = "Financial Overview",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 32.dp)
        )

        // =====================================================================
        // Native Compose Canvas Donut Chart
        // =====================================================================
        // The donut is drawn inside a fixed 200dp Box. The Canvas fills the box.
        // We use drawArc() to paint thick, rounded strokes (no filled wedges).
        // =====================================================================
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Convert the desired stroke thickness from dp to raw pixels.
                // This ensures the ring looks the same physical width on all densities.
                val strokeWidth = 24.dp.toPx()

                if (total > 0) {
                    // ----- INCOME ARC (Green) -----
                    // startAngle = -90f places the arc's starting point at 12 o'clock
                    //   (Canvas uses 3 o'clock as 0°, so -90° rotates to the top).
                    // sweepAngle = incomeSweep draws clockwise by that many degrees.
                    // useCenter = false ensures we draw an ARC, not a PIE wedge.
                    // StrokeCap.Round gives the arc ends a smooth, rounded terminal.
                    drawArc(
                        color = Color(0xFF4CAF50), // Material Green 500
                        startAngle = -90f,
                        sweepAngle = incomeSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // ----- EXPENSE ARC (Red) -----
                    // This arc begins exactly where the income arc ends:
                    //   startAngle = -90f + incomeSweep
                    // This ensures the two arcs fit together seamlessly into a full ring.
                    // sweepAngle = expenseSweep covers the remaining portion of 360°.
                    drawArc(
                        color = Color(0xFFF44336), // Material Red 500
                        startAngle = -90f + incomeSweep,
                        sweepAngle = expenseSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                } else {
                    // ----- EMPTY STATE -----
                    // When there is no data (income == 0 AND expense == 0), we draw
                    // a neutral gray circle so the user sees a placeholder ring
                    // instead of a blank white space. Alpha = 0.3f keeps it subtle.
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }

            // Center text overlaid on the donut hole.
            // Shows "Net Balance" label and the computed (income - expense) value.
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Net Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatter.format(income - expense),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (income - expense >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // =====================================================================
        // Summary ElevatedCard
        // =====================================================================
        // Material 3 ElevatedCard provides a subtle shadow (2dp) that lifts the
        // card off the surface. Inside, we display three key financial metrics.
        // =====================================================================
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReportRow(
                    label = "Total Income",
                    amount = income,
                    color = Color(0xFF4CAF50),
                    formatter = formatter
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ReportRow(
                    label = "Total Expense",
                    amount = expense,
                    color = Color(0xFFF44336),
                    formatter = formatter
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ReportRow(
                    label = "Net Savings",
                    amount = income - expense,
                    color = MaterialTheme.colorScheme.primary,
                    formatter = formatter
                )
            }
        }
    }
}

// =============================================================================
// ReportRow — A single metric row inside the Summary Card
// =============================================================================
// Displays a colored indicator dot, a label, and the formatted currency amount.
// =============================================================================
@Composable
private fun ReportRow(label: String, amount: Double, color: Color, formatter: NumberFormat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Colored indicator dot — matches the arc color in the donut chart.
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, shape = MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = formatter.format(amount),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
