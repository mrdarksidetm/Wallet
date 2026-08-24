package com.darkside.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.data.entity.BudgetPeriod
import com.darkside.wallet.ui.utils.ExpressiveCard
import com.darkside.wallet.ui.utils.ExpressiveProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val budgetProgress by viewModel.budgetProgress.collectAsStateWithLifecycle(initialValue = emptyList())
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    
    var showAddBudgetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budgets", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddBudgetDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Set Budget") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = budgetProgress, key = { it.budget.id }) { item ->
                    BudgetListItem(
                        item = item, 
                        currencyCode = currencyCode,
                        onDelete = { viewModel.deleteBudget(item.budget) }
                    )
                }
            }
        }
    }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            categories = categories,
            onDismiss = { showAddBudgetDialog = false },
            onConfirm = { categoryId, amount ->
                viewModel.addBudget(amount, categoryId, BudgetPeriod.MONTHLY)
                showAddBudgetDialog = false
            }
        )
    }
}

@Composable
fun BudgetListItem(
    item: BudgetWithProgress, 
    currencyCode: String,
    onDelete: () -> Unit
) {
    ExpressiveCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.category?.name ?: "General",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Limit: ${CurrencyEngine.formatCurrency(item.budget.amount, currencyCode)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            ExpressiveProgressBar(
                progress = item.progress,
                label = "Spent: ${CurrencyEngine.formatCurrency(item.spent, currencyCode)}",
                color = if (item.progress > 0.9f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AddBudgetDialog(
    categories: List<com.darkside.wallet.data.entity.CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Double) -> Unit
) {
    var selectedCategoryId by remember { mutableLongStateOf(categories.firstOrNull()?.id ?: 0L) }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Category Budget") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(categories.find { it.id == selectedCategoryId }?.name ?: "Select Category")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amount = it },
                    label = { Text("Monthly Limit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    prefix = { Text("₹") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (selectedCategoryId != 0L && amt > 0) {
                        onConfirm(selectedCategoryId, amt)
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Set Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
