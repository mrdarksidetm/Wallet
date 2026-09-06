package com.darkytm.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkytm.wallet.ui.WalletViewModel
import com.darkytm.wallet.ui.components.ActivityInsightsSection
import com.darkytm.wallet.ui.components.AnimatedBalanceHero
import com.darkytm.wallet.ui.components.HomeHeader
import com.darkytm.wallet.ui.components.OverviewCategoryType
import com.darkytm.wallet.ui.components.OverviewDetailHost
import com.darkytm.wallet.ui.components.OverviewGridSection
import com.darkytm.wallet.ui.components.RecentTransactionsSection
import com.darkytm.wallet.ui.theme.PaletteStyle
import com.darkytm.wallet.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WalletViewModel,
    onAddClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showThemeDialog by remember { mutableStateOf(false) }
    var activeOverviewCategory by remember { mutableStateOf<OverviewCategoryType?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeCustomizerDialog(
            currentThemeMode = state.themeMode,
            currentPalette = state.paletteStyle,
            isDynamic = state.isDynamicColor,
            onThemeModeSelected = { viewModel.setThemeMode(it) },
            onPaletteSelected = { viewModel.setPaletteStyle(it) },
            onDynamicToggled = { viewModel.setDynamicColor(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text(
                    text = "Profile & Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("User: DarkyTM", fontWeight = FontWeight.SemiBold)
                    Text("Architecture: Native Jetpack Compose + Room", style = MaterialTheme.typography.bodyMedium)
                    Text("Design System: Material 3 Expressive", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add entry") },
                text = { Text("Add Entry", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. Dynamic Expressive Header
            item {
                HomeHeader(
                    userName = "User",
                    onThemeClick = { showThemeDialog = true },
                    onProfileClick = { showProfileDialog = true }
                )
            }

            // 2. Animated Balance Hero Card with Canvas organic blobs
            item {
                AnimatedBalanceHero(
                    totalBalance = state.totalBalance,
                    monthlyIncome = state.monthlyIncome,
                    monthlyExpense = state.monthlyExpense,
                    isBalanceVisible = state.isBalanceVisible,
                    onToggleVisibility = { viewModel.toggleBalanceVisibility() }
                )
            }

            // 3. Overview Hub 2-Column Grid (8 Financial Hubs)
            item {
                OverviewGridSection(
                    accounts = state.accounts,
                    budgets = state.budgets,
                    goals = state.goals,
                    people = state.people,
                    recurringRules = state.recurringRules,
                    categories = state.categories,
                    totalBalance = state.totalBalance,
                    isBalanceVisible = state.isBalanceVisible,
                    onCategoryClick = { activeOverviewCategory = it }
                )
            }

            // 4. Activity Insights (Calendar Heatmap + 30-day Trends Sparkline)
            item {
                ActivityInsightsSection(
                    allTransactions = state.allTransactions,
                    isBalanceVisible = state.isBalanceVisible
                )
            }

            // 5. Recent Transactions with Segmented Filter
            item {
                RecentTransactionsSection(
                    transactions = state.allTransactions,
                    isBalanceVisible = state.isBalanceVisible,
                    onDeleteTransaction = { viewModel.deleteTransaction(it) },
                    onAddClick = onAddClick
                )
            }
        }
    }

    // Interactive Detail Bottom Sheet for Overview Categories
    OverviewDetailHost(
        activeCategory = activeOverviewCategory,
        accounts = state.accounts,
        budgets = state.budgets,
        goals = state.goals,
        people = state.people,
        recurringRules = state.recurringRules,
        categories = state.categories,
        isBalanceVisible = state.isBalanceVisible,
        onDismiss = { activeOverviewCategory = null }
    )
}

@Composable
fun ThemeCustomizerDialog(
    currentThemeMode: ThemeMode,
    currentPalette: PaletteStyle,
    isDynamic: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onPaletteSelected: (PaletteStyle) -> Unit,
    onDynamicToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Appearance & Theme Styles", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Theme Mode", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                ThemeMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = currentThemeMode == mode,
                            onClick = { onThemeModeSelected(mode) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (mode) {
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                                ThemeMode.AMOLED -> "AMOLED (Pure Pitch Black)"
                                ThemeMode.SYSTEM -> "Follow System"
                            }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text("Material 3 Dynamic Palette Styles (10)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                PaletteStyle.entries.forEach { style ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = currentPalette == style,
                            onClick = { onPaletteSelected(style) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(text = style.displayName, fontWeight = FontWeight.Medium)
                            Text(
                                text = style.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Android Wallpaper Colors (12+)", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isDynamic, onCheckedChange = onDynamicToggled)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
