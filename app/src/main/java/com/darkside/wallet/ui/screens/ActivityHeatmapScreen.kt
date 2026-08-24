package com.darkside.wallet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.data.entity.TransactionEntity
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.ui.components.TransactionSegmentedCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHeatmapScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val transactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()

    // Group transactions by Year and Month
    val groupedByYearMonth = remember(transactions) {
        val map = TreeMap<Int, TreeMap<Int, MutableList<TransactionEntity>>>(reverseOrder())
        for (tx in transactions) {
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) // 0-indexed
            val yearMap = map.getOrPut(year) { TreeMap(reverseOrder()) }
            yearMap.getOrPut(month) { mutableListOf() }.add(tx)
        }
        map
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Activity History",
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
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No transactions found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedByYearMonth.forEach { (year, monthsMap) ->
                    item {
                        Text(
                            text = year.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = colorScheme.primary
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }

                    monthsMap.forEach { (month, monthTxs) ->
                        item {
                            MonthHeatmapCard(
                                year = year,
                                month = month,
                                transactions = monthTxs,
                                currencyCode = currencyCode
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthHeatmapCard(
    year: Int,
    month: Int,
    transactions: List<TransactionEntity>,
    currencyCode: String
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedDayMidnight by remember { mutableStateOf<Long?>(null) }

    // Map day to transactions & total amount
    val txsByDay = remember(transactions) {
        val map = mutableMapOf<Int, MutableList<TransactionEntity>>()
        for (tx in transactions) {
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            val day = cal.get(Calendar.DAY_OF_MONTH)
            map.getOrPut(day) { mutableListOf() }.add(tx)
        }
        map
    }

    val selectedDayTxs = remember(selectedDayMidnight, txsByDay) {
        if (selectedDayMidnight == null) emptyList()
        else {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDayMidnight!! }
            txsByDay[cal.get(Calendar.DAY_OF_MONTH)] ?: emptyList()
        }
    }

    val monthCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(monthCal.time)
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    // Weekday of 1st day (Sunday = 1, Monday = 2, ...)
    val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK)
    val startOffset = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${transactions.size} activities",
                    style = MaterialTheme.typography.labelMedium.copy(color = colorScheme.onSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday label headers
            val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekdays.forEach { dayLabel ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid (7 columns)
            val totalCells = startOffset + daysInMonth
            val rows = (totalCells + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until 7) {
                            val cellIndex = (row * 7) + col
                            if (cellIndex < startOffset || cellIndex >= totalCells) {
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                val day = cellIndex - startOffset + 1
                                val dayTxs = txsByDay[day] ?: emptyList()
                                val hasEntry = dayTxs.isNotEmpty()

                                val dayCal = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, day)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                val dayMidnight = dayCal.timeInMillis
                                val isSelected = selectedDayMidnight == dayMidnight

                                val totalAmount = dayTxs.sumOf { it.amount }
                                val cellBgColor = when {
                                    isSelected -> colorScheme.primary
                                    !hasEntry -> Color.Transparent
                                    totalAmount < 500 -> colorScheme.primary.copy(alpha = 0.55f)
                                    totalAmount < 2000 -> colorScheme.primary.copy(alpha = 0.75f)
                                    else -> colorScheme.primary
                                }

                                val textColor = if (hasEntry || isSelected) Color.White
                                else colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(cellBgColor)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) colorScheme.primary
                                            else colorScheme.outlineVariant.copy(alpha = if (hasEntry) 0.3f else 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = hasEntry) {
                                            selectedDayMidnight = if (selectedDayMidnight == dayMidnight) null else dayMidnight
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (hasEntry || isSelected) FontWeight.Black else FontWeight.Normal,
                                            color = textColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Selected Day Breakdown
            AnimatedVisibility(visible = selectedDayMidnight != null && selectedDayTxs.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    val dateFormatted = selectedDayMidnight?.let {
                        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date(it))
                    } ?: ""

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateFormatted,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.primary
                            )
                        )
                        Text(
                            text = "${selectedDayTxs.size} ${if (selectedDayTxs.size == 1) "entry" else "entries"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TransactionSegmentedCard(
                        transactions = selectedDayTxs,
                        currencyCode = currencyCode
                    )
                }
            }
        }
    }
}
