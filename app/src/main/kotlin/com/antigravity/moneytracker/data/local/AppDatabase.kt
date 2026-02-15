package com.antigravity.moneytracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.antigravity.moneytracker.data.local.dao.TransactionDao
import com.antigravity.moneytracker.data.local.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
