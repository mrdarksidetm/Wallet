package com.darkside.wallet.utils

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Phase 30: Secure Data Shredding
 * 
 * Exposes a "Factory Reset" logic sequence to wipe all user data cleanly.
 * This is critical for offline-first privacy. We clear Room tables, vacuum the DB,
 * and clear shared preferences/cache directories to prevent ghost data extraction.
 */
object DataShredder {
    suspend fun factoryReset(context: Context, database: SupportSQLiteDatabase): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Drop all data from the tables
                database.execSQL("DELETE FROM domain_transactions")
                database.execSQL("DELETE FROM accounts")
                database.execSQL("DELETE FROM categories")
                database.execSQL("DELETE FROM budgets")
                database.execSQL("DELETE FROM goals")
                database.execSQL("DELETE FROM loans")
                
                // 2. Vacuum to reclaim space and erase ghost byte data
                database.execSQL("VACUUM;")
                
                // 3. Clear preferences (DataStore/SharedPreferences)
                val sharedPrefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
                if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
                    sharedPrefsDir.listFiles()?.forEach { it.delete() }
                }

                // 4. Clear cache directory (crash logs, temp images)
                context.cacheDir.deleteRecursively()
                
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
