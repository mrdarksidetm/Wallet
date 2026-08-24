package com.darkside.wallet.data.domain

import androidx.room.withTransaction
import com.darkside.wallet.data.*
import com.darkside.wallet.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Service to handle high-level Transaction business logic.
 */
class TransactionService(
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao
) {

    /**
     * Adds a transaction and updates the associated account balance atomically.
     */
    suspend fun addTransaction(
        amount: Double,
        date: Long,
        type: TransactionType,
        accountId: Long,
        categoryId: Long,
        personId: Long = 0,
        note: String? = null,
        icon: String? = null,
        color: String? = null,
        transferAccountId: Long? = null,
        tags: String? = null,
        isTemplate: Boolean = false
    ) {
        if (amount <= 0) throw Exception("Amount must be greater than 0")
        if (type == TransactionType.TRANSFER && transferAccountId == null) {
            throw Exception("Transfer account is required for transfers")
        }
        if (type == TransactionType.TRANSFER && accountId == transferAccountId) {
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
                TransactionType.INCOME -> accountDao.updateAccount(account.copy(balance = account.balance + amount))
                TransactionType.EXPENSE -> accountDao.updateAccount(account.copy(balance = account.balance - amount))
                TransactionType.TRANSFER -> {
                    val transferAcc = accountDao.getAccountById(transferAccountId!!)
                        ?: throw Exception("Transfer account not found")
                    
                    accountDao.updateAccount(account.copy(balance = account.balance - amount))
                    accountDao.updateAccount(transferAcc.copy(balance = transferAcc.balance + amount))
                }
            }

            // 4. Insert Transaction
            transactionDao.insertTransaction(transaction)
        }
    }

    /**
     * Deletes a transaction and reverts the account balance changes.
     */
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        database.withTransaction {
            val account = accountDao.getAccountById(transaction.accountId) ?: return@withTransaction

            // 1. Revert Balance
            when (transaction.type) {
                TransactionType.INCOME -> accountDao.updateAccount(account.copy(balance = account.balance - transaction.amount))
                TransactionType.EXPENSE -> accountDao.updateAccount(account.copy(balance = account.balance + transaction.amount))
                TransactionType.TRANSFER -> {
                    val transferAcc = transaction.transferAccountId?.let { accountDao.getAccountById(it) }
                    if (transferAcc != null) {
                        accountDao.updateAccount(account.copy(balance = account.balance + transaction.amount))
                        accountDao.updateAccount(transferAcc.copy(balance = transferAcc.balance - transaction.amount))
                    }
                }
            }

            // 2. Delete from DB
            transactionDao.deleteTransaction(transaction)
        }
    }

    /**
     * CRITICAL: Clears all tables in the database.
     * Used for full data restoration.
     */
    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }
}
