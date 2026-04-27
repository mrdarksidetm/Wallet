package com.darkside.wallet.data.domain

import androidx.compose.runtime.Immutable
import com.darkside.wallet.data.entity.TransactionType

/**
 * Data class representing a Transaction.
 * 
 * Compose Optimization:
 * @Immutable explicitly tells the Jetpack Compose compiler that all public properties
 * of this class will never change after construction. This enables "Strong Skipping",
 * preventing massive lists of Transactions from unnecessarily recomposing when unrelated 
 * UI state changes, significantly boosting 120Hz scroll performance.
 * 
 * Room Annotations:
 * @Entity: Marks this class as a table in the Room database. We explicitly define the table name.
 * @PrimaryKey: Marks 'id' as the unique identifier for each row.
 * 
 * Relational IDs (categoryId, accountId):
 * By storing string IDs rather than embedding complex objects, the schema remains normalized.
 * This reduces data duplication, keeps the model lightweight, and allows for highly scalable
 * joins at the repository or UI layer.
 */
@Immutable
data class Transaction(
    val id: String,
    val amount: Double,
    val note: String?,
    val date: Long, // Stored as Unix timestamp for fast querying and indexing
    val type: TransactionType,
    val categoryId: String,
    val accountId: String,
    
    // Phase 35: Privacy Geofencing (Foreground only location tagging)
    val latitude: Double? = null,
    val longitude: Double? = null
)

