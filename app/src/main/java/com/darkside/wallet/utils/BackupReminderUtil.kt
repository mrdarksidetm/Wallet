package com.darkside.wallet.utils

import java.util.concurrent.TimeUnit

/**
 * Phase 50: In-App Backup Reminders & TTL
 * 
 * Evaluates the time elapsed since the last backup was taken.
 * If more than 14 days have passed, this flags the UI to display a reminder banner.
 */
object BackupReminderUtil {
    private const val BACKUP_TTL_DAYS = 14L

    fun shouldShowBackupReminder(lastBackupTimestamp: Long): Boolean {
        if (lastBackupTimestamp == 0L) return true // Never backed up
        
        val currentTime = System.currentTimeMillis()
        val diffInMillis = currentTime - lastBackupTimestamp
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        
        return diffInDays >= BACKUP_TTL_DAYS
    }
}
