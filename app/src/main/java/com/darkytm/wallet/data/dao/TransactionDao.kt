package com.darkytm.wallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction as RoomTx
import androidx.room.Update
import com.darkytm.wallet.data.model.Transaction
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @RoomTx
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>>

    @RoomTx
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC LIMIT :limit")
    fun getRecentTransactionsWithDetails(limit: Int = 20): Flow<List<TransactionWithDetails>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE accountId = :accountId OR toAccountId = :accountId ORDER BY dateMillis DESC")
    fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY dateMillis DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE personId = :personId ORDER BY dateMillis DESC")
    fun getTransactionsByPerson(personId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE goalId = :goalId ORDER BY dateMillis DESC")
    fun getTransactionsByGoal(goalId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis")
    fun getTransactionsInDateRange(startMillis: Long, endMillis: Long): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>): List<Long>

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
