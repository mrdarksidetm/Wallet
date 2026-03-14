package com.mrdarksidetm.wallet.data

import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Phase 45: Advanced Database Defragmentation (Vacuuming)
 *
 * CRITICAL: Memory Reclamation Strategy
 * SQLite uses a Write-Ahead Log (WAL) and leaves empty byte-spaces when transactions 
 * are deleted. Over years of logging expenses, the database file bloats.
 * By executing `PRAGMA wal_checkpoint(TRUNCATE)` and `VACUUM`, we physically rebuild 
 * the database file, packing the data tightly and reclaiming disk space and RAM overhead.
 * This ensures the app remains performant on 4GB RAM phones permanently.
 */
object DatabaseOptimizer {
    
    fun optimizeDatabase(database: SupportSQLiteDatabase) {
        try {
            // Force the WAL log to flush into the main DB and truncate the log file
            database.query("PRAGMA wal_checkpoint(TRUNCATE);").close()
            
            // Rebuild the entire database file to reclaim empty deleted space
            database.execSQL("VACUUM;")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
