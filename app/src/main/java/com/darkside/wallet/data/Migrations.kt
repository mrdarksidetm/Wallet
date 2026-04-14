package com.darkside.wallet.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Phase 22: Advanced Database Migrations
 * 
 * CRITICAL: Zero Data Loss
 * In offline-first apps, we cannot simply use fallbackToDestructiveMigration().
 * If the user updates the app and the schema changes, dropping the table wipes 
 * their entire financial history because there is no cloud backup to restore from.
 * We must provide explicit SQL statements for every schema evolution.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Example: Adding geofencing columns to domain_transactions table safely
        db.execSQL("ALTER TABLE domain_transactions ADD COLUMN latitude REAL DEFAULT NULL")
        db.execSQL("ALTER TABLE domain_transactions ADD COLUMN longitude REAL DEFAULT NULL")
    }
}
