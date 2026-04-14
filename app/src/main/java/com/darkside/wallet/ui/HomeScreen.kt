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
import com.darkside.wallet.data.TransactionEntity
import com.darkside.wallet.ui.theme.PaisaGreen
import com.darkside.wallet.ui.theme.PaisaRed
import java.text.NumberFormat
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
    val income by viewModel.thisMonthIncome.collectAsState()
    val expense by viewModel.thisMonthExpense.collectAsState()
    val transactions by viewModel.recentTransactions.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userPhoto by viewModel.userPhotoPath.collectAsState()
    val heatmapData by viewModel.heatmapData.collectAsState()
    val incomeTrends by viewModel.incomeTrends.collectAsState()
    val expenseTrends by viewModel.expenseTrends.collectAsState()
    val currencyCode by viewModel.currencyCode.collectAsState()

    var isBalanceVisible by remember { mutableStateOf(true) }
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Box(modifier = Modifier.fillMaxSize()) {
        // Subtle background gradient for "Stitch" depth
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD2691E).copy(alpha = 0.05f),
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
                                text = "Good late night",
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
                        // Premium Banner - Stitch Style Glassy
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            modifier = Modifier.clickable { onNavigateToSubMenu("Premium") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = "Premium", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text("PRO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }

                        // UserShape
                        IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(44.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                            ) {
                                val hasValidPhoto = com.darkside.wallet.utils.FileUtils.fileExists(userPhoto)
                                if (hasValidPhoto) {
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
                                    ) {
                                        Text(userName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Total Balance Card - Enhanced with Glassmorphism and Animations
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
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "balance_visibility"
                        ) { visible ->
                            Text(
                                text = if (visible) formatter.format(totalBalance) else "••••••",  
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
                            BalanceSummaryItem("Income", income, Color(0xFF10B981))
                            BalanceSummaryItem("Expense", expense, Color(0xFFEF4444))
                        }
                    }
                }
            }

            // Overview Grid
            item {
                val gridItems = listOf(
                    Pair("Budgets", Icons.Default.PieChart) to "₹0.00 left",
                    Pair("Assets", Icons.Default.AccountBalance) to "₹0.00 total",
                    Pair("Bill Splitter", Icons.Default.Share) to "0 active",
                    Pair("Loans", Icons.Default.CurrencyExchange) to "₹0.00 due",
                    Pair("Goals", Icons.Default.Star) to "0 active",
                    Pair("Labels", Icons.Default.Info) to "0 tags",
                    Pair("Analytics", Icons.Default.Timeline) to "View charts",
                    Pair("Recurring", Icons.Default.Refresh) to "0 active",
                    Pair("Categories", Icons.AutoMirrored.Filled.List) to "12 items",
                    Pair("Weekly", Icons.Default.DateRange) to "This week",
                    Pair("Places", Icons.Default.LocationOn) to "0 locations",
                    Pair("Person", Icons.Default.Person) to "0 people",
                    Pair("Calendar heatmap", Icons.Default.DateRange) to "Activity",
                    Pair("Trend", Icons.AutoMirrored.Filled.TrendingUp) to "Growth",
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

            // Activity Heatmap
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                ) {
                    com.darkside.wallet.ui.screens.ActivityHeatmap(
                        modifier = Modifier.padding(16.dp),
                        heatmapData = heatmapData
                    )
                }
            }

            // Trends Chart
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                ) {
                    com.darkside.wallet.ui.screens.TrendsChart(
                        modifier = Modifier.padding(16.dp),
                        incomeTrends = incomeTrends,
                        expenseTrends = expenseTrends
                    )
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

            // Transaction List with animateItem()
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
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { state ->
                            when (state) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    viewModel.archiveTransaction(transaction.id)
                                    true
                                }
                                SwipeToDismissBoxValue.EndToStart -> {
                                    viewModel.deleteTransaction(transaction)
                                    true
                                }
                                SwipeToDismissBoxValue.Settled -> false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        modifier = Modifier.animateItem(), // Compose 1.7+ specific
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF2196F3)
                                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336)
                                },
                                label = "dismiss_background_color"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color)
                                    .padding(horizontal = 24.dp),
                                contentAlignment = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                    else -> Alignment.Center
                                }
                            ) {
                                val icon = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Archive else Icons.Default.Delete
                                if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
                                    Icon(icon, contentDescription = null, tint = Color.White)
                                }
                            }
                        },
                        content = {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                            ) {
                                TransactionListItem(transaction)
                            }
                        }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun BalanceSummaryItem(label: String, amount: Double, color: Color) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = formatter.format(amount), style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
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
fun TransactionListItem(transaction: TransactionEntity) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
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
                if (transaction.type == "Income") Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.ShoppingBag,      
                contentDescription = null,
                tint = if (transaction.type == "Income") PaisaGreen else PaisaRed
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.note.ifBlank { transaction.category }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = transaction.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = "${if (transaction.type == "Income") "+" else "-"}${formatter.format(transaction.amount)}",  
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == "Income") PaisaGreen else PaisaRed
        )
    }
}
