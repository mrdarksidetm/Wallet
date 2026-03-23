package com.mrdarksidetm.wallet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getTransactionsForAccount(accountId: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE accountId = :accountId AND type = 'Income'")
    fun getTotalIncome(accountId: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE accountId = :accountId AND type = 'Expense'")
    fun getTotalExpense(accountId: String): Flow<Double?>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    @Query("UPDATE transactions SET isArchived = 1 WHERE id = :transactionId")
    suspend fun archiveTransaction(transactionId: String)

    @Query("SELECT * FROM transactions WHERE isArchived = 0 ORDER BY date DESC")
    fun getActiveTransactions(): Flow<List<TransactionEntity>>
}
