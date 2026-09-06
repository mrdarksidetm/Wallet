package com.darkytm.wallet.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.data.model.TransactionWithDetails
import com.darkytm.wallet.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ActivityInsightsSection(
    allTransactions: List<TransactionWithDetails>,
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Activity Insights",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = colorScheme.onSurface
            )

            Text(
                text = "Analytics",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Card 1: Calendar Heatmap
        CalendarHeatmapCard(
            transactions = allTransactions,
            isBalanceVisible = isBalanceVisible
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Card 2: 30-Day Activity Trends Line / Area Chart
        ActivityTrendsCard(
            transactions = allTransactions,
            isBalanceVisible = isBalanceVisible
        )
    }
}

@Composable
private fun CalendarHeatmapCard(
    transactions: List<TransactionWithDetails>,
    isBalanceVisible: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (0.299f * colorScheme.background.red + 0.587f * colorScheme.background.green + 0.114f * colorScheme.background.blue) < 0.5f
    val cardBackground = if (isDark) colorScheme.surfaceContainer else colorScheme.surfaceContainerLow

    var monthOffset by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    val cal = remember(monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    // In Java Calendar, SUNDAY=1, MONDAY=2. Let's make Monday=0 .. Sunday=6
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val monthTitle = remember(cal) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }

    // Aggregate transactions for current displayed month
    val dailySpend = remember(transactions, year, month) {
        val map = mutableMapOf<Int, Double>()
        val countMap = mutableMapOf<Int, Int>()
        val startCal = Calendar.getInstance()
        transactions.forEach { item ->
            val tx = item.transaction
            if (tx.type == TransactionType.EXPENSE) {
                startCal.timeInMillis = tx.dateMillis
                if (startCal.get(Calendar.YEAR) == year && startCal.get(Calendar.MONTH) == month) {
                    val day = startCal.get(Calendar.DAY_OF_MONTH)
                    map[day] = (map[day] ?: 0.0) + tx.amount
                    countMap[day] = (countMap[day] ?: 0) + 1
                }
            }
        }
        Pair(map, countMap)
    }

    val maxSpend = dailySpend.first.values.maxOrNull() ?: 1.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = if (isDark) 0.35f else 0.45f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Heatmap",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Calendar heatmap",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = colorScheme.onSurface
                    )
                }

                // Month Navigator Arrows
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            monthOffset--
                            selectedDay = null
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = "Prev Month",
                            tint = colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = monthTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = {
                            monthOffset++
                            selectedDay = null
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Next Month",
                            tint = colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday Headers
            val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekdays.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid of Days (up to 6 rows)
            val totalSlots = firstDayOfWeek + daysInMonth
            val totalRows = (totalSlots + 6) / 7

            for (row in 0 until totalRows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (col in 0 until 7) {
                        val index = row * 7 + col
                        val dayNum = index - firstDayOfWeek + 1
                        if (dayNum in 1..daysInMonth) {
                            val spend = dailySpend.first[dayNum] ?: 0.0
                            val intensity = if (spend > 0) ((spend / maxSpend) * 4).coerceIn(1.0, 4.0).toInt() else 0
                            val cellColor = when (intensity) {
                                1 -> colorScheme.primary.copy(alpha = 0.25f)
                                2 -> colorScheme.primary.copy(alpha = 0.50f)
                                3 -> colorScheme.primary.copy(alpha = 0.75f)
                                4 -> colorScheme.primary
                                else -> colorScheme.surfaceContainerHighest.copy(alpha = 0.35f)
                            }
                            val isSelected = selectedDay == dayNum

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(cellColor)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedDay = if (selectedDay == dayNum) null else dayNum
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (intensity > 2 || isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (intensity >= 3) colorScheme.onPrimary else colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(34.dp))
                        }
                    }
                }
            }

            // Selected Day Summary Card
            AnimatedVisibility(
                visible = selectedDay != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                selectedDay?.let { day ->
                    val spend = dailySpend.first[day] ?: 0.0
                    val count = dailySpend.second[day] ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.6f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Day $day, $monthTitle",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = if (isBalanceVisible) "${CurrencyUtils.formatAmount(spend)} spent ($count txs)" else "•••• spent ($count txs)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { selectedDay = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityTrendsCard(
    transactions: List<TransactionWithDetails>,
    isBalanceVisible: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (0.299f * colorScheme.background.red + 0.587f * colorScheme.background.green + 0.114f * colorScheme.background.blue) < 0.5f
    val cardBackground = if (isDark) colorScheme.surfaceContainer else colorScheme.surfaceContainerLow

    // Compute last 30 days data points
    val stats = remember(transactions) {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        val days = 30
        val dailyExpenses = DoubleArray(days) { 0.0 }
        val dailyIncomes = DoubleArray(days) { 0.0 }
        var total30DayExpense = 0.0
        var total30DayIncome = 0.0

        val calTx = Calendar.getInstance()
        val thirtyDaysAgo = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            add(Calendar.DAY_OF_YEAR, -29)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        transactions.forEach { item ->
            val tx = item.transaction
            if (tx.dateMillis in thirtyDaysAgo..now.timeInMillis) {
                calTx.timeInMillis = tx.dateMillis
                val dayDiff = ((now.timeInMillis - tx.dateMillis) / (24 * 60 * 60 * 1000L)).toInt().coerceIn(0, 29)
                val bucketIndex = 29 - dayDiff
                when (tx.type) {
                    TransactionType.EXPENSE -> {
                        dailyExpenses[bucketIndex] += tx.amount
                        total30DayExpense += tx.amount
                    }
                    TransactionType.INCOME -> {
                        dailyIncomes[bucketIndex] += tx.amount
                        total30DayIncome += tx.amount
                    }
                    else -> {}
                }
            }
        }

        TrendStats(
            dailyExpenses = dailyExpenses.toList(),
            totalExpense = total30DayExpense,
            totalIncome = total30DayIncome
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = if (isDark) 0.35f else 0.45f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorScheme.tertiary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = "Trends",
                            tint = colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Activity trends (30 days)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Income & Expense Summary Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TrendBadge(
                    label = "Income",
                    amount = stats.totalIncome,
                    color = Color(0xFF10B981),
                    isVisible = isBalanceVisible,
                    modifier = Modifier.weight(1f)
                )

                TrendBadge(
                    label = "Expense",
                    amount = stats.totalExpense,
                    color = Color(0xFFEF4444),
                    isVisible = isBalanceVisible,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Native Compose Canvas Sparkline Chart
            val primaryColor = colorScheme.primary
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val w = size.width
                val h = size.height
                val data = stats.dailyExpenses
                val maxVal = (data.maxOrNull() ?: 1.0).coerceAtLeast(10.0)

                // Background grid lines (3 lines)
                repeat(3) { i ->
                    val y = h * (i + 1) / 4f
                    drawLine(
                        color = primaryColor.copy(alpha = 0.08f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (data.isNotEmpty()) {
                    val step = w / (data.size - 1).coerceAtLeast(1)
                    val points = data.mapIndexed { index, v ->
                        val x = index * step
                        val y = h - ((v / maxVal) * (h * 0.85f)).toFloat() - (h * 0.05f)
                        Offset(x, y)
                    }

                    val strokePath = Path()
                    val fillPath = Path()

                    fillPath.moveTo(0f, h)
                    points.forEachIndexed { i, pt ->
                        if (i == 0) {
                            strokePath.moveTo(pt.x, pt.y)
                            fillPath.lineTo(pt.x, pt.y)
                        } else {
                            val prev = points[i - 1]
                            val cx = (prev.x + pt.x) / 2f
                            strokePath.cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                            fillPath.cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                        }
                    }
                    fillPath.lineTo(w, h)
                    fillPath.close()

                    // Draw gradient fill under line
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.28f), Color.Transparent),
                            startY = 0f,
                            endY = h
                        )
                    )

                    // Draw smooth curve line
                    drawPath(
                        path = strokePath,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw end point indicator
                    val lastPt = points.last()
                    drawCircle(
                        color = primaryColor,
                        radius = 4.dp.toPx(),
                        center = lastPt
                    )
                }
            }
        }
    }
}

private data class TrendStats(
    val dailyExpenses: List<Double>,
    val totalExpense: Double,
    val totalIncome: Double
)

@Composable
private fun TrendBadge(
    label: String,
    amount: Double,
    color: Color,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = if (isVisible) CurrencyUtils.formatAmount(amount) else "••••••",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )
        }
    }
}
