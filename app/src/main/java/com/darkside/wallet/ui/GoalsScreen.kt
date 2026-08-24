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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.data.entity.GoalEntity
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.ui.utils.ExpressiveCard
import com.darkside.wallet.ui.utils.ExpressiveProgressBar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val goals by viewModel.goals.collectAsStateWithLifecycle(initialValue = emptyList())
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
    var showAddGoalDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Goals", fontWeight = FontWeight.Bold) },
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
                onClick = { showAddGoalDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Goal") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { innerPadding ->
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.TrackChanges,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No goals yet", style = MaterialTheme.typography.titleMedium)
                    Text("Save up for what matters", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(goals) { goal ->
                    GoalListItem(
                        goal = goal, 
                        currencyCode = currencyCode,
                        onUpdateSaved = { amt -> viewModel.updateGoalSavedAmount(goal, amt) },
                        onDelete = { viewModel.deleteGoal(goal) }
                    )
                }
            }
        }
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { name, amount ->
                viewModel.addGoal(name, amount, System.currentTimeMillis() + 31536000000L) // 1 year default
                showAddGoalDialog = false
            }
        )
    }
}

@Composable
fun GoalListItem(
    goal: GoalEntity, 
    currencyCode: String,
    onUpdateSaved: (Double) -> Unit,
    onDelete: () -> Unit
) {
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    var showUpdateDialog by remember { mutableStateOf(false) }
    
    ExpressiveCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { showUpdateDialog = true }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Target: ${CurrencyEngine.formatCurrency(goal.targetAmount, currencyCode)}",
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
                progress = progress,
                label = "Saved: ${CurrencyEngine.formatCurrency(goal.currentAmount, currencyCode)}",
                color = if (progress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showUpdateDialog) {
        var amountText by remember { mutableStateOf(goal.currentAmount.toString()) }
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Progress") },
            text = {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amountText = it },
                    label = { Text("Amount Saved") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        onUpdateSaved(amt)
                        showUpdateDialog = false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amount = it },
                    label = { Text("Target Amount") },
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
                    if (name.isNotBlank() && amt > 0) {
                        onConfirm(name, amt)
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Goal")
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
