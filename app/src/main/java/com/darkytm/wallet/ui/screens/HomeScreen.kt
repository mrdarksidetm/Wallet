package com.darkytm.wallet.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkytm.wallet.data.model.AccountWithBalance
import com.darkytm.wallet.data.model.BudgetWithProgress
import com.darkytm.wallet.data.model.GoalWithProgress
import com.darkytm.wallet.data.model.PersonWithDebt
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.data.model.TransactionWithDetails
import com.darkytm.wallet.ui.WalletViewModel
import com.darkytm.wallet.ui.theme.DebtPurpleDark
import com.darkytm.wallet.ui.theme.DebtPurpleLight
import com.darkytm.wallet.ui.theme.ExpenseRedDark
import com.darkytm.wallet.ui.theme.ExpenseRedLight
import com.darkytm.wallet.ui.theme.IncomeGreenDark
import com.darkytm.wallet.ui.theme.IncomeGreenLight
import com.darkytm.wallet.ui.theme.PaletteStyle
import com.darkytm.wallet.ui.theme.ThemeMode
import com.darkytm.wallet.ui.theme.TransferBlueDark
import com.darkytm.wallet.ui.theme.TransferBlueLight
import com.darkytm.wallet.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WalletViewModel,
    onAddClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeCustomizerDialog(
            currentThemeMode = state.themeMode,
            currentPalette = state.paletteStyle,
            isDynamic = state.isDynamicColor,
            onThemeModeSelected = { viewModel.setThemeMode(it) },
            onPaletteSelected = { viewModel.setPaletteStyle(it) },
            onDynamicToggled = { viewModel.setDynamicColor(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wallet",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Theme & Palette Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add entry") },
                text = { Text("Add Entry", fontWeight = FontWeight.SemiBold) },
                elevation = FloatingActionButtonDefaults.elevation()
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            item {
                BalanceOverviewCard(balanceText = CurrencyUtils.formatAmount(state.totalBalance))
            }

            // Subsystems Summary Horizontal Row
            if (state.accounts.isNotEmpty() || state.goals.isNotEmpty() || state.budgets.isNotEmpty() || state.people.isNotEmpty()) {
                item {
                    SubsystemsCarousel(
                        accounts = state.accounts,
                        goals = state.goals,
                        budgets = state.budgets,
                        people = state.people
                    )
                }
            }

            item {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp)
                )
            }

            if (state.recentTransactions.isEmpty()) {
                item { EmptyTransactionsView() }
            } else {
                items(state.recentTransactions, key = { it.transaction.id }) { item ->
                    TransactionItemRow(
                        item = item,
                        onDelete = { viewModel.deleteTransaction(item.transaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceOverviewCard(balanceText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(
                text = "Total Net Worth",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = balanceText,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SubsystemsCarousel(
    accounts: List<AccountWithBalance>,
    goals: List<GoalWithProgress>,
    budgets: List<BudgetWithProgress>,
    people: List<PersonWithDebt>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Accounts
        accounts.forEach { acc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = acc.account.iconEmoji, fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(text = acc.account.name, style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = CurrencyUtils.formatAmount(acc.currentBalance),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Goals
        goals.forEach { g ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp).width(140.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = g.goal.iconEmoji, fontSize = 18.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = g.goal.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = g.progressPercent,
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${(g.progressPercent * 100).toInt()}% • ${CurrencyUtils.formatAmount(g.currentSaved)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Budgets
        budgets.forEach { b ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp).width(130.dp)) {
                    Text(
                        text = b.budget.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${CurrencyUtils.formatAmount(b.spentAmount)} spent",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItemRow(item: TransactionWithDetails, onDelete: () -> Unit) {
    val tx = item.transaction
    val isIncome = tx.type == TransactionType.INCOME
    val isTransfer = tx.type == TransactionType.TRANSFER
    val isDebt = tx.type == TransactionType.DEBT_LEND || tx.type == TransactionType.DEBT_BORROW || tx.type == TransactionType.DEBT_REPAY
    val isGoal = tx.type == TransactionType.GOAL_CONTRIBUTION || tx.type == TransactionType.GOAL_WITHDRAWAL

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val amountColor = when (tx.type) {
        TransactionType.INCOME -> if (isDark) IncomeGreenDark else IncomeGreenLight
        TransactionType.EXPENSE -> if (isDark) ExpenseRedDark else ExpenseRedLight
        TransactionType.TRANSFER -> if (isDark) TransferBlueDark else TransferBlueLight
        TransactionType.DEBT_LEND, TransactionType.DEBT_BORROW, TransactionType.DEBT_REPAY -> if (isDark) DebtPurpleDark else DebtPurpleLight
        TransactionType.GOAL_CONTRIBUTION, TransactionType.GOAL_WITHDRAWAL -> MaterialTheme.colorScheme.primary
    }

    val emoji = item.category?.iconEmoji
        ?: item.goal?.iconEmoji
        ?: item.person?.avatarEmoji
        ?: item.account?.iconEmoji
        ?: "💸"

    val dateText = remember(tx.dateMillis) {
        SimpleDateFormat("d MMM, h:mm a", Locale.getDefault()).format(Date(tx.dateMillis))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(44.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = tx.title.ifBlank { item.category?.name ?: tx.type.name },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    item.account?.let {
                        Text(
                            text = "${it.iconEmoji} ${it.name} • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                if (tx.note.isNotBlank()) {
                    Text(
                        text = tx.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val prefix = when (tx.type) {
                TransactionType.INCOME, TransactionType.DEBT_BORROW, TransactionType.GOAL_WITHDRAWAL -> "+"
                TransactionType.EXPENSE, TransactionType.DEBT_LEND, TransactionType.GOAL_CONTRIBUTION -> "-"
                TransactionType.TRANSFER -> "⇄ "
                else -> ""
            }

            Text(
                text = prefix + CurrencyUtils.formatAmount(tx.amount),
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EmptyTransactionsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🪙", fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No Transactions Logged",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Tap \"Add Entry\" to log income, expenses, transfers, debts, or savings goals.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ThemeCustomizerDialog(
    currentThemeMode: ThemeMode,
    currentPalette: PaletteStyle,
    isDynamic: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onPaletteSelected: (PaletteStyle) -> Unit,
    onDynamicToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Appearance & Theme Styles", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Theme Mode", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                ThemeMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = currentThemeMode == mode,
                            onClick = { onThemeModeSelected(mode) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (mode) {
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                                ThemeMode.AMOLED -> "AMOLED (Pure Pitch Black)"
                                ThemeMode.SYSTEM -> "Follow System"
                            }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text("Material 3 Dynamic Palette Styles (10)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                PaletteStyle.entries.forEach { style ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = currentPalette == style,
                            onClick = { onPaletteSelected(style) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(text = style.displayName, fontWeight = FontWeight.Medium)
                            Text(
                                text = style.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Android Wallpaper Colors (12+)", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isDynamic, onCheckedChange = onDynamicToggled)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
