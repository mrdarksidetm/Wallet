package com.mrdarksidetm.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * SearchScreen using Material 3 SearchBar.
 * 
 * Architecture & State Flow:
 * 1. The user types into the M3 SearchBar.
 * 2. The `query` state updates and triggers a recomposition.
 * 3. In a full MVVM setup, this `query` string would be pushed to the ViewModel 
 *    (e.g. `viewModel.updateSearchQuery(query)`), which filters the Room Flow.
 * 4. The filtered list is collected back into the UI and passed to the `LazyColumn`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    
    // Placeholder data. In production, collect this from ViewModel Flow
    val searchResults = remember(query) {
        listOf("Groceries", "Salary", "Rent", "Coffee", "Internet").filter {
            it.contains(query, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = query,
            onQueryChange = { query = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Search transactions...") },
            leadingIcon = {
                if (active) {
                    IconButton(onClick = { active = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            trailingIcon = {
                if (active && query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            }
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
