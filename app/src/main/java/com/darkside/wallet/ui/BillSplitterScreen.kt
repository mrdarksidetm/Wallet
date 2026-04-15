package com.darkside.wallet.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSplitterScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    var totalAmount by remember { mutableStateOf("") }
    var numPeople by remember { mutableStateOf("2") }
    val persons by viewModel.persons.collectAsState()
    var selectedPersonId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val total = totalAmount.toDoubleOrNull() ?: 0.0
    val people = numPeople.toIntOrNull() ?: 1
    val perPerson = if (people > 0) total / people else 0.0

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
                            "Each Person Pays",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            "₹${"%.2f".format(perPerson)}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Amount", style = MaterialTheme.typography.labelLarge)
                        OutlinedTextField(
                            value = totalAmount,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) totalAmount = it },
                            modifier = Modifier.fillMaxWidth(),
                            prefix = { Text("₹") },
                            textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Number of People", style = MaterialTheme.typography.labelLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { numPeople = (numPeople.toIntOrNull()?.minus(1)?.coerceAtLeast(1) ?: 1).toString() }) {
                                Icon(Icons.Default.Remove, contentDescription = null)
                            }
                            Text(numPeople, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                            IconButton(onClick = { numPeople = (numPeople.toIntOrNull()?.plus(1) ?: 1).toString() }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Split with Person (Create Loan)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                if (persons.isEmpty()) {
                    Text("No people added yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(persons) { person ->
                            FilterChip(
                                selected = selectedPersonId == person.id,
                                onClick = { 
                                    selectedPersonId = if (selectedPersonId == person.id) null else person.id 
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
                        if (total <= 0) return@Button
                        if (selectedPersonId == null) {
                            scope.launch { snackbarHostState.showSnackbar("Please select a person to create a loan") }
                            return@Button
                        }
                        
                        viewModel.addLoan(
                            personId = selectedPersonId!!,
                            amount = perPerson,
                            type = "lent",
                            note = "Bill split: ₹${totalAmount}"
                        )
                        scope.launch { snackbarHostState.showSnackbar("Loan created for ${persons.find { it.id == selectedPersonId }?.name}") }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = total > 0 && selectedPersonId != null
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Split & Record Loan")
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
