package com.darkside.wallet

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.darkside.wallet.worker.RecurringWorker
import com.darkside.wallet.utils.LocalCrashReporter
import java.util.concurrent.TimeUnit

class WalletApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Phase 42: Local Crash Log Export (Zero-Network Crashlytics)
        LocalCrashReporter(this)
        
        scheduleRecurringTransactions()
    }

    private fun scheduleRecurringTransactions() {
        val recurringWorkRequest = PeriodicWorkRequestBuilder<RecurringWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RecurringTransactionWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            recurringWorkRequest
        )
    }
}
