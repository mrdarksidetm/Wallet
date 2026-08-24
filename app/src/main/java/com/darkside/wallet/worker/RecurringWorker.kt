package com.darkside.wallet.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darkside.wallet.data.AppDatabase
import com.darkside.wallet.data.domain.RecurringService
import com.darkside.wallet.data.domain.TransactionService

/**
 * Background worker to process recurring transactions.
 */
class RecurringWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val transactionService = TransactionService(
                database,
                database.transactionDao(),
                database.accountDao()
            )
            val recurringService = RecurringService(
                database.recurringDao(),
                transactionService
            )

            recurringService.checkRecurringTransactions()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
