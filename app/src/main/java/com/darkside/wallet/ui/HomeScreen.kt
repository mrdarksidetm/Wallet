package com.darkside.wallet.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darkside.wallet.data.entity.TransactionEntity
import com.darkside.wallet.data.entity.TransactionType
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.ui.theme.Income
import com.darkside.wallet.ui.theme.Expense
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WalletViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToSubMenu: (String) -> Unit,
    onLoansClick: () -> Unit = {}
) {
    val totalBalance by viewModel.totalBalance.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expense by viewModel.totalExpense.collectAsState()
    val transactions by viewModel.recentTransactions.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userPhoto by viewModel.userPhotoPath.collectAsState()
    val currencyCode by viewModel.currencyCode.collectAsState()

    var isBalanceVisible by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Subtle background gradient for "Stitch" depth
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0061A4).copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Good day,",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // User Profile
                        IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(44.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                            ) {
                                if (userPhoto != null) {
                                    AsyncImage(
                                        model = java.io.File(userPhoto!!),
                                        contentDescription = "Profile",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = userName.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Total Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total balance",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            IconButton(onClick = { isBalanceVisible = !isBalanceVisible }) {
                                Icon(
                                    if (isBalanceVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        AnimatedContent(
                            targetState = isBalanceVisible,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "balance_visibility"
                        ) { visible ->
                            Text(
                                text = if (visible) CurrencyEngine.formatCurrency(totalBalance, currencyCode) else "••••••",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(vertical = 4.dp),
                                letterSpacing = if (visible) (-1).sp else 4.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BalanceSummaryItem("Income", income, Income, currencyCode)
                            BalanceSummaryItem("Expense", expense, Expense, currencyCode)
                        }
                    }
                }
            }

            // Overview Grid
            item {
                val gridItems = listOf(
                    Pair("Budgets", Icons.Default.PieChart) to "View limits",
                    Pair("Assets", Icons.Default.AccountBalance) to "Net worth",
                    Pair("Bill Splitter", Icons.Default.Share) to "Split costs",
                    Pair("Loans", Icons.Default.CurrencyExchange) to "Manage debt",
                    Pair("Goals", Icons.Default.Star) to "Savings",
                    Pair("Recurring", Icons.Default.Refresh) to "Subscriptions",
                    Pair("Categories", Icons.AutoMirrored.Filled.List) to "Organization",
                    Pair("Recent transactions", Icons.AutoMirrored.Filled.List) to "History"
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    gridItems.chunked(2).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowItems.forEach { (meta, subtitle) ->
                                val (title, icon) = meta
                                val onClickAction: () -> Unit = if (title == "Loans") onLoansClick else { { onNavigateToSubMenu(title) } }
                                OverviewGridItem(Modifier.weight(1f), icon, title, subtitle, onClickAction)
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigateToSubMenu("Recent transactions") }) {
                        Text("View all")
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(
                    items = transactions.take(5),
                    key = { it.id }
                ) { transaction ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                    ) {
                        TransactionListItem(transaction, currencyCode)
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun BalanceSummaryItem(label: String, amount: Double, color: Color, currencyCode: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = CurrencyEngine.formatCurrency(amount, currencyCode),
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OverviewGridItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TransactionListItem(transaction: TransactionEntity, currencyCode: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (transaction.type == TransactionType.INCOME) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = if (transaction.type == TransactionType.INCOME) Income else Expense
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.note ?: "Transaction",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transaction.type.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${CurrencyEngine.formatCurrency(transaction.amount, currencyCode)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == TransactionType.INCOME) Income else Expense
        )
    }
}
