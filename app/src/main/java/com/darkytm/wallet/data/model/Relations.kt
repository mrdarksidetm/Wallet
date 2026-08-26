package com.darkytm.wallet.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithDetails(
    @Embedded val transaction: Transaction,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: Account?,

    @Relation(
        parentColumn = "toAccountId",
        entityColumn = "id"
    )
    val toAccount: Account?,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category?,

    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person: Person?,

    @Relation(
        parentColumn = "goalId",
        entityColumn = "id"
    )
    val goal: Goal?
)

data class AccountWithBalance(
    val account: Account,
    val currentBalance: Double
)

data class GoalWithProgress(
    val goal: Goal,
    val currentSaved: Double,
    val progressPercent: Float
)

data class PersonWithDebt(
    val person: Person,
    val totalLent: Double,     // Money you gave to this person
    val totalBorrowed: Double, // Money you borrowed from this person
    val netBalance: Double     // Positive means they owe you; negative means you owe them
)

data class BudgetWithProgress(
    val budget: Budget,
    val category: Category?,
    val spentAmount: Double,
    val remainingAmount: Double,
    val progressPercent: Float,
    val isExceeded: Boolean
)
