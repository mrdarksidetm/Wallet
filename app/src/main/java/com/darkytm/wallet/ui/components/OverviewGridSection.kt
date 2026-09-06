package com.darkytm.wallet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkytm.wallet.data.model.AccountWithBalance
import com.darkytm.wallet.data.model.BudgetWithProgress
import com.darkytm.wallet.data.model.Category as CategoryModel
import com.darkytm.wallet.data.model.GoalWithProgress
import com.darkytm.wallet.data.model.PersonWithDebt
import com.darkytm.wallet.data.model.RecurringRule
import com.darkytm.wallet.util.CurrencyUtils

enum class OverviewCategoryType {
    ACCOUNTS,
    BUDGETS,
    GOALS,
    LOANS,
    RECURRING,
    CATEGORIES,
    BILL_SPLITTER,
    PEOPLE
}

@Composable
fun OverviewGridSection(
    accounts: List<AccountWithBalance>,
    budgets: List<BudgetWithProgress>,
    goals: List<GoalWithProgress>,
    people: List<PersonWithDebt>,
    recurringRules: List<RecurringRule>,
    categories: List<CategoryModel>,
    totalBalance: Double,
    isBalanceVisible: Boolean,
    onCategoryClick: (OverviewCategoryType) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = colorScheme.onSurface
            )

            Text(
                text = "8 Hubs",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Row 1: Accounts & Budgets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                icon = Icons.Filled.AccountBalance,
                title = "Accounts",
                subtitle = if (isBalanceVisible) "${CurrencyUtils.formatAmount(totalBalance)} total" else "•••• total",
                accentColor = colorScheme.primary,
                onClick = { onCategoryClick(OverviewCategoryType.ACCOUNTS) },
                modifier = Modifier.weight(1f)
            )

            OverviewCard(
                icon = Icons.Filled.PieChart,
                title = "Budgets",
                subtitle = if (budgets.isNotEmpty()) "${budgets.size} tracking" else "Track spending",
                accentColor = colorScheme.secondary,
                onClick = { onCategoryClick(OverviewCategoryType.BUDGETS) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Goals & Loans/Debts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                icon = Icons.Filled.Flag,
                title = "Goals",
                subtitle = if (goals.isNotEmpty()) "${goals.size} targets" else "Savings targets",
                accentColor = colorScheme.tertiary,
                onClick = { onCategoryClick(OverviewCategoryType.GOALS) },
                modifier = Modifier.weight(1f)
            )

            OverviewCard(
                icon = Icons.Filled.SwapHoriz,
                title = "Loans",
                subtitle = if (people.isNotEmpty()) "${people.size} contacts" else "Debts & lending",
                accentColor = Color(0xFFF59E0B),
                onClick = { onCategoryClick(OverviewCategoryType.LOANS) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: Recurring & Categories
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                icon = Icons.Filled.Repeat,
                title = "Recurring",
                subtitle = if (recurringRules.isNotEmpty()) "${recurringRules.size} active" else "Subscriptions",
                accentColor = Color(0xFF8B5CF6),
                onClick = { onCategoryClick(OverviewCategoryType.RECURRING) },
                modifier = Modifier.weight(1f)
            )

            OverviewCard(
                icon = Icons.Filled.Category,
                title = "Categories",
                subtitle = "${categories.size} groups",
                accentColor = Color(0xFFEC4899),
                onClick = { onCategoryClick(OverviewCategoryType.CATEGORIES) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 4: Bill Splitter & People
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                icon = Icons.Filled.CallSplit,
                title = "Bill Splitter",
                subtitle = "Shared expenses",
                accentColor = Color(0xFF06B6D4),
                onClick = { onCategoryClick(OverviewCategoryType.BILL_SPLITTER) },
                modifier = Modifier.weight(1f)
            )

            OverviewCard(
                icon = Icons.Filled.People,
                title = "People",
                subtitle = "${people.size} friends",
                accentColor = Color(0xFF10B981),
                onClick = { onCategoryClick(OverviewCategoryType.PEOPLE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OverviewCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (0.299f * colorScheme.background.red + 0.587f * colorScheme.background.green + 0.114f * colorScheme.background.blue) < 0.5f
    val cardBackground = if (isDark) colorScheme.surfaceContainer else colorScheme.surfaceContainerLow

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = if (isDark) 0.35f else 0.45f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expressive Shape Container for Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = if (isDark) 0.22f else 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Navigate",
                    tint = colorScheme.onSurface.copy(alpha = 0.35f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
