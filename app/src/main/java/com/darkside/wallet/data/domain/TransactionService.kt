package com.darkside.wallet.data.domain

import androidx.room.withTransaction
import com.darkside.wallet.data.*
import java.util.*

/**
 * Service to handle high-level Transaction business logic.
 * Ported from Flutter TransactionService v1.4.1.
 */
class TransactionService(
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val goalDao: GoalDao
) {

    /**
     * Adds a transaction and updates the associated account balance atomically.
     */
    suspend fun addTransaction(
        amount: Double,
        date: Long,
        type: String, // income, expense, transfer
        accountId: String,
        categoryId: String,
        personId: String? = null,
        note: String = "",
        icon: String? = null,
        color: Int? = null,
        transferAccountId: String? = null,
        tags: List<String> = emptyList(),
        isTemplate: Boolean = false
    ) {
        if (amount <= 0) throw Exception("Amount must be greater than 0")
        if (type == "transfer" && transferAccountId == null) {
            throw Exception("Transfer account is required for transfers")
        }
        if (type == "transfer" && accountId == transferAccountId) {
            throw Exception("Cannot transfer to the same account")
        }

        database.withTransaction {
            // 1. Fetch Account
            val account = accountDao.getAccountById(accountId) 
                ?: throw Exception("Account not found")

            // 2. Create Transaction Entity
            val transaction = TransactionEntity(
                amount = amount,
                date = date,
                type = type,
                note = note,
                categoryId = categoryId,
                accountId = accountId,
                personId = personId,
                transferAccountId = transferAccountId,
                icon = icon,
                color = color,
                tags = tags,
                isTemplate = isTemplate
            )

            // 3. Update Balance
            when (type) {
                "income" -> accountDao.updateAccount(account.copy(balance = account.balance + amount))
                "expense" -> accountDao.updateAccount(account.copy(balance = account.balance - amount))
                "transfer" -> {
                    val transferAcc = accountDao.getAccountById(transferAccountId!!)
                        ?: throw Exception("Transfer account not found")
                    
                    accountDao.updateAccount(account.copy(balance = account.balance - amount))
                    accountDao.updateAccount(transferAcc.copy(balance = transferAcc.balance + amount))
                }
            }

            // 4. Sync with Goals if Savings account
            if (account.type.lowercase() == "savings") {
                val goals = goalDao.getGoalsForAccount(accountId)
                goals.forEach { goal ->
                    val newAmount = when (type) {
                        "income" -> goal.currentAmount + amount
                        "expense" -> goal.currentAmount - amount
                        else -> goal.currentAmount
                    }
                    goalDao.updateGoal(goal.copy(
                        currentAmount = newAmount,
                        isCompleted = newAmount >= goal.targetAmount
                    ))
                }
            }

            // 5. Insert Transaction
            transactionDao.insertTransaction(transaction)
        }
    }

    /**
     * Deletes a transaction and reverts the account balance/goal changes.
     */
    suspend fun deleteTransaction(transactionId: String) {
        database.withTransaction {
            val transaction = transactionDao.getTransactionById(transactionId) ?: return@withTransaction
            val account = accountDao.getAccountById(transaction.accountId) ?: return@withTransaction

            // 1. Revert Balance
            when (transaction.type) {
                "income" -> accountDao.updateAccount(account.copy(balance = account.balance - transaction.amount))
                "expense" -> accountDao.updateAccount(account.copy(balance = account.balance + transaction.amount))
                "transfer" -> {
                    val transferAcc = transaction.transferAccountId?.let { accountDao.getAccountById(it) }
                    if (transferAcc != null) {
                        accountDao.updateAccount(account.copy(balance = account.balance + transaction.amount))
                        accountDao.updateAccount(transferAcc.copy(balance = transferAcc.balance - transaction.amount))
                    }
                }
            }

            // 2. Revert Goal Sync
            if (account.type.lowercase() == "savings") {
                val goals = goalDao.getGoalsForAccount(transaction.accountId)
                goals.forEach { goal ->
                    val newAmount = when (transaction.type) {
                        "income" -> goal.currentAmount - transaction.amount
                        "expense" -> goal.currentAmount + transaction.amount
                        else -> goal.currentAmount
                    }
                    goalDao.updateGoal(goal.copy(
                        currentAmount = newAmount,
                        isCompleted = newAmount >= goal.targetAmount
                    ))
                }
            }

            // 3. Delete from DB
            transactionDao.deleteTransaction(transaction)
        }
    }
}
