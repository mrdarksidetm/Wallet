package com.darkside.wallet.data

import androidx.room.*
import com.darkside.wallet.data.entity.TransactionEntity
import com.darkside.wallet.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactionSync(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getTransactionsForAccount(accountId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE accountId = :accountId AND type = 'INCOME'")
    fun getTotalIncome(accountId: Long): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE accountId = :accountId AND type = 'EXPENSE'")
    fun getTotalExpense(accountId: Long): Flow<Double?>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("UPDATE transactions SET isArchived = 1 WHERE id = :transactionId")
    suspend fun archiveTransaction(transactionId: Long)

    @Query("SELECT * FROM transactions WHERE isArchived = 0 AND isDeleted = 0 ORDER BY date DESC")
    fun getActiveTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isArchived = 0 AND isDeleted = 0 ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isArchived = 0 AND isDeleted = 0 ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactionsSync(limit: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE type = :type AND isArchived = 0 AND isDeleted = 0")
    fun getTransactionsByTypeSync(type: TransactionType): List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transactions WHERE isDeleted = 0")
    fun getTotalAmountSync(): Double?
}
