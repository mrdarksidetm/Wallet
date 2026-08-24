package com.darkside.wallet.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton

data class CurrencyItem(
    val code: String,
    val name: String,
    val symbol: String
)

val SupportedCurrencies = listOf(
    CurrencyItem("INR", "Indian Rupee", "₹"),
    CurrencyItem("USD", "US Dollar", "$"),
    CurrencyItem("EUR", "Euro", "€"),
    CurrencyItem("GBP", "British Pound", "£"),
    CurrencyItem("JPY", "Japanese Yen", "¥"),
    CurrencyItem("CAD", "Canadian Dollar", "$"),
    CurrencyItem("AUD", "Australian Dollar", "$"),
    CurrencyItem("CNY", "Chinese Yuan", "¥"),
    CurrencyItem("CHF", "Swiss Franc", "CHF"),
    CurrencyItem("SGD", "Singapore Dollar", "$"),
    CurrencyItem("AED", "UAE Dirham", "د.إ"),
    CurrencyItem("SAR", "Saudi Riyal", "﷼"),
    CurrencyItem("BRL", "Brazilian Real", "R$"),
    CurrencyItem("RUB", "Russian Ruble", "₽"),
    CurrencyItem("KRW", "South Korean Won", "₩"),
    CurrencyItem("ZAR", "South African Rand", "R")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val currentCurrency by viewModel.currencyCode.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    val filteredCurrencies = remember(searchQuery) {
        if (searchQuery.isBlank()) SupportedCurrencies
        else SupportedCurrencies.filter {
            it.code.contains(searchQuery, ignoreCase = true) ||
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.symbol.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Currency",
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
        containerColor = colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search currency...") },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = colorScheme.surfaceContainerLow,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filteredCurrencies, key = { it.code }) { currency ->
                    val isSelected = currentCurrency.equals(currency.code, ignoreCase = true)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.updateCurrency(currency.code)
                                onBack()
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else colorScheme.surfaceContainerLow
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            color = if (isSelected) colorScheme.primary else colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colorScheme.surfaceContainerHighest),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currency.symbol,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = colorScheme.primary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currency.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = currency.code,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
