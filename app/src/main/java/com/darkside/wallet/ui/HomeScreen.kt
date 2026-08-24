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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darkside.wallet.data.entity.TransactionEntity
import com.darkside.wallet.data.entity.TransactionType
import com.darkside.wallet.data.domain.CurrencyEngine
import com.darkside.wallet.ui.theme.Income
import com.darkside.wallet.ui.theme.Expense
import com.darkside.wallet.ui.theme.Primary
import com.darkside.wallet.ui.components.AppIcons
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WalletViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToSubMenu: (String) -> Unit,
    onLoansClick: () -> Unit = {}
) {
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val income by viewModel.totalIncome.collectAsStateWithLifecycle()
    val expense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val transactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhoto by viewModel.userPhotoPath.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header (1:1 with home_header.dart)
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
                        IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(48.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (userPhoto != null) {
                                    AsyncImage(
                                        model = File(userPhoto!!),
                                        contentDescription = "Profile",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        Column {
                            Text(
                                text = "Hello,",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (userName.isBlank()) "Guest" else userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { onNavigateToSubMenu("Search") },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Total Balance Card (1:1 with animated_balance_hero.dart)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background Blobs (Simplified replication)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Primary.copy(alpha = 0.1f),
                                radius = 300f,
                                center = Offset(-50f, -50f)
                            )
                            drawCircle(
                                color = Primary.copy(alpha = 0.05f),
                                radius = 400f,
                                center = Offset(size.width + 50f, size.height + 50f)
                            )
                        }

                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Total balance",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                IconButton(onClick = { viewModel.toggleBalanceVisibility() }) {
                                    Icon(
                                        if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isBalanceVisible) CurrencyEngine.formatCurrency(totalBalance, currencyCode) else "â€¢â€¢â€¢â€¢â€¢â€¢",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = if (isBalanceVisible) (-1).sp else 4.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "This month",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                if (isBalanceVisible && income > 0) {
                                    Text(
                                        text = "${((expense / income) * 100).toInt()}% spent",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MiniStatItem(Modifier.weight(1f), "Income", income, Income, isBalanceVisible, currencyCode, Icons.Default.TrendingUp)
                                MiniStatItem(Modifier.weight(1f), "Expense", expense, Expense, isBalanceVisible, currencyCode, Icons.Default.TrendingDown)
                            }
                        }
                    }
                }
            }

            // Overview Grid (1:1 with overview_card.dart)
            item {
                val gridItems = listOf(
                    Triple("Budgets", "View limits", "pie_chart"),
                    Triple("Assets", "Net worth", "account_balance"),
                    Triple("Bill Splitter", "Split costs", "share"),
                    Triple("Loans", "Manage debt", "currency_exchange"),
                    Triple("Goals", "Savings", "flag"),
                    Triple("Recurring", "Subscriptions", "refresh"),
                    Triple("Categories", "Organization", "category"),
                    Triple("Transactions", "History", "list")
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    gridItems.chunked(2).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowItems.forEach { (title, subtitle, iconName) ->
                                val onClickAction = when (title) {
                                    "Loans" -> onLoansClick
                                    "Transactions" -> { { onNavigateToSubMenu("Recent transactions") } }
                                    else -> { { onNavigateToSubMenu(title) } }
                                }
                                OverviewCardReplicated(Modifier.weight(1f), title, subtitle, iconName, onClickAction)
                            }
                        }
                    }
                }
            }

            // Activity Insights (Heatmap & Trends - 1:1 with activity_insights_section.dart)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .clickable { onNavigateToSubMenu("Activity History") },
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Calendar heatmap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "View full interactive activity calendar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FilledTonalButton(
                                    onClick = { onNavigateToSubMenu("Activity History") },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Open", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Recent Transactions List (Segmented squircle card container)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    TextButton(onClick = { onNavigateToSubMenu("Recent transactions") }) {
                        Text("View all", fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (transactions.isNotEmpty()) {
                item {
                    com.darkside.wallet.ui.components.TransactionSegmentedCard(
                        transactions = transactions.take(5),
                        currencyCode = currencyCode,
                        onTransactionClick = { onNavigateToSubMenu("Recent transactions") }
                    )
                }
            } else {
                item {
                    Text(
                        "No transactions yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun MiniStatItem(
    modifier: Modifier = Modifier,
    label: String,
    amount: Double,
    color: Color,
    isVisible: Boolean,
    currencyCode: String,
    icon: ImageVector
) {
    ContainerReplicated(
        modifier = modifier,
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(26.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    style = TextStyle(color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                )
                Text(
                    text = if (isVisible) CurrencyEngine.formatCurrency(amount, currencyCode) else "â€¢â€¢â€¢",
                    style = TextStyle(fontWeight = FontWeight.Black, color = color.copy(alpha = 0.8f), fontSize = 14.sp)
                )
            }
        }
    }
}

@Composable
fun OverviewCardReplicated(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconName: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(
                    AppIcons.getIcon(iconName),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TransactionListTileReplicated(tx: TransactionEntity, currencyCode: String) {
    val isIncome = tx.type == TransactionType.INCOME
    
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    AppIcons.getIcon(tx.icon ?: if (isIncome) "trending_up" else "shopping_bag"),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.note ?: "Transaction",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date(tx.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${if (isIncome) "+" else "-"}${CurrencyEngine.formatCurrency(tx.amount, currencyCode)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444),
                letterSpacing = (-0.5).sp
            )
        }
    }
}

@Composable
fun ContainerReplicated(
    modifier: Modifier = Modifier,
    color: Color,
    shape: androidx.compose.ui.graphics.Shape,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.clip(shape).background(color)) {
        content()
    }
}
