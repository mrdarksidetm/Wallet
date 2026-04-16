package com.darkside.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.darkside.wallet.data.entity.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(AccountType.WALLET) }
    var initialBalance by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val accountTypes = AccountType.values()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Account") },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = type.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Account Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    accountTypes.forEach { accountType ->
                        DropdownMenuItem(
                            text = { Text(accountType.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                type = accountType
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = initialBalance,
                onValueChange = { initialBalance = it },
                label = { Text("Initial Balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val balance = initialBalance.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        viewModel.addAccount(name, type, balance)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Account")
            }
        }
    }
}
