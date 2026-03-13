package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountsScreen(viewModel: WalletViewModel, snackbarHostState: SnackbarHostState) {
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(recentTransactions, key = { it.id }) { transaction ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { swipeValue ->
                        // Only allow swipe to finish if it's StartToEnd
                        swipeValue == SwipeToDismissBoxValue.StartToEnd
                    }
                )

                LaunchedEffect(dismissState.currentValue) {
                    if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                        viewModel.deleteTransaction(transaction)
                        scope.launch {
                            snackbarHostState.showSnackbar("Transaction Deleted")
                        }
                    }
                }

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromEndToStart = false,
                    modifier = Modifier.animateItemPlacement(),
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red, shape = MaterialTheme.shapes.medium)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                        }
                    }
                ) {
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}
