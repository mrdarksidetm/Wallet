package com.darkside.wallet.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.data.entity.CategoryEntity
import com.darkside.wallet.data.entity.TransactionType
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.ui.components.AppIcons
import com.darkside.wallet.ui.components.TransactionGroupedList
import com.darkside.wallet.ui.settings.SectionHeaderTitle
import com.darkside.wallet.ui.theme.parseHexColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    categoryId: Long,
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onNavigateToAddTransaction: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val transactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()

    val category = remember(categories, categoryId) {
        categories.find { it.id == categoryId } ?: CategoryEntity(name = "Category")
    }
    val categoryColor = remember(category) { category.color.parseHexColor() }

    val categoryTransactions = remember(transactions, categoryId) {
        transactions.filter { it.categoryId == categoryId }
    }

    // Monthly totals for Bar Chart (last 6 months)
    val monthlyStats = remember(categoryTransactions) {
        val cal = Calendar.getInstance()
        val list = mutableListOf<Pair<String, Double>>()
        for (i in 5 downTo 0) {
            val monthCal = Calendar.getInstance().apply {
                add(Calendar.MONTH, -i)
            }
            val targetYear = monthCal.get(Calendar.YEAR)
            val targetMonth = monthCal.get(Calendar.MONTH)
            val monthLabel = SimpleDateFormat("MMM", Locale.getDefault()).format(monthCal.time)

            val monthTotal = categoryTransactions.filter {
                val tCal = Calendar.getInstance().apply { timeInMillis = it.date }
                tCal.get(Calendar.YEAR) == targetYear && tCal.get(Calendar.MONTH) == targetMonth && it.type == TransactionType.EXPENSE
            }.sumOf { it.amount }

            list.add(monthLabel to monthTotal)
        }
        list
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(categoryColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AppIcons.getIcon(category.icon),
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Monthly Spending Bar Chart
            item {
                Column {
                    SectionHeaderTitle("MONTHLY SPENDING")
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            val maxAmount = (monthlyStats.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val barWidth = 24.dp.toPx()
                                    val count = monthlyStats.size
                                    val totalSpacing = size.width - (barWidth * count)
                                    val step = totalSpacing / (count + 1)

                                    monthlyStats.forEachIndexed { index, (_, amount) ->
                                        val x = step + index * (barWidth + step)
                                        val barHeight = ((amount / maxAmount) * (size.height - 30.dp.toPx())).toFloat().coerceAtLeast(4f)
                                        val y = size.height - barHeight - 20.dp.toPx()

                                        drawRoundRect(
                                            color = categoryColor,
                                            topLeft = Offset(x, y),
                                            size = Size(barWidth, barHeight),
                                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                        )
                                    }
                                }

                                // Month Labels
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    monthlyStats.forEach { (label, _) ->
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Transaction History Section
            item {
                Column {
                    SectionHeaderTitle("TRANSACTION HISTORY")
                    Spacer(modifier = Modifier.height(10.dp))

                    if (categoryTransactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No transactions for this category yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        TransactionGroupedList(
                            transactions = categoryTransactions,
                            currencyCode = currencyCode,
                            onDeleteTransaction = { viewModel.deleteTransaction(it) },
                            enableDismiss = true
                        )
                    }
                }
            }
        }
    }
}
