package com.darkside.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkside.wallet.data.entity.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val recurringItems by viewModel.recurringTransactions.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recurring", tint = Color.White)
            }
        }
    ) { innerPadding ->
        if (recurringItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No recurring transactions", style = MaterialTheme.typography.titleMedium)
                    Text("Automate your regular bills", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recurringItems) { item ->
                    RecurringListItem(
                        item = item,
                        onDelete = { viewModel.deleteRecurring(item) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddRecurringDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, name, cat, type, freq ->
                viewModel.addRecurring(
                    amount = amount,
                    name = name,
                    categoryId = 1, // Default for simple UI
                    type = if (type == "Income") TransactionType.INCOME else TransactionType.EXPENSE,
                    frequency = when (freq) {
                        "Daily" -> RecurrenceFrequency.DAILY
                        "Weekly" -> RecurrenceFrequency.WEEKLY
                        else -> RecurrenceFrequency.MONTHLY
                    }
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RecurringListItem(item: RecurringEntity, onDelete: () -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Repeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = "${item.frequency} • Next: ${java.text.SimpleDateFormat("dd MMM", Locale.getDefault()).format(item.nextDate)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatter.format(item.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (item.type == TransactionType.INCOME) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AddRecurringDialog(onDismiss: () -> Unit, onConfirm: (Double, String, String, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedFreq by remember { mutableStateOf("Monthly") }
    var selectedType by remember { mutableStateOf("Expense") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Recurring") },
        text = {
            Column {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                // Simple frequency selector
                Text("Frequency", style = MaterialTheme.typography.labelSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Daily", "Weekly", "Monthly").forEach { freq ->
                        FilterChip(
                            selected = selectedFreq == freq,
                            onClick = { selectedFreq = freq },
                            label = { Text(freq) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amt = amount.toDoubleOrNull() ?: 0.0
                if (amt > 0) onConfirm(amt, note, "Other", selectedType, selectedFreq)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
