package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSplitterScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    var totalAmount by remember { mutableStateOf("") }
    var numPeople by remember { mutableStateOf("2") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bill Splitter") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Amount", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = totalAmount,
                        onValueChange = { totalAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹") },
                        textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black)
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

            Spacer(modifier = Modifier.height(24.dp))

            val total = totalAmount.toDoubleOrNull() ?: 0.0
            val people = numPeople.toIntOrNull() ?: 1
            val perPerson = if (people > 0) total / people else 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Each Person Pays", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text("₹${"%.2f".format(perPerson)}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Save to Transactions as split? */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Split & Record")
            }
        }
    }
}
