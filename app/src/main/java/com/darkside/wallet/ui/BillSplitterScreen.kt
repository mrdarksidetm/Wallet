package com.darkside.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.ui.utils.ExpressiveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSplitterScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val persons by viewModel.persons.collectAsStateWithLifecycle(initialValue = emptyList())
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
    
    var totalAmount by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("") }
    var tipPercent by remember { mutableStateOf("") }
    val selectedPersonIds = remember { mutableStateListOf<Long>() }
    
    val amount = totalAmount.toDoubleOrNull() ?: 0.0
    val tax = taxPercent.toDoubleOrNull() ?: 0.0
    val tip = tipPercent.toDoubleOrNull() ?: 0.0
    
    val taxAmount = amount * (tax / 100.0)
    val tipAmount = amount * (tip / 100.0)
    val grandTotal = amount + taxAmount + tipAmount
    
    val totalPeopleCount = selectedPersonIds.size + 1 // +1 for "You"
    val perPerson = if (totalPeopleCount > 0) grandTotal / totalPeopleCount else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bill Splitter", fontWeight = FontWeight.Bold) },
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary Card
            item {
                ExpressiveCard(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Each person pays",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = CurrencyEngine.formatCurrency(perPerson, currencyCode),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Grand Total: ${CurrencyEngine.formatCurrency(grandTotal, currencyCode)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Input Fields
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = totalAmount,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) totalAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Bill Amount") },
                        prefix = { Text("₹") },
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = taxPercent,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) taxPercent = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Tax %") },
                            suffix = { Text("%") },
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = tipPercent,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) tipPercent = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Tip %") },
                            suffix = { Text("%") },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // Person Selector
            item {
                Column {
                    Text(
                        text = "Split with ($totalPeopleCount people)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (persons.isEmpty()) {
                        Text(
                            "No people added yet. Add them in People section.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            // "You" avatar
                            item {
                                PersonChip(
                                    name = "You",
                                    isSelected = true,
                                    onClick = {}
                                )
                            }
                            
                            items(persons) { person ->
                                PersonChip(
                                    name = person.name,
                                    isSelected = selectedPersonIds.contains(person.id),
                                    onClick = {
                                        if (selectedPersonIds.contains(person.id)) {
                                            selectedPersonIds.remove(person.id)
                                        } else {
                                            selectedPersonIds.add(person.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(name) },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else {
            { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
        },
        shape = RoundedCornerShape(12.dp)
    )
}
