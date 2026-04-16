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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.data.entity.TransactionType
import com.darkside.wallet.ui.theme.Income
import com.darkside.wallet.ui.theme.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: WalletViewModel) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val spendingByCategory by viewModel.spendingByCategory.collectAsState()
    val currencyCode by viewModel.currencyCode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Report", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        ReportRow("Total Income", totalIncome, Income, currencyCode)
                        Spacer(modifier = Modifier.height(12.dp))
                        ReportRow("Total Expense", totalExpense, Expense, currencyCode)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), alpha = 0.1f)
                        ReportRow("Net Balance", totalIncome - totalExpense, MaterialTheme.colorScheme.primary, currencyCode)
                    }
                }
            }

            // Visual Breakdown Section
            item {
                Text(
                    text = "Spending Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (spendingByCategory.isEmpty()) {
                        Text("No spending data available", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        PaisaDonutChart(
                            spending = spendingByCategory,
                            totalExpense = totalExpense,
                            currencyCode = currencyCode
                        )
                    }
                }
            }

            // Category List
            if (spendingByCategory.isNotEmpty()) {
                items(spendingByCategory) { spending ->
                    CategoryBreakdownItem(spending, currencyCode)
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun PaisaDonutChart(
    spending: List<CategorySpending>,
    totalExpense: Double,
    currencyCode: String
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "chart_progress"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    // Sorting by value for "Editorial" look
    val sortedSpending = remember(spending) { spending.sortedByDescending { it.amount } }

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val strokeWidth = 28.dp.toPx()
            val gapAngle = if (sortedSpending.size > 1) 4f else 0f
            val availableAngle = 360f - (gapAngle * sortedSpending.size)
            
            var currentAngle = -90f
            
            sortedSpending.forEach { item ->
                val sweepAngle = (item.amount.toFloat() / totalExpense.toFloat()) * availableAngle * progress.value
                
                if (sweepAngle > 0.5f) {
                    val color = try {
                        Color(android.graphics.Color.parseColor(item.color.replace("0xFF", "#")))
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.primary
                    }
                    
                    drawArc(
                        color = color,
                        startAngle = currentAngle + (gapAngle / 2f),
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                currentAngle += (item.amount.toFloat() / totalExpense.toFloat()) * availableAngle + gapAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total Spent",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = CurrencyEngine.formatCurrency(totalExpense, currencyCode),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
        }
    }
}

@Composable
fun CategoryBreakdownItem(spending: CategorySpending, currencyCode: String) {
    val color = try {
        Color(android.graphics.Color.parseColor(spending.color.replace("0xFF", "#")))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for category icon
                Text(
                    text = spending.categoryName.take(1),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = spending.categoryName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                    Text(
                        text = CurrencyEngine.formatCurrency(spending.amount, currencyCode),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(color.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(spending.percentage)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(color, color.copy(alpha = 0.7f))
                                )
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${(spending.percentage * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ReportRow(label: String, amount: Double, color: Color, currencyCode: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = CurrencyEngine.formatCurrency(amount, currencyCode),
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
