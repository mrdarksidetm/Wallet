package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WalletViewModel,
    onNavigateToSettings: () -> Unit
) {
    val totalBalance by viewModel.totalBalance.collectAsState()
    val income by viewModel.thisMonthIncome.collectAsState()
    val expense by viewModel.thisMonthExpense.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val userName by viewModel.userName.collectAsState()

    val primaryColor = Color(0xFFD2691E)
    val bgColor = Color(0xFF1A140F)
    val cardColor = Color(0xFF251C15)
    val onBgColor = Color.White
    val textMuted = Color(0xFF94A3B8)
    val incomeColor = Color(0xFF10B981)

    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            // Simplified custom bottom bar to mimic the HTML design
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(cardColor.copy(alpha = 0.9f))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(40.dp))
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(Icons.Filled.Home, "Home", primaryColor, true)
                    BottomNavItem(Icons.Outlined.CreditCard, "Accounts", textMuted, false)
                    Spacer(modifier = Modifier.width(64.dp))
                    BottomNavItem(Icons.Outlined.SyncAlt, "Reports", textMuted, false)
                    BottomNavItem(Icons.Outlined.Search, "Search", textMuted, false)
                }

                // FAB
                FloatingActionButton(
                    onClick = { },
                    containerColor = Color(0xFFEF8E52),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-24).dp)
                        .size(80.dp),
                    shape = RoundedCornerShape(32.dp),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Top Nav
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(primaryColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = primaryColor)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Good late night", color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text(userName, color = onBgColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuD7auInrMqEu8-euE8_znCfyyqzmldE0a2Vywhqt3tzIkfLyC8K5NRXSijyhLi44Zl5tb8Az3zEvn05FhzLpSopIhtpE8ZkTY9ANyTzrv_q92Vi1-fKfFw68LO0TamaKRNq3u-52WCMqdcnpb52WQx93w5YaTvm9_nc7UHAZixdu3fxfF386i5oOmtGXOU5DFsUmWDtUYZ_hKR4-P0TgUrUbXD0060xq66pJ3xOm6KdtUrIfsYFnsW40usrCD0RU5A66CXQ5lKDYTg",
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(2.dp, primaryColor.copy(alpha = 0.2f), CircleShape)
                        )
                    }
                }
            }

            item {
                // Total Balance Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(cardColor)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
                        .padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 40.dp, y = (-40).dp)
                            .size(160.dp)
                            .background(primaryColor.copy(alpha = 0.1f), CircleShape)
                            .blur(40.dp)
                    )
                    
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total balance", color = primaryColor.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Outlined.VisibilityOff, contentDescription = null, tint = textMuted)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(formatter.format(totalBalance), color = onBgColor, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("INCOME", color = textMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text(formatter.format(income), color = incomeColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("EXPENSE", color = textMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text(formatter.format(expense), color = primaryColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                // Overview
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Overview", color = onBgColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Outlined.GridView, contentDescription = null, tint = textMuted)
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Grid representation
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewItem(Icons.Outlined.PieChart, "Budgets", "0 Budgets", primaryColor, cardColor, Modifier.weight(1f))
                        OverviewItem(Icons.Filled.AccountBalance, "Assets", "₹0.00", primaryColor, cardColor, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewItem(Icons.Outlined.Group, "Bill Splitter", "0 Bills Active", primaryColor, cardColor, Modifier.weight(1f))
                        OverviewItem(Icons.Outlined.CurrencyExchange, "Loans", "₹1.95K Balance", primaryColor, cardColor, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewItem(Icons.Outlined.Analytics, "Analytics", "₹0.00 this month", primaryColor, cardColor, Modifier.weight(1f))
                        OverviewItem(Icons.Outlined.EventRepeat, "Recurring", "0 Active", primaryColor, cardColor, Modifier.weight(1f))
                    }
                }
            }

            item {
                // Recent Activity Header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Recent Activity", color = onBgColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TextButton(
                        onClick = { },
                        colors = ButtonDefaults.textButtonColors(containerColor = primaryColor.copy(alpha = 0.1f), contentColor = primaryColor),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("See All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(recentTransactions.take(3)) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardColor)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(if (tx.type == "Income") Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward, contentDescription = null, tint = Color(0xFFCBD5E1))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(tx.category, color = onBgColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(tx.note.ifBlank { "Transaction" }, color = textMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = (if (tx.type == "Income") "+" else "-") + formatter.format(tx.amount),
                            color = if (tx.type == "Income") incomeColor else primaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, color: Color, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OverviewItem(icon: ImageVector, title: String, subtitle: String, primaryColor: Color, cardColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable { }
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(primaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = primaryColor)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
