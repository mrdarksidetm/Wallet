package com.darkytm.wallet.data.model

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER,
    DEBT_LEND,
    DEBT_BORROW,
    DEBT_REPAY,
    GOAL_CONTRIBUTION,
    GOAL_WITHDRAWAL
}

enum class AccountType {
    CASH,
    BANK,
    CREDIT_CARD,
    SAVINGS,
    INVESTMENT,
    OTHER
}

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    YEARLY
}
