package com.darkside.wallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darkside.wallet.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AccountEntity::class, TransactionEntity::class, CategoryEntity::class, PersonEntity::class, LoanEntity::class, BudgetEntity::class, GoalEntity::class, RecurringEntity::class], version = 9, exportSchema = false)
@androidx.room.TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun personDao(): PersonDao
    abstract fun loanDao(): LoanDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao
    abstract fun recurringDao(): RecurringDao

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
                .fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with a default account and categories
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                database.accountDao().insertAccount(
                                    AccountEntity(name = "Cash", type = AccountType.CASH, balance = 0.0)
                                )
                                database.categoryDao().insertCategories(
                                    listOf(
                                        CategoryEntity(name = "Food", icon = "restaurant", type = CategoryType.EXPENSE),
                                        CategoryEntity(name = "Salary", icon = "payments", type = CategoryType.INCOME),
                                        CategoryEntity(name = "Transport", icon = "directions_car", type = CategoryType.EXPENSE),
                                        CategoryEntity(name = "Entertainment", icon = "movie", type = CategoryType.EXPENSE),
                                        CategoryEntity(name = "Health", icon = "medical_services", type = CategoryType.EXPENSE),
                                        CategoryEntity(name = "Other", icon = "category", type = CategoryType.EXPENSE)
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

