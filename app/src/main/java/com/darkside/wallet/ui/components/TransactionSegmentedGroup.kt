package com.darkside.wallet.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.data.entity.TransactionEntity
import com.darkside.wallet.data.entity.TransactionType
import com.darkside.wallet.ui.theme.Expense
import com.darkside.wallet.ui.theme.Income
import com.darkside.wallet.ui.theme.parseHexColor
import java.text.SimpleDateFormat
import java.util.*

/**
 * Formats a timestamp into a friendly date header label (Today, Yesterday, etc.)
 */
fun formatTransactionGroupDate(dateMillis: Long): String {
    val now = Calendar.getInstance()
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val yesterday = Calendar.getInstance().apply {
        timeInMillis = today.timeInMillis - (24 * 60 * 60 * 1000)
    }

    val txCal = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return when (txCal.timeInMillis) {
        today.timeInMillis -> "Today"
        yesterday.timeInMillis -> "Yesterday"
        else -> {
            if (txCal.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(dateMillis))
            } else {
                SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(dateMillis))
            }
        }
    }
}

/**
 * A segmented squircle card container for a group of transactions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSegmentedCard(
    transactions: List<TransactionEntity>,
    currencyCode: String,
    modifier: Modifier = Modifier,
    onTransactionClick: ((TransactionEntity) -> Unit)? = null,
    onDeleteTransaction: ((TransactionEntity) -> Unit)? = null,
    enableDismiss: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
            transactions.forEachIndexed { index, tx ->
                val isLast = index == transactions.size - 1

                if (enableDismiss && onDeleteTransaction != null) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                onDeleteTransaction(tx)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorScheme.errorContainer)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Delete",
                                        color = colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    ) {
                        TransactionRowItem(
                            tx = tx,
                            currencyCode = currencyCode,
                            onClick = { onTransactionClick?.invoke(tx) }
                        )
                    }
                } else {
                    TransactionRowItem(
                        tx = tx,
                        currencyCode = currencyCode,
                        onClick = { onTransactionClick?.invoke(tx) }
                    )
                }

                if (!isLast) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                        thickness = 0.5.dp,
                        color = colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

/**
 * An individual transaction row within a segmented group card.
 */
@Composable
fun TransactionRowItem(
    tx: TransactionEntity,
    currencyCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val categoryColor = tx.color?.parseHexColor() ?: colorScheme.primary
    val isIncome = tx.type == TransactionType.INCOME
    val isTransfer = tx.type == TransactionType.TRANSFER

    val amountColor = when {
        isIncome -> Income
        isTransfer -> colorScheme.primary
        else -> Expense
    }

    val amountPrefix = when {
        isIncome -> "+"
        isTransfer -> ""
        else -> "-"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(categoryColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            if (isTransfer) {
                Icon(
                    Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                val iconVector = AppIcons.getIcon(tx.icon ?: "payments")
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Note & Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (tx.note.isNullOrBlank()) (if (isTransfer) "Transfer" else "Transaction") else tx.note!!,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(tx.date)),
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Amount Display
        Text(
            text = "$amountPrefix${CurrencyEngine.formatCurrency(tx.amount, currencyCode)}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            ),
            color = amountColor
        )
    }
}

/**
 * A grouped list composable that splits transactions by day and renders
 * segmented group cards with daily summaries.
 */
@Composable
fun TransactionGroupedList(
    transactions: List<TransactionEntity>,
    currencyCode: String,
    modifier: Modifier = Modifier,
    onTransactionClick: ((TransactionEntity) -> Unit)? = null,
    onDeleteTransaction: ((TransactionEntity) -> Unit)? = null,
    enableDismiss: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    // Group transactions by Date (midnight timestamp)
    val grouped = remember(transactions) {
        val map = TreeMap<Long, MutableList<TransactionEntity>>(reverseOrder())
        for (tx in transactions) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = tx.date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            map.getOrPut(cal.timeInMillis) { mutableListOf() }.add(tx)
        }
        map
    }

    Column(modifier = modifier.fillMaxWidth()) {
        grouped.forEach { (dateKey, dayTxs) ->
            var dayTotal = 0.0
            dayTxs.forEach { tx ->
                when (tx.type) {
                    TransactionType.INCOME -> dayTotal += tx.amount
                    TransactionType.EXPENSE -> dayTotal -= tx.amount
                    else -> {}
                }
            }

            val isPositive = dayTotal > 0
            val isNegative = dayTotal < 0

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTransactionGroupDate(dateKey),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.2.sp
                        ),
                        color = colorScheme.onSurfaceVariant
                    )

                    if (dayTotal != 0.0) {
                        Text(
                            text = "${if (isPositive) "+" else if (isNegative) "-" else ""}${
                                CurrencyEngine.formatCurrency(kotlin.math.abs(dayTotal), currencyCode)
                            }",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black
                            ),
                            color = if (isPositive) Income else if (isNegative) Expense else colorScheme.onSurfaceVariant
                        )
                    }
                }

                TransactionSegmentedCard(
                    transactions = dayTxs,
                    currencyCode = currencyCode,
                    onTransactionClick = onTransactionClick,
                    onDeleteTransaction = onDeleteTransaction,
                    enableDismiss = enableDismiss
                )
            }
        }
    }
}
