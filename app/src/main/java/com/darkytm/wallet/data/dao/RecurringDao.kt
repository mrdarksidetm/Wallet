package com.darkytm.wallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.darkytm.wallet.data.model.RecurringRule
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringDao {

    @Query("SELECT * FROM recurring_rules WHERE isActive = 1 ORDER BY nextDueDateMillis ASC")
    fun getAllActiveRules(): Flow<List<RecurringRule>>

    @Query("SELECT * FROM recurring_rules WHERE isActive = 1 AND nextDueDateMillis <= :currentMillis")
    suspend fun getDueRules(currentMillis: Long): List<RecurringRule>

    @Query("SELECT * FROM recurring_rules WHERE id = :id")
    suspend fun getRuleById(id: Long): RecurringRule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RecurringRule): Long

    @Update
    suspend fun update(rule: RecurringRule)

    @Delete
    suspend fun delete(rule: RecurringRule)
}
