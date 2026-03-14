package com.mrdarksidetm.wallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AccountEntity::class, TransactionEntity::class, CategoryEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wallet_database"
                )
                // Phase 22: Removed fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with a default account and categories
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                database.accountDao().insertAccount(
                                    AccountEntity(id = 1, name = "Cash", type = "Cash", initialBalance = 0.0)
                                )
                                database.categoryDao().insertCategories(
                                    listOf(
                                        CategoryEntity(name = "Food", icon = "restaurant"),
                                        CategoryEntity(name = "Salary", icon = "payments"),
                                        CategoryEntity(name = "Transport", icon = "directions_car"),
                                        CategoryEntity(name = "Entertainment", icon = "movie"),
                                        CategoryEntity(name = "Health", icon = "medical_services"),
                                        CategoryEntity(name = "Other", icon = "category")
                                    )
                                )
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
