package com.darkside.wallet.data

import androidx.room.*
import com.darkside.wallet.data.entity.RecurringEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurring(recurring: RecurringEntity): Long

    @Update
    suspend fun updateRecurring(recurring: RecurringEntity)

    @Delete
    suspend fun deleteRecurring(recurring: RecurringEntity)

    @Query("SELECT * FROM recurring WHERE isDeleted = 0")
    fun getAllRecurring(): Flow<List<RecurringEntity>>

    @Query("SELECT * FROM recurring WHERE isActive = 1 AND isDeleted = 0")
    fun getActiveRecurring(): Flow<List<RecurringEntity>>
}
