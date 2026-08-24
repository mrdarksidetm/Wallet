package com.darkside.wallet.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.ui.theme.Expense
import com.darkside.wallet.ui.theme.Income
import com.darkside.wallet.ui.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: WalletViewModel,
    onCategoryClick: (Long) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val spendingByCategory by viewModel.spendingByCategory.collectAsStateWithLifecycle()
    val dailySpending by viewModel.dailySpending.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()

    var isLineChart by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Reports",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ReportRow("Total Income", totalIncome, Income, currencyCode)
                        Spacer(modifier = Modifier.height(10.dp))
                        ReportRow("Total Expense", totalExpense, Expense, currencyCode)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            thickness = 0.5.dp,
                            color = colorScheme.outlineVariant.copy(alpha = 0.25f)
                        )
                        ReportRow("Net Balance", totalIncome - totalExpense, colorScheme.primary, currencyCode)
                    }
                }
            }

            // Visual Breakdown Section Header & Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isLineChart) "Spending Trend" else "Spending Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    IconButton(onClick = { isLineChart = !isLineChart }) {
                        Icon(
                            imageVector = if (isLineChart) Icons.Rounded.PieChart else Icons.Rounded.ShowChart,
                            contentDescription = "Toggle Chart",
                            tint = colorScheme.primary
                        )
                    }
                }
            }

            // Canvas Chart Area
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLineChart) {
                            if (dailySpending.isEmpty()) {
                                Text("No trend data available", color = colorScheme.onSurfaceVariant)
                            } else {
                                WalletLineChart(data = dailySpending)
                            }
                        } else {
                            if (spendingByCategory.isEmpty()) {
                                Text("No expense records this month.", color = colorScheme.onSurfaceVariant)
                            } else {
                                WalletDonutChart(
                                    spending = spendingByCategory,
                                    totalExpense = totalExpense,
                                    currencyCode = currencyCode
                                )
                            }
                        }
                    }
                }
            }

            // Category Breakdown List
            if (!isLineChart && spendingByCategory.isNotEmpty()) {
                items(spendingByCategory, key = { it.categoryId }) { spending ->
                    CategoryBreakdownCard(
                        spending = spending,
                        currencyCode = currencyCode,
                        onClick = { onCategoryClick(spending.categoryId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportRow(title: String, amount: Double, color: Color, currencyCode: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = CurrencyEngine.formatCurrency(amount, currencyCode),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        )
    }
}

@Composable
fun WalletDonutChart(
    spending: List<CategorySpending>,
    totalExpense: Double,
    currencyCode: String
) {
    val colorScheme = MaterialTheme.colorScheme
    var animationPlayed by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "donut_progress"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 26.dp.toPx()
            val radius = (size.width - strokeWidth) / 2f
            val centerOffset = Offset(size.width / 2f, size.height / 2f)

            if (totalExpense <= 0) {
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.2f),
                    radius = radius,
                    center = centerOffset,
                    style = Stroke(width = strokeWidth)
                )
                return@Canvas
            }

            var startAngle = -90f
            spending.forEach { item ->
                val sweepAngle = ((item.amount / totalExpense) * 360f * progress.value).toFloat()
                val color = item.color.parseHexColor()

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total Expenses",
                style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = CurrencyEngine.formatCurrency(totalExpense, currencyCode),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Expense
                )
            )
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    spending: CategorySpending,
    currencyCode: String,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val categoryColor = spending.color.parseHexColor()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(categoryColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = spending.categoryName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyEngine.formatCurrency(spending.amount, currencyCode),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = "${(spending.percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { spending.percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = categoryColor,
                trackColor = colorScheme.surfaceContainerHighest
            )
        }
    }
}

@Composable
fun WalletLineChart(data: List<DailySummary>) {
    if (data.isEmpty()) return
    val colorScheme = MaterialTheme.colorScheme
    val maxAmount = data.maxOf { it.amount }.toFloat().coerceAtLeast(1f)

    var animationPlayed by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "line_chart_progress"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val count = data.size
        if (count < 2) return@Canvas

        val stepX = size.width / (count - 1)
        val heightPadding = 20.dp.toPx()
        val availableHeight = size.height - (heightPadding * 2)

        val points = data.mapIndexed { index, summary ->
            val x = index * stepX
            val y = size.height - heightPadding - ((summary.amount.toFloat() / maxAmount) * availableHeight * progress.value)
            Offset(x, y)
        }

        // Draw Line
        for (i in 0 until points.size - 1) {
            drawLine(
                color = colorScheme.primary,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw Points
        points.forEach { point ->
            drawCircle(
                color = colorScheme.primary,
                radius = 5.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = point
            )
        }
    }
}
