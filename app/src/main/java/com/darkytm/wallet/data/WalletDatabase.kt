package com.darkytm.wallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darkytm.wallet.data.dao.AccountDao
import com.darkytm.wallet.data.dao.BudgetDao
import com.darkytm.wallet.data.dao.CategoryDao
import com.darkytm.wallet.data.dao.GoalDao
import com.darkytm.wallet.data.dao.PersonDao
import com.darkytm.wallet.data.dao.RecurringDao
import com.darkytm.wallet.data.dao.TransactionDao
import com.darkytm.wallet.data.model.Account
import com.darkytm.wallet.data.model.AccountType
import com.darkytm.wallet.data.model.Budget
import com.darkytm.wallet.data.model.BudgetPeriod
import com.darkytm.wallet.data.model.Category
import com.darkytm.wallet.data.model.Goal
import com.darkytm.wallet.data.model.Person
import com.darkytm.wallet.data.model.RecurringFrequency
import com.darkytm.wallet.data.model.RecurringRule
import com.darkytm.wallet.data.model.Transaction
import com.darkytm.wallet.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromAccountType(type: AccountType): String = type.name

    @TypeConverter
    fun toAccountType(value: String): AccountType = AccountType.valueOf(value)

    @TypeConverter
    fun fromBudgetPeriod(period: BudgetPeriod): String = period.name

    @TypeConverter
    fun toBudgetPeriod(value: String): BudgetPeriod = BudgetPeriod.valueOf(value)

    @TypeConverter
    fun fromRecurringFrequency(frequency: RecurringFrequency): String = frequency.name

    @TypeConverter
    fun toRecurringFrequency(value: String): RecurringFrequency = RecurringFrequency.valueOf(value)
}

@Database(
    entities = [
        Transaction::class,
        Account::class,
        Category::class,
        Person::class,
        Goal::class,
        Budget::class,
        RecurringRule::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WalletDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun personDao(): PersonDao
    abstract fun goalDao(): GoalDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringDao(): RecurringDao

    companion object {
        @Volatile private var INSTANCE: WalletDatabase? = null

        fun getInstance(context: Context): WalletDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WalletDatabase::class.java,
                    "wallet.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val instance = getInstance(context)
                            seedDefaults(instance)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }

        private suspend fun seedDefaults(database: WalletDatabase) {
            val accountDao = database.accountDao()
            val categoryDao = database.categoryDao()

            val defaultAccounts = listOf(
                Account(name = "Cash", iconEmoji = "💵", colorHex = 0xFF8D4F00L, initialBalance = 0.0, type = AccountType.CASH),
                Account(name = "Main Bank", iconEmoji = "🏦", colorHex = 0xFF1565C0L, initialBalance = 0.0, type = AccountType.BANK),
                Account(name = "Savings Vault", iconEmoji = "💰", colorHex = 0xFF2E7D32L, initialBalance = 0.0, type = AccountType.SAVINGS)
            )
            accountDao.insertAll(defaultAccounts)

            val defaultCategories = listOf(
                Category(name = "Food & Dining", iconEmoji = "🍔", colorHex = 0xFFE65100L, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Transport & Fuel", iconEmoji = "🚗", colorHex = 0xFF0277BDL, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Bills & Utilities", iconEmoji = "💡", colorHex = 0xFFF57F17L, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Shopping", iconEmoji = "🛍️", colorHex = 0xFF8E24AAL, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Entertainment", iconEmoji = "🎬", colorHex = 0xFFD81B60L, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Health & Care", iconEmoji = "🏥", colorHex = 0xFF00897BL, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Travel", iconEmoji = "✈️", colorHex = 0xFF3949ABL, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Other Expense", iconEmoji = "📦", colorHex = 0xFF757575L, type = TransactionType.EXPENSE, isDefault = true),
                Category(name = "Salary", iconEmoji = "💼", colorHex = 0xFF2E7D32L, type = TransactionType.INCOME, isDefault = true),
                Category(name = "Freelance / Gig", iconEmoji = "💻", colorHex = 0xFF00ACC1L, type = TransactionType.INCOME, isDefault = true),
                Category(name = "Investments", iconEmoji = "📈", colorHex = 0xFF43A047L, type = TransactionType.INCOME, isDefault = true),
                Category(name = "Gift / Other Income", iconEmoji = "🎁", colorHex = 0xFFFFB300L, type = TransactionType.INCOME, isDefault = true)
            )
            categoryDao.insertAll(defaultCategories)
        }
    }
}
