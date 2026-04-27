package com.darkside.wallet.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkside.wallet.data.entity.BudgetEntity
import com.darkside.wallet.data.entity.BudgetPeriod
import com.darkside.wallet.data.entity.CategoryEntity
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val budgetProgress by viewModel.budgetProgress.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budgets", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddBudgetDialog = true },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget", tint = Color.White)
            }
        }
    ) { innerPadding ->
        if (budgetProgress.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PieChart,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No budgets set yet", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Track your spending by category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(budgetProgress) { item ->
                    BudgetListItem(item, onDelete = { 
                        // Implement delete budget if needed
                    })
                }
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("About Budgets") },
            text = {
                Text("Budgets help you control your spending by setting limits on specific categories. The progress bar shows how much you've spent compared to your limit.")
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) { Text("Got it") }
            }
        )
    }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            categories = categories,
            onDismiss = { showAddBudgetDialog = false },
            onConfirm = { amount, categoryId, period ->
                viewModel.addBudget(
                    amount = amount,
                    categoryId = categoryId,
                    period = period
                )
                showAddBudgetDialog = false
            }
        )
    }
}

@Composable
fun BudgetListItem(item: BudgetWithProgress, onDelete: () -> Unit) {
    val color = item.category?.color?.let { 
        try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
    } ?: MaterialTheme.colorScheme.primary
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = color)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(item.category?.name ?: "Unknown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${formatter.format(item.spent)} of ${formatter.format(item.budget.amount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(item.progress * 100).toInt()}% used", style = MaterialTheme.typography.labelMedium)
                if (item.progress > 1.0f) {
                    Text("Over budget!", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { item.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = if (item.progress > 1.0f) MaterialTheme.colorScheme.error else color,
                trackColor = color.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun AddBudgetDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Double, Long, BudgetPeriod) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    var selectedPeriod by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Budget") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Category", style = MaterialTheme.typography.labelLarge)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedCategory?.name ?: "Select Category")
                    }
                    DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = { selectedCategory = cat; categoryExpanded = false }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Period", style = MaterialTheme.typography.labelLarge)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { periodExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedPeriod.name)
                    }
                    DropdownMenu(expanded = periodExpanded, onDismissRequest = { periodExpanded = false }) {
                        BudgetPeriod.entries.forEach { period ->
                            DropdownMenuItem(
                                text = { Text(period.name) },
                                onClick = { selectedPeriod = period; periodExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    val catId = selectedCategory?.id
                    if (amt != null && catId != null) {
                        onConfirm(amt, catId, selectedPeriod)
                    }
                },
                enabled = amount.isNotEmpty() && selectedCategory != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
