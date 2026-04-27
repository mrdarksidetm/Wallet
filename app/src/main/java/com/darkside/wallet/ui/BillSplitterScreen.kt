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
import com.darkside.wallet.data.entity.LoanType
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSplitterScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    var totalAmount by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("0") }
    var tipPercent by remember { mutableStateOf("0") }
    
    val persons by viewModel.persons.collectAsState()
    val selectedPersonIds = remember { mutableStateListOf<Long>() }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    val amount = totalAmount.toDoubleOrNull() ?: 0.0
    val tax = taxPercent.toDoubleOrNull() ?: 0.0
    val tip = tipPercent.toDoubleOrNull() ?: 0.0
    
    val taxAmount = amount * (tax / 100.0)
    val tipAmount = amount * (tip / 100.0)
    val grandTotal = amount + taxAmount + tipAmount
    
    val totalPeopleCount = selectedPersonIds.size + 1 // +1 for "You"
    val perPerson = if (totalPeopleCount > 0) grandTotal / totalPeopleCount else 0.0

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Bill Splitter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Total Per Person",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            formatter.format(perPerson),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(formatter.format(grandTotal), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = totalAmount,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) totalAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Bill Amount") },
                        prefix = { Text("₹") },
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = taxPercent,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) taxPercent = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Tax (%)") },
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        OutlinedTextField(
                            value = tipPercent,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) tipPercent = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Tip (%)") },
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    "Split With (${totalPeopleCount})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                if (persons.isEmpty()) {
                    Text("No people added yet. Add them in People section.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(persons) { person ->
                            FilterChip(
                                selected = selectedPersonIds.contains(person.id),
                                onClick = { 
                                    if (selectedPersonIds.contains(person.id)) {
                                        selectedPersonIds.remove(person.id)
                                    } else {
                                        selectedPersonIds.add(person.id)
                                    }
                                },
                                label = { Text(person.name) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                shape = CircleShape
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (amount <= 0) return@Button
                        
                        viewModel.addLoans(
                            personIds = selectedPersonIds.toList(),
                            amount = perPerson,
                            type = LoanType.LENT,
                            note = "Split from ₹${totalAmount} bill"
                        )
                        
                        scope.launch { 
                            snackbarHostState.showSnackbar("Split saved as Loans for ${selectedPersonIds.size} people")
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = amount > 0 && selectedPersonIds.isNotEmpty()
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finalize & Record Loans")
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
