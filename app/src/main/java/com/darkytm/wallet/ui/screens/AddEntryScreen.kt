package com.darkytm.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkytm.wallet.data.model.Category
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.ui.WalletViewModel
import com.darkytm.wallet.util.CurrencyUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEntryScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var amountText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedAccountId by remember(state.accounts) {
        mutableLongStateOf(state.accounts.firstOrNull()?.account?.id ?: 1L)
    }
    var selectedCategoryId by remember(state.categories) {
        mutableStateOf<Long?>(state.categories.firstOrNull { it.type == TransactionType.EXPENSE }?.id)
    }
    var selectedGoalId by remember { mutableStateOf<Long?>(null) }
    var selectedPersonId by remember { mutableStateOf<Long?>(null) }

    val currencySymbol = remember { CurrencyUtils.getCurrencySymbol() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Transaction", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Transaction Type Segmented Control
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val types = listOf(
                    TransactionType.EXPENSE to "Expense 💸",
                    TransactionType.INCOME to "Income 💰",
                    TransactionType.TRANSFER to "Transfer ⇄",
                    TransactionType.DEBT_LEND to "Debt 👥"
                )
                types.forEachIndexed { index, (type, label) ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index, types.size),
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            selectedCategoryId = state.categories.firstOrNull { it.type == type }?.id
                        },
                        label = { Text(label, fontSize = 12.sp, maxLines = 1) }
                    )
                }
            }

            // 2. Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = CurrencyUtils.sanitizeAmountInput(it) },
                label = { Text("Amount") },
                leadingIcon = { Text(currencySymbol, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Account Selector
            if (state.accounts.isNotEmpty()) {
                Text("Account (Outflow / Source)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.accounts.forEach { acc ->
                        FilterChip(
                            selected = selectedAccountId == acc.account.id,
                            onClick = { selectedAccountId = acc.account.id },
                            label = { Text("${acc.account.iconEmoji} ${acc.account.name}") }
                        )
                    }
                }
            }

            // 4. Category Selector (for Expense / Income)
            val filteredCategories = state.categories.filter {
                if (selectedType == TransactionType.INCOME) it.type == TransactionType.INCOME else it.type == TransactionType.EXPENSE
            }
            if (filteredCategories.isNotEmpty() && (selectedType == TransactionType.EXPENSE || selectedType == TransactionType.INCOME)) {
                Text("Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filteredCategories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = { selectedCategoryId = cat.id },
                            label = { Text("${cat.iconEmoji} ${cat.name}") }
                        )
                    }
                }
            }

            // 5. Goals Link (if any exists)
            if (state.goals.isNotEmpty()) {
                Text("Connect to Savings Goal (Optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.goals.forEach { g ->
                        FilterChip(
                            selected = selectedGoalId == g.goal.id,
                            onClick = {
                                selectedGoalId = if (selectedGoalId == g.goal.id) null else g.goal.id
                                if (selectedGoalId != null) selectedType = TransactionType.GOAL_CONTRIBUTION
                            },
                            label = { Text("${g.goal.iconEmoji} ${g.goal.name}") }
                        )
                    }
                }
            }

            // 6. People Link for Debt (if any exists)
            if (state.people.isNotEmpty() && (selectedType == TransactionType.DEBT_LEND || selectedType == TransactionType.DEBT_BORROW)) {
                Text("Person (Counterparty)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.people.forEach { p ->
                        FilterChip(
                            selected = selectedPersonId == p.person.id,
                            onClick = { selectedPersonId = if (selectedPersonId == p.person.id) null else p.person.id },
                            label = { Text("${p.person.avatarEmoji} ${p.person.name}") }
                        )
                    }
                }
            }

            // 7. Title & Notes
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // 8. Submit Button
            val isValid = (amountText.toDoubleOrNull() ?: 0.0) > 0.0
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    viewModel.addTransaction(
                        amount = amount,
                        type = selectedType,
                        title = title.trim(),
                        note = note.trim(),
                        accountId = selectedAccountId,
                        categoryId = selectedCategoryId,
                        goalId = selectedGoalId,
                        personId = selectedPersonId
                    )
                    onBack()
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Save Transaction", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
