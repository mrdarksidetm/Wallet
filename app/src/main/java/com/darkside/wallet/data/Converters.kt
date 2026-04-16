package com.darkside.wallet.data

import androidx.room.TypeConverter
import com.darkside.wallet.data.entity.*

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? = value?.joinToString(",")

    @TypeConverter
    fun toStringList(value: String?): List<String>? = value?.split(",")?.filter { it.isNotEmpty() }

    @TypeConverter
    fun fromAccountType(value: AccountType): String = value.name

    @TypeConverter
    fun toAccountType(value: String): AccountType = AccountType.valueOf(value)

    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromLoanType(value: LoanType): String = value.name

    @TypeConverter
    fun toLoanType(value: String): LoanType = LoanType.valueOf(value)

    @TypeConverter
    fun fromRecurrenceFrequency(value: RecurrenceFrequency): String = value.name

    @TypeConverter
    fun toRecurrenceFrequency(value: String): RecurrenceFrequency = RecurrenceFrequency.valueOf(value)

    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod): String = value.name

    @TypeConverter
    fun toBudgetPeriod(value: String): BudgetPeriod = BudgetPeriod.valueOf(value)
}

