package com.darkside.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.ui.WalletViewModel

/**
 * SearchScreen using Material 3 SearchBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    
    // In production, collect this from ViewModel Flow
    // val searchResults by viewModel.getSearchResults(query).collectAsStateWithLifecycle(initialValue = emptyList())
    val searchResults = remember(query) {
        listOf("Groceries", "Salary", "Rent", "Coffee", "Internet").filter {
            it.contains(query, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { active = false },
                    expanded = active,
                    onExpandedChange = { active = it },
                    placeholder = { Text("Search transactions...") },
                    leadingIcon = {
                        IconButton(onClick = { 
                            if (active) active = false else onBack() 
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )
            },
            expanded = active,
            onExpandedChange = { active = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
