package com.darkside.wallet.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.darkside.wallet.data.LoanEntity
import com.darkside.wallet.data.PersonEntity
import com.darkside.wallet.utils.FileUtils
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lent", "Borrowed", "People")
    val persons by viewModel.persons.collectAsState()
    val loans by viewModel.loans.collectAsState()
    
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showAddLoanDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loans") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                if (selectedTab == 2) showAddPersonDialog = true else showAddLoanDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> LoanList(loans.filter { it.type == "Lent" }, persons, viewModel)
                1 -> LoanList(loans.filter { it.type == "Borrowed" }, persons, viewModel)
                2 -> PersonList(persons, viewModel)
            }
        }
    }

    if (showAddPersonDialog) {
        AddPersonDialog(
            viewModel = viewModel,
            onDismiss = { showAddPersonDialog = false }
        )
    }

    if (showAddLoanDialog) {
        AddLoanDialog(
            persons = persons,
            type = if (selectedTab == 0) "Lent" else "Borrowed",
            onDismiss = { showAddLoanDialog = false },
            onConfirm = { personId, amount, note, type ->
                viewModel.addLoan(personId, amount, type, note)
                showAddLoanDialog = false
            }
        )
    }
}

@Composable
fun PersonAvatar(photoPath: String?, name: String, size: dp = 40.dp) {
    val hasValidPhoto = FileUtils.fileExists(photoPath)
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (hasValidPhoto) {
            AsyncImage(
                model = File(photoPath!!),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun LoanList(loans: List<LoanEntity>, persons: List<PersonEntity>, viewModel: WalletViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(loans) { loan ->
            val person = persons.find { it.id == loan.personId }
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                onClick = { viewModel.toggleLoanSettled(loan) }
            ) {
                ListItem(
                    headlineContent = { Text(person?.name ?: "Unknown") },
                    supportingContent = { Text(loan.note) },
                    leadingContent = { PersonAvatar(person?.photoPath, person?.name ?: "?") },
                    trailingContent = { 
                        Text(
                            text = "₹${"%.2f".format(loan.amount)}",
                            color = if (loan.isSettled) Color.Gray else if (loan.type == "Lent") Color(0xFF4CAF50) else Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    overlineContent = { if (loan.isSettled) Text("SETTLED", color = Color.Gray) }
                )
            }
        }
    }
}

@Composable
fun PersonList(persons: List<PersonEntity>, viewModel: WalletViewModel) {
    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // This is tricky because we need to know which person.
        // For simplicity, we'll skip direct edit here or use a dialog.
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(persons) { person ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                ListItem(
                    headlineContent = { Text(person.name) },
                    leadingContent = { PersonAvatar(person.photoPath, person.name) },
                    trailingContent = {
                        IconButton(onClick = { viewModel.deletePerson(person) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AddPersonDialog(viewModel: WalletViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        tempPhotoUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Person") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { photoLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (tempPhotoUri != null) {
                        AsyncImage(
                            model = tempPhotoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (name.isNotBlank()) {
                    viewModel.addPerson(context, name, tempPhotoUri)
                    onDismiss()
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddLoanDialog(
    persons: List<PersonEntity>,
    type: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String) -> Unit
) {
    var personId by remember { mutableStateOf(persons.firstOrNull()?.id ?: "") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add $type Loan") },
        text = {
            Column {
                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(persons.find { it.id == personId }?.name ?: "Select Person")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        persons.forEach { person ->
                            DropdownMenuItem(
                                text = { Text(person.name) },
                                onClick = {
                                    personId = person.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val amt = amount.toDoubleOrNull()
                if (personId.isNotBlank() && amt != null) onConfirm(personId, amt, note, type) 
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
