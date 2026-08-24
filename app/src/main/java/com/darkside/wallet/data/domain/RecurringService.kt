package com.darkside.wallet.data.domain

import com.darkside.wallet.data.RecurringDao
import com.darkside.wallet.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Engine to process recurring transactions.
 * Ported from Flutter implementation for functional parity.
 */
class RecurringService(
    private val recurringDao: RecurringDao,
    private val transactionService: TransactionService
) {

    /**
     * Checks for due recurring transactions and processes them.
     */
    suspend fun checkRecurringTransactions() {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val dueItems = recurringDao.getActiveRecurring().first().filter { it.nextDate <= now }

            dueItems.forEach { recurring ->
                processRecurring(recurring)
            }
        }
    }

    private suspend fun processRecurring(recurring: RecurringEntity) {
        try {
            // 1. Create the transaction
            transactionService.addTransaction(
                amount = recurring.amount,
                date = recurring.nextDate,
                type = recurring.type,
                accountId = recurring.accountId,
                categoryId = recurring.categoryId,
                note = "${recurring.name} (Recurring)",
                transferAccountId = recurring.transferAccountId
            )

            // 2. Calculate next date
            val nextDate = calculateNextDate(recurring.nextDate, recurring.frequency)

            // 3. Update the recurring entry
            // If there's an end date and we passed it, deactivate
            val isActive = if (recurring.endDate != null && nextDate > recurring.endDate) {
                false
            } else {
                recurring.isActive
            }

            recurringDao.updateRecurring(
                recurring.copy(
                    nextDate = nextDate,
                    isActive = isActive,
                    updatedAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateNextDate(currentDate: Long, frequency: RecurrenceFrequency): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentDate
        }
        when (frequency) {
            RecurrenceFrequency.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            RecurrenceFrequency.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            RecurrenceFrequency.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            RecurrenceFrequency.YEARLY -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }
}
