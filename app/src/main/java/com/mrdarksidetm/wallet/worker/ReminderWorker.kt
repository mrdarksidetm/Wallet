package com.mrdarksidetm.wallet.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mrdarksidetm.wallet.utils.NotificationHelper

/**
 * Phase 24: Local Scheduled Notifications
 * 
 * Background worker that triggers notifications for financial events.
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Wallet Reminder"
        val message = inputData.getString("message") ?: "Don't forget to log your transactions!"

        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification(title, message)

        return Result.success()
    }
}
