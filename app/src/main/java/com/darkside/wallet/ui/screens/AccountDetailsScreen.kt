package com.darkside.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Savings
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
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.data.entity.AccountEntity
import com.darkside.wallet.data.entity.AccountType
import com.darkside.wallet.data.entity.TransactionType
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.ui.components.TransactionGroupedList
import com.darkside.wallet.ui.settings.SectionHeaderTitle
import com.darkside.wallet.ui.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    accountId: Long,
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val transactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()

    val account = remember(accounts, accountId) {
        accounts.find { it.id == accountId } ?: AccountEntity(name = "Account")
    }

    val accountTransactions = remember(transactions, accountId) {
        transactions.filter { it.accountId == accountId }
    }

    val totalExpense = remember(accountTransactions) {
        accountTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    val categoryBreakdown = remember(accountTransactions, categories, totalExpense) {
        if (totalExpense <= 0) emptyList()
        else {
            accountTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.categoryId }
                .mapNotNull { (catId, txs) ->
                    val cat = categories.find { it.id == catId } ?: return@mapNotNull null
                    val sum = txs.sumOf { it.amount }
                    Triple(cat.name, sum, (sum / totalExpense).toFloat(), cat.color.parseHexColor())
                }
                .sortedByDescending { it.second }
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        account.name,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Account Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                val icon = when (account.type) {
                                    AccountType.CASH -> Icons.Rounded.AccountBalanceWallet
                                    AccountType.CARD -> Icons.Rounded.CreditCard
                                    AccountType.SAVINGS -> Icons.Rounded.Savings
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorScheme.surfaceContainerHighest)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = account.type.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyEngine.formatCurrency(account.balance, currencyCode),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp,
                                color = colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            // Category Spending Segmented Bar
            if (categoryBreakdown.isNotEmpty()) {
                item {
                    Column {
                        SectionHeaderTitle("SPENDING BREAKDOWN")
                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                // Multi-color segmented progress bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                ) {
                                    categoryBreakdown.forEach { (_, _, fraction, color) ->
                                        Box(
                                            modifier = Modifier
                                                .weight(fraction.coerceAtLeast(0.01f))
                                                .fillMaxHeight()
                                                .background(color)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                categoryBreakdown.forEach { (catName, amount, fraction, color) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = catName,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                        }

                                        Text(
                                            text = "${(fraction * 100).toInt()}% • ${CurrencyEngine.formatCurrency(amount, currencyCode)}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
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

                    if (accountTransactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No transactions recorded for this account",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        TransactionGroupedList(
                            transactions = accountTransactions,
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
