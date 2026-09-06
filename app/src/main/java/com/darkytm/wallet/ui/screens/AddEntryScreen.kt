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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val colorScheme = MaterialTheme.colorScheme

    var amountText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedAccountId by remember(state.accounts) {
        mutableLongStateOf(state.accounts.firstOrNull()?.account?.id ?: 0L)
    }
    var selectedToAccountId by remember(state.accounts) {
        mutableLongStateOf(state.accounts.getOrNull(1)?.account?.id ?: state.accounts.firstOrNull()?.account?.id ?: 0L)
    }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedGoalId by remember { mutableStateOf<Long?>(null) }
    var selectedPersonId by remember { mutableStateOf<Long?>(null) }

    val filteredCategories = remember(state.categories, selectedType) {
        state.categories.filter { it.type == selectedType }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Log Transaction",
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                        Text(label, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount") },
                placeholder = { Text("0.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
            )

            // Title / Description
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / Purpose") },
                placeholder = { Text("e.g. Dinner, Coffee, Salary") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )

            // Note (Optional)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
                placeholder = { Text("Additional details...") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )

            // Source Account Selection
            Text(
                text = if (selectedType == TransactionType.TRANSFER) "From Account" else "Source Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.accounts.forEach { acc ->
                    val isSelected = selectedAccountId == acc.account.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedAccountId = acc.account.id },
                        label = { Text("${acc.account.iconEmoji} ${acc.account.name}", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            // Destination Account Selection (only for transfers)
            if (selectedType == TransactionType.TRANSFER) {
                Text(
                    text = "To Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.accounts.filter { it.account.id != selectedAccountId }.forEach { acc ->
                        val isSelected = selectedToAccountId == acc.account.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedToAccountId = acc.account.id },
                            label = { Text("${acc.account.iconEmoji} ${acc.account.name}", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            // Category Selection (for Expense/Income)
            if (selectedType != TransactionType.TRANSFER && filteredCategories.isNotEmpty()) {
                Text("Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredCategories.forEach { cat ->
                        val isSelected = selectedCategoryId == cat.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryId = if (selectedCategoryId == cat.id) null else cat.id
                            },
                            label = { Text("${cat.iconEmoji} ${cat.name}", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            // Goals Linking (Optional)
            if (state.goals.isNotEmpty()) {
                Text("Link to Savings Goal (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.goals.forEach { g ->
                        val isSelected = selectedGoalId == g.goal.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedGoalId = if (selectedGoalId == g.goal.id) null else g.goal.id
                            },
                            label = { Text("${g.goal.iconEmoji} ${g.goal.name}", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            // People Linking (Optional)
            if (state.people.isNotEmpty()) {
                Text("Link to Person (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.people.forEach { p ->
                        val isSelected = selectedPersonId == p.person.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPersonId = if (selectedPersonId == p.person.id) null else p.person.id
                            },
                            label = { Text("${p.person.avatarEmoji} ${p.person.name}", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

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
                            toAccountId = if (selectedType == TransactionType.TRANSFER) selectedToAccountId else null,
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
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Entry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
