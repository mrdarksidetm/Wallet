package com.darkside.wallet.data.domain

import androidx.compose.runtime.Immutable
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

/**
 * Enum class representing the distinct types of transactions.
 * Using an Enum provides compile-time safety and avoids invalid string states
 * compared to using raw strings for transaction types.
 */
enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

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
@Entity(tableName = "domain_transactions")
data class Transaction(
    @PrimaryKey
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

/**
 * Data Access Object for Transactions.
 */
@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM domain_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Monthly Filtering Logic:
     * Room queries can filter timestamps dynamically. We pass the start timestamp 
     * (e.g., 00:00 on the 1st of the month) and the end timestamp (e.g., 23:59 on the last day).
     * This allows the SQLite engine to efficiently filter rows before returning data to Kotlin,
     * which is significantly faster and more memory-efficient than filtering in-memory.
     */
    @Query("SELECT * FROM domain_transactions WHERE date >= :startOfMonth AND date <= :endOfMonth ORDER BY date DESC")
    fun getTransactionsByMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<Transaction>>
}
