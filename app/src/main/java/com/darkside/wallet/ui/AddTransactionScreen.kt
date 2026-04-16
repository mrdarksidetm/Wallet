package com.darkside.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.darkside.wallet.data.entity.*
import com.darkside.wallet.data.domain.CurrencyEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    
    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val people by viewModel.persons.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var selectedPersonId by remember { mutableStateOf<Long?>(null) }
    var transferAccountId by remember { mutableStateOf<Long?>(null) }

    var showPersonDialog by remember { mutableStateOf(false) }
    var newPersonName by remember { mutableStateOf("") }

    var expandedCat by remember { mutableStateOf(false) }
    var expandedAcc by remember { mutableStateOf(false) }
    var expandedPerson by remember { mutableStateOf(false) }
    var expandedTransfer by remember { mutableStateOf(false) }

    // Initialize defaults
    LaunchedEffect(accounts, categories) {
        if (selectedAccountId == null) selectedAccountId = accounts.firstOrNull()?.id
        if (selectedCategoryId == null) selectedCategoryId = categories.firstOrNull { it.type.name == transactionType.name }?.id
    }

    if (showPersonDialog) {
        AlertDialog(
            onDismissRequest = { showPersonDialog = false },
            title = { Text("Add Person") },
            text = {
                OutlinedTextField(
                    value = newPersonName,
                    onValueChange = { newPersonName = it },
                    label = { Text("Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPersonName.isNotBlank()) {
                        viewModel.addPerson(newPersonName)
                        newPersonName = ""
                        showPersonDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showPersonDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TransactionType.values().forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = transactionType == type,
                        onClick = { 
                            transactionType = type
                            selectedCategoryId = categories.firstOrNull { it.type.name == type.name }?.id
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = TransactionType.values().size)
                    ) {
                        Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            // Account Selector
            ExposedDropdownMenuBox(
                expanded = expandedAcc,
                onExpandedChange = { expandedAcc = !expandedAcc },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = accounts.find { it.id == selectedAccountId }?.name ?: "Select Account",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Account") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAcc) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedAcc, onDismissRequest = { expandedAcc = false }) {
                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.name) },
                            onClick = {
                                selectedAccountId = account.id
                                expandedAcc = false
                            }
                        )
                    }
                }
            }

            if (transactionType == TransactionType.TRANSFER) {
                ExposedDropdownMenuBox(
                    expanded = expandedTransfer,
                    onExpandedChange = { expandedTransfer = !expandedTransfer },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = accounts.find { it.id == transferAccountId }?.name ?: "Select To Account",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("To Account") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTransfer) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedTransfer, onDismissRequest = { expandedTransfer = false }) {
                        accounts.filter { it.id != selectedAccountId }.forEach { account ->
                            DropdownMenuItem(
                                text = { Text(account.name) },
                                onClick = {
                                    transferAccountId = account.id
                                    expandedTransfer = false
                                }
                            )
                        }
                    }
                }
            } else {
                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = expandedCat,
                    onExpandedChange = { expandedCat = !expandedCat },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categories.find { it.id == selectedCategoryId }?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                        categories.filter { it.type.name == transactionType.name }.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }
            }

            // Person Selector (Parity with Flutter Quick Add)
            ExposedDropdownMenuBox(
                expanded = expandedPerson,
                onExpandedChange = { expandedPerson = !expandedPerson },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = people.find { it.id == selectedPersonId }?.name ?: "With Person (Optional)",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Person") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPerson) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedPerson, onDismissRequest = { expandedPerson = false }) {
                    DropdownMenuItem(
                        text = { Text("Add New Person", fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.Add, null) },
                        onClick = {
                            expandedPerson = false
                            showPersonDialog = true
                        }
                    )
                    HorizontalDivider()
                    people.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                selectedPersonId = person.id
                                expandedPerson = false
                            }
                        )
                    }
                }
            }

            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (selectedAccountId != null && (selectedCategoryId != null || transactionType == TransactionType.TRANSFER)) {
                        viewModel.addTransaction(
                            amount = amt,
                            note = note,
                            categoryId = if (transactionType == TransactionType.TRANSFER) 0L else (selectedCategoryId ?: 0L),
                            type = transactionType,
                            accountId = selectedAccountId!!,
                            transferAccountId = if (transactionType == TransactionType.TRANSFER) transferAccountId else null,
                            personId = selectedPersonId ?: 0L,
                            onComplete = {
                                try {
                                    com.darkside.wallet.utils.HapticEngine.performSuccessPulse(context)
                                } catch (e: Exception) { /* Ignore haptic errors */ }
                                onBack()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Transaction")
                }
            }
        }
    }
}
