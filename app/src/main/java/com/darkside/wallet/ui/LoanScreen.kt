package com.darkside.wallet.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.data.entity.LoanEntity
import com.darkside.wallet.data.entity.LoanType
import com.darkside.wallet.data.entity.PersonEntity
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.ui.theme.Expense
import com.darkside.wallet.ui.theme.Income
import com.darkside.wallet.utils.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val loans by viewModel.loans.collectAsStateWithLifecycle()
    val persons by viewModel.persons.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()

    var isBorrowedExpanded by remember { mutableStateOf(true) }
    var isLentExpanded by remember { mutableStateOf(true) }
    var showAddLoanDialog by remember { mutableStateOf(false) }

    val borrowedLoans = remember(loans) { loans.filter { it.type == LoanType.BORROWED } }
    val lentLoans = remember(loans) { loans.filter { it.type == LoanType.LENT } }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Loans & Debts",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    AppBackButton(onBack = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLoanDialog = true },
                containerColor = colorScheme.primaryContainer,
                contentColor = colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Loan")
            }
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        if (loans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No loans or debts found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (borrowedLoans.isNotEmpty()) {
                    item {
                        LoanCollapsibleSection(
                            title = "Borrowed",
                            subtitle = "You owe others",
                            items = borrowedLoans,
                            persons = persons,
                            accentColor = Expense,
                            currencyCode = currencyCode,
                            isExpanded = isBorrowedExpanded,
                            onToggle = { isBorrowedExpanded = !isBorrowedExpanded },
                            onToggleSettled = { viewModel.toggleLoanSettled(it) },
                            onDelete = { viewModel.deleteLoan(it) }
                        )
                    }
                }

                if (lentLoans.isNotEmpty()) {
                    item {
                        LoanCollapsibleSection(
                            title = "Lent",
                            subtitle = "Others owe you",
                            items = lentLoans,
                            persons = persons,
                            accentColor = Income,
                            currencyCode = currencyCode,
                            isExpanded = isLentExpanded,
                            onToggle = { isLentExpanded = !isLentExpanded },
                            onToggleSettled = { viewModel.toggleLoanSettled(it) },
                            onDelete = { viewModel.deleteLoan(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAddLoanDialog) {
        AddLoanBottomSheet(
            persons = persons,
            currencyCode = currencyCode,
            onDismiss = { showAddLoanDialog = false },
            onConfirm = { personId, amount, note, type ->
                viewModel.addLoan(personId, amount, type, note)
                showAddLoanDialog = false
            }
        )
    }
}

@Composable
fun LoanCollapsibleSection(
    title: String,
    subtitle: String,
    items: List<LoanEntity>,
    persons: List<PersonEntity>,
    accentColor: Color,
    currencyCode: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onToggleSettled: (LoanEntity) -> Unit,
    onDelete: (LoanEntity) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 180f,
        label = "loan_arrow_rotation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colorScheme.onSurfaceVariant
                    )
                )
            }

            FilledTonalIconButton(
                onClick = onToggle,
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(arrowRotation).size(20.dp)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items.forEach { loan ->
                    val person = persons.find { it.id == loan.personId }
                    LoanItemCard(
                        loan = loan,
                        person = person,
                        accentColor = accentColor,
                        currencyCode = currencyCode,
                        onToggleSettled = { onToggleSettled(loan) },
                        onDelete = { onDelete(loan) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanItemCard(
    loan: LoanEntity,
    person: PersonEntity?,
    accentColor: Color,
    currencyCode: String,
    onToggleSettled: () -> Unit,
    onDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isPaid = loan.isPaid

    val dateStr = if (loan.dueDate != null) {
        "Due: ${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(loan.dueDate!!))}"
    } else "No due date"

    val hasValidAvatar = person?.avatar != null && FileUtils.fileExists(person.avatar)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPaid) colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
            else colorScheme.surfaceContainerLow
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleSettled() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = if (isPaid) 0.08f else 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (hasValidAvatar) {
                    AsyncImage(
                        model = File(person!!.avatar!!),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = if (loan.type == LoanType.LENT) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                        contentDescription = null,
                        tint = if (isPaid) accentColor.copy(alpha = 0.5f) else accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person?.name ?: (loan.note ?: "Unknown"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isPaid) TextDecoration.LineThrough else null,
                        color = if (isPaid) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (loan.note.isNullOrBlank()) dateStr else "${loan.note} • $dateStr",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isPaid) colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyEngine.formatCurrency(loan.amount, currencyCode),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = if (isPaid) accentColor.copy(alpha = 0.5f) else accentColor
                    )
                )
                if (isPaid) {
                    Text(
                        text = "SETTLED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanBottomSheet(
    persons: List<PersonEntity>,
    currencyCode: String,
    onDismiss: () -> Unit,
    onConfirm: (Long, Double, String?, LoanType) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedType by remember { mutableStateOf(LoanType.LENT) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var selectedPersonId by remember { mutableLongStateOf(persons.firstOrNull()?.id ?: 0L) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add Loan / Debt",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            // Type Toggle
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedType == LoanType.LENT,
                    onClick = { selectedType = LoanType.LENT },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Lent (Owed to You)", fontWeight = FontWeight.Bold)
                }
                SegmentedButton(
                    selected = selectedType == LoanType.BORROWED,
                    onClick = { selectedType = LoanType.BORROWED },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Borrowed (You Owe)", fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount ($currencyCode)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note / Description") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(selectedPersonId, amount, noteText.trim().ifEmpty { null }, selectedType)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Loan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
