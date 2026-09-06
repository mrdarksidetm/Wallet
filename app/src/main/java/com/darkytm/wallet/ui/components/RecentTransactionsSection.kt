package com.darkytm.wallet.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkytm.wallet.data.model.Transaction
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.data.model.TransactionWithDetails
import com.darkytm.wallet.util.CurrencyUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class TransactionFilter {
    ALL,
    EXPENSE,
    INCOME,
    TRANSFER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentTransactionsSection(
    transactions: List<TransactionWithDetails>,
    isBalanceVisible: Boolean,
    onDeleteTransaction: (Transaction) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }
    var selectedTxForDetails by remember { mutableStateOf<TransactionWithDetails?>(null) }

    val filteredList = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            TransactionFilter.ALL -> transactions
            TransactionFilter.EXPENSE -> transactions.filter { it.transaction.type == TransactionType.EXPENSE }
            TransactionFilter.INCOME -> transactions.filter { it.transaction.type == TransactionType.INCOME }
            TransactionFilter.TRANSFER -> transactions.filter { it.transaction.type == TransactionType.TRANSFER }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorScheme.surfaceContainerHighest)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${filteredList.size} items",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Segmented Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TransactionFilter.values().forEach { filter ->
                val isSelected = selectedFilter == filter
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) colorScheme.primaryContainer else Color.Transparent,
                    label = "filterBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
                    label = "filterText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .clickable { selectedFilter = filter }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction Items List or Empty View
        if (filteredList.isEmpty()) {
            EmptyTransactionsView(
                filter = selectedFilter,
                onAddClick = onAddClick
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredList.forEach { item ->
                    TransactionItemRow(
                        item = item,
                        isBalanceVisible = isBalanceVisible,
                        onClick = { selectedTxForDetails = item }
                    )
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedTxForDetails?.let { item ->
        TransactionDetailSheet(
            item = item,
            isBalanceVisible = isBalanceVisible,
            onDismiss = { selectedTxForDetails = null },
            onDelete = {
                onDeleteTransaction(item.transaction)
                selectedTxForDetails = null
            }
        )
    }
}

@Composable
fun TransactionItemRow(
    item: TransactionWithDetails,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tx = item.transaction
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (0.299f * colorScheme.background.red + 0.587f * colorScheme.background.green + 0.114f * colorScheme.background.blue) < 0.5f

    val amountColor = when (tx.type) {
        TransactionType.INCOME -> Color(0xFF10B981)
        TransactionType.EXPENSE -> if (isDark) Color(0xFFFF6B6B) else Color(0xFFDC2626)
        TransactionType.TRANSFER -> colorScheme.primary
        else -> colorScheme.onSurface
    }

    val amountPrefix = when (tx.type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        TransactionType.TRANSFER -> "↔ "
        else -> ""
    }

    val displayTitle = when {
        tx.title.isNotBlank() -> tx.title
        item.category != null -> item.category.name
        tx.type == TransactionType.TRANSFER -> "Account Transfer"
        else -> "Transaction"
    }

    val dateFormatted = formatRelativeDate(tx.dateMillis)
    val accountInfo = item.account?.name ?: "Account"
    val subtitle = "$accountInfo • $dateFormatted"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) colorScheme.surfaceContainer else colorScheme.surfaceContainerLow)
            .border(
                width = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = if (isDark) 0.3f else 0.4f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category / Type Emoji or Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when (tx.type) {
                            TransactionType.INCOME -> Color(0xFF10B981).copy(alpha = 0.15f)
                            TransactionType.EXPENSE -> colorScheme.primary.copy(alpha = 0.14f)
                            TransactionType.TRANSFER -> colorScheme.secondary.copy(alpha = 0.15f)
                            else -> colorScheme.surfaceContainerHighest
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.category != null) {
                    Text(text = item.category.iconEmoji, fontSize = 20.sp)
                } else {
                    Icon(
                        imageVector = when (tx.type) {
                            TransactionType.INCOME -> Icons.Filled.ArrowDownward
                            TransactionType.EXPENSE -> Icons.Filled.ArrowUpward
                            TransactionType.TRANSFER -> Icons.Filled.SwapHoriz
                            else -> Icons.Filled.ReceiptLong
                        },
                        contentDescription = null,
                        tint = amountColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Subtitle Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount
            Text(
                text = if (isBalanceVisible) "$amountPrefix${CurrencyUtils.formatAmount(tx.amount)}" else "••••••",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = amountColor,
                textAlign = TextAlign.End
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDetailSheet(
    item: TransactionWithDetails,
    isBalanceVisible: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val tx = item.transaction
    val colorScheme = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showConfirmDelete by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = colorScheme.onSurface
                )

                IconButton(
                    onClick = { showConfirmDelete = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(colorScheme.errorContainer.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Transaction",
                        tint = colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.7f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tx.type.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBalanceVisible) CurrencyUtils.formatAmount(tx.amount) else "••••••••",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.0).sp,
                        color = colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Detail rows
            DetailRow(label = "Date & Time", value = SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()).format(tx.dateMillis))
            DetailRow(label = "Account", value = item.account?.name ?: "—")
            if (tx.type == TransactionType.TRANSFER && item.toAccount != null) {
                DetailRow(label = "Destination Account", value = item.toAccount.name)
            }
            if (item.category != null) {
                DetailRow(label = "Category", value = "${item.category.iconEmoji} ${item.category.name}")
            }
            if (item.person != null) {
                DetailRow(label = "Person", value = "${item.person.avatarEmoji} ${item.person.name}")
            }
            if (item.goal != null) {
                DetailRow(label = "Goal", value = "${item.goal.iconEmoji} ${item.goal.name}")
            }
            if (tx.note.isNotBlank()) {
                DetailRow(label = "Note", value = tx.note)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (showConfirmDelete) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = { showConfirmDelete = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    FilledTonalButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onDelete()
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = colorScheme.errorContainer,
                            contentColor = colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm Delete", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                FilledTonalButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptyTransactionsView(
    filter: TransactionFilter,
    onAddClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No ${filter.name.lowercase()} transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Add an entry to start tracking your cashflow with native precision.",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Entry", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatRelativeDate(timeMillis: Long): String {
    val now = Calendar.getInstance()
    val txCal = Calendar.getInstance().apply { timeInMillis = timeMillis }

    val isSameDay = now.get(Calendar.YEAR) == txCal.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == txCal.get(Calendar.DAY_OF_YEAR)
    if (isSameDay) return "Today"

    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val isYesterday = yesterday.get(Calendar.YEAR) == txCal.get(Calendar.YEAR) &&
            yesterday.get(Calendar.DAY_OF_YEAR) == txCal.get(Calendar.DAY_OF_YEAR)
    if (isYesterday) return "Yesterday"

    return SimpleDateFormat("MMM dd", Locale.getDefault()).format(timeMillis)
}
