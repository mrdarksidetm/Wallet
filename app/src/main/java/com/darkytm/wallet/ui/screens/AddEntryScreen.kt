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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        mutableLongStateOf(state.accounts.firstOrNull()?.account?.id ?: 0L)
    }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedGoalId by remember { mutableStateOf<Long?>(null) }
    var selectedPersonId by remember { mutableStateOf<Long?>(null) }

    val filteredCategories = remember(state.categories, selectedType) {
        state.categories.filter { it.type == selectedType }
    }

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
            // Type Selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val types = listOf(
                    TransactionType.EXPENSE to "Expense",
                    TransactionType.INCOME to "Income",
                    TransactionType.TRANSFER to "Transfer"
                )
                types.forEachIndexed { index, (type, label) ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size),
                        onClick = {
                            selectedType = type
                            selectedCategoryId = null
                        },
                        selected = selectedType == type
                    ) {
                        Text(label)
                    }
                }
            }

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount ($)") },
                placeholder = { Text("0.00") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            // Title / Description
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / Note") },
                placeholder = { Text("e.g. Groceries at Trader Joe's") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Account Selection
            Text("Source Account", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.accounts.forEach { acc ->
                    FilterChip(
                        selected = selectedAccountId == acc.account.id,
                        onClick = { selectedAccountId = acc.account.id },
                        label = { Text("${acc.account.iconEmoji} ${acc.account.name}") }
                    )
                }
            }

            // Category Selection (for Expense/Income)
            if (selectedType != TransactionType.TRANSFER && filteredCategories.isNotEmpty()) {
                Text("Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredCategories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = {
                                selectedCategoryId = if (selectedCategoryId == cat.id) null else cat.id
                            },
                            label = { Text("${cat.iconEmoji} ${cat.name}") }
                        )
                    }
                }
            }

            // Goals Linking (Optional)
            if (state.goals.isNotEmpty()) {
                Text("Link to Savings Goal (Optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.goals.forEach { g ->
                        FilterChip(
                            selected = selectedGoalId == g.goal.id,
                            onClick = {
                                selectedGoalId = if (selectedGoalId == g.goal.id) null else g.goal.id
                            },
                            label = { Text("${g.goal.iconEmoji} ${g.goal.name}") }
                        )
                    }
                }
            }

            // People Linking (Optional)
            if (state.people.isNotEmpty()) {
                Text("Link to Person (Optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.people.forEach { p ->
                        FilterChip(
                            selected = selectedPersonId == p.person.id,
                            onClick = {
                                selectedPersonId = if (selectedPersonId == p.person.id) null else p.person.id
                            },
                            label = { Text("${p.person.avatarEmoji} ${p.person.name}") }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Save Button
            Button(
                onClick = {
                    val amount = CurrencyUtils.parseAmount(amountText) ?: 0.0
                    if (amount > 0 && selectedAccountId > 0) {
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
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Save Entry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
