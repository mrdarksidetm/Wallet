package com.darkytm.wallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.darkytm.wallet.data.model.Transaction as WalletTx
import com.darkytm.wallet.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<WalletTx>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC LIMIT :limit")
    fun getRecentTransactionsWithDetails(limit: Int = 20): Flow<List<TransactionWithDetails>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): WalletTx?

    @Query("SELECT * FROM transactions WHERE accountId = :accountId OR toAccountId = :accountId ORDER BY dateMillis DESC")
    fun getTransactionsByAccount(accountId: Long): Flow<List<WalletTx>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY dateMillis DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<WalletTx>>

    @Query("SELECT * FROM transactions WHERE personId = :personId ORDER BY dateMillis DESC")
    fun getTransactionsByPerson(personId: Long): Flow<List<WalletTx>>

    @Query("SELECT * FROM transactions WHERE goalId = :goalId ORDER BY dateMillis DESC")
    fun getTransactionsByGoal(goalId: Long): Flow<List<WalletTx>>

    @Query("SELECT * FROM transactions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis")
    fun getTransactionsInDateRange(startMillis: Long, endMillis: Long): Flow<List<WalletTx>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: WalletTx): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<WalletTx>): List<Long>

    @Update
    suspend fun update(transaction: WalletTx)

    @Delete
    suspend fun delete(transaction: WalletTx)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
