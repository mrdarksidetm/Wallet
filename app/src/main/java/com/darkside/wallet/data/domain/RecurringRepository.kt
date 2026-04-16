package com.darkside.wallet.data.domain

import com.darkside.wallet.data.RecurringDao
import com.darkside.wallet.data.entity.RecurringEntity
import kotlinx.coroutines.flow.Flow

class RecurringRepository(private val dao: RecurringDao) {
    fun getAllRecurring(): Flow<List<RecurringEntity>> = dao.getAllRecurring()
    fun getActiveRecurring(): Flow<List<RecurringEntity>> = dao.getActiveRecurring()
    suspend fun insertRecurring(recurring: RecurringEntity): Long = dao.insertRecurring(recurring)
    suspend fun updateRecurring(recurring: RecurringEntity) = dao.updateRecurring(recurring)
    suspend fun deleteRecurring(recurring: RecurringEntity) = dao.deleteRecurring(recurring)
}
