package com.darkytm.wallet.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkytm.wallet.data.model.AccountWithBalance
import com.darkytm.wallet.data.model.BudgetWithProgress
import com.darkytm.wallet.data.model.Category
import com.darkytm.wallet.data.model.GoalWithProgress
import com.darkytm.wallet.data.model.PersonWithDebt
import com.darkytm.wallet.data.model.RecurringRule
import com.darkytm.wallet.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewDetailHost(
    activeCategory: OverviewCategoryType?,
    accounts: List<AccountWithBalance>,
    budgets: List<BudgetWithProgress>,
    goals: List<GoalWithProgress>,
    people: List<PersonWithDebt>,
    recurringRules: List<RecurringRule>,
    categories: List<Category>,
    isBalanceVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (activeCategory == null) return
    val sheetState = rememberModalBottomSheetState()
    val colorScheme = MaterialTheme.colorScheme

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
            when (activeCategory) {
                OverviewCategoryType.ACCOUNTS -> AccountsDetailContent(accounts, isBalanceVisible, onDismiss)
                OverviewCategoryType.BUDGETS -> BudgetsDetailContent(budgets, isBalanceVisible, onDismiss)
                OverviewCategoryType.GOALS -> GoalsDetailContent(goals, isBalanceVisible, onDismiss)
                OverviewCategoryType.LOANS -> LoansDetailContent(people, isBalanceVisible, onDismiss)
                OverviewCategoryType.RECURRING -> RecurringDetailContent(recurringRules, isBalanceVisible, onDismiss)
                OverviewCategoryType.CATEGORIES -> CategoriesDetailContent(categories, onDismiss)
                OverviewCategoryType.BILL_SPLITTER -> BillSplitterDetailContent(onDismiss)
                OverviewCategoryType.PEOPLE -> PeopleDetailContent(people, onDismiss)
            }
        }
    }
}

@Composable
private fun AccountsDetailContent(
    accounts: List<AccountWithBalance>,
    isBalanceVisible: Boolean,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Accounts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Spacer(Modifier.height(16.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(accounts) { acc ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(acc.account.iconEmoji, fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(acc.account.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(acc.account.type.name, style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                }
                Text(
                    text = if (isBalanceVisible) CurrencyUtils.formatAmount(acc.currentBalance) else "••••••",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BudgetsDetailContent(
    budgets: List<BudgetWithProgress>,
    isBalanceVisible: Boolean,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Budgets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Spacer(Modifier.height(16.dp))
    if (budgets.isEmpty()) {
        Text("No active budgets created yet.", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(budgets) { b ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(b.budget.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isBalanceVisible) "${CurrencyUtils.formatAmount(b.spentAmount)} / ${CurrencyUtils.formatAmount(b.budget.limitAmount)}" else "••••••",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (b.isExceeded) colorScheme.error else colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { b.progressPercent.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (b.isExceeded) colorScheme.error else colorScheme.primary,
                        trackColor = colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalsDetailContent(
    goals: List<GoalWithProgress>,
    isBalanceVisible: Boolean,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Savings Goals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Spacer(Modifier.height(16.dp))
    if (goals.isEmpty()) {
        Text("No savings goals created yet.", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(goals) { g ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(g.goal.iconEmoji, fontSize = 22.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(g.goal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "${(g.progressPercent * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.tertiary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { g.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = colorScheme.tertiary,
                        trackColor = colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (isBalanceVisible) "Saved: ${CurrencyUtils.formatAmount(g.currentSaved)} of ${CurrencyUtils.formatAmount(g.goal.targetAmount)}" else "••••••",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LoansDetailContent(
    people: List<PersonWithDebt>,
    isBalanceVisible: Boolean,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Loans & Debts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Spacer(Modifier.height(16.dp))
    if (people.isEmpty()) {
        Text("No lending or borrowed transactions recorded.", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(people) { p ->
                val isOwedToMe = p.netBalance >= 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(p.person.avatarEmoji, fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.person.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isOwedToMe) "Owes you" else "You owe",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (isBalanceVisible) CurrencyUtils.formatAmount(kotlin.math.abs(p.netBalance)) else "••••••",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isOwedToMe) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringDetailContent(
    rules: List<RecurringRule>,
    isBalanceVisible: Boolean,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Recurring Rules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Spacer(Modifier.height(16.dp))
    if (rules.isEmpty()) {
        Text("No recurring subscriptions or bills active.", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(rules) { r ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(r.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${r.frequency.name} • Due ${SimpleDateFormat("MMM dd", Locale.getDefault()).format(r.nextDueDateMillis)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (isBalanceVisible) CurrencyUtils.formatAmount(r.amount) else "••••••",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriesDetailContent(
    categories: List<Category>,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Spacer(Modifier.height(16.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(cat.iconEmoji, fontSize = 22.sp)
                Spacer(Modifier.width(12.dp))
                Text(cat.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Text(cat.type.name, style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PeopleDetailContent(
    people: List<PersonWithDebt>,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("People & Contacts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Spacer(Modifier.height(16.dp))
    if (people.isEmpty()) {
        Text("No contacts added yet.", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(people) { p ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(p.person.avatarEmoji, fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.person.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (p.person.phone.isNotBlank()) {
                            Text(p.person.phone, style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillSplitterDetailContent(onDismiss: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    var totalBillInput by remember { mutableStateOf("100.00") }
    var tipPercent by remember { mutableIntStateOf(10) }
    var peopleCount by remember { mutableIntStateOf(3) }

    val billAmount = totalBillInput.toDoubleOrNull() ?: 0.0
    val tipAmount = billAmount * (tipPercent / 100.0)
    val totalWithTip = billAmount + tipAmount
    val perPersonShare = if (peopleCount > 0) totalWithTip / peopleCount else 0.0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CallSplit, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text("Bill Splitter", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }

    Spacer(Modifier.height(16.dp))

    // Per Person Highlight Box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colorScheme.surfaceContainerHighest)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Per Person Share", style = MaterialTheme.typography.labelMedium, color = colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                text = CurrencyUtils.formatAmount(perPersonShare),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = colorScheme.onSurface
            )
            Text(
                text = "Total: ${CurrencyUtils.formatAmount(totalWithTip)} (incl. ${tipPercent}% tip)",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(Modifier.height(18.dp))

    // Bill Input
    OutlinedTextField(
        value = totalBillInput,
        onValueChange = { totalBillInput = it },
        label = { Text("Total Bill Amount") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    )

    Spacer(Modifier.height(14.dp))

    // Tip Selector
    Text("Tip Percentage", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(0, 10, 15, 20).forEach { pct ->
            val selected = tipPercent == pct
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) colorScheme.primaryContainer else colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
                    .clickable { tipPercent = pct }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${pct}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // People Stepper
    Text("Split between", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (peopleCount > 1) peopleCount-- },
            modifier = Modifier.background(colorScheme.surfaceContainerHighest, CircleShape)
        ) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease")
        }

        Text(
            text = "$peopleCount people",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = { peopleCount++ },
            modifier = Modifier.background(colorScheme.surfaceContainerHighest, CircleShape)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Increase")
        }
    }
}
