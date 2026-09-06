package com.darkytm.wallet.data.repository

import com.darkytm.wallet.data.WalletDatabase
import com.darkytm.wallet.data.model.Account
import com.darkytm.wallet.data.model.AccountWithBalance
import com.darkytm.wallet.data.model.Budget
import com.darkytm.wallet.data.model.BudgetPeriod
import com.darkytm.wallet.data.model.BudgetWithProgress
import com.darkytm.wallet.data.model.Category
import com.darkytm.wallet.data.model.Goal
import com.darkytm.wallet.data.model.GoalWithProgress
import com.darkytm.wallet.data.model.Person
import com.darkytm.wallet.data.model.PersonWithDebt
import com.darkytm.wallet.data.model.RecurringRule
import com.darkytm.wallet.data.model.Transaction
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class WalletRepository(private val database: WalletDatabase) {

    private val transactionDao = database.transactionDao()
    private val accountDao = database.accountDao()
    private val categoryDao = database.categoryDao()
    private val personDao = database.personDao()
    private val goalDao = database.goalDao()
    private val budgetDao = database.budgetDao()
    private val recurringDao = database.recurringDao()

    // 1. Core Transactions
    fun getRecentTransactionsWithDetails(limit: Int = 20): Flow<List<TransactionWithDetails>> =
        transactionDao.getRecentTransactionsWithDetails(limit)

    fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>> =
        transactionDao.getAllTransactionsWithDetails()

    suspend fun addTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)

    // 2. Calculated Accounts & Balances (Derived from Transaction single source of truth)
    fun observeAccountsWithBalances(): Flow<List<AccountWithBalance>> {
        return combine(
            accountDao.getAllAccounts(),
            transactionDao.getAllTransactions()
        ) { accounts, transactions ->
            accounts.map { account ->
                var balance = account.initialBalance
                transactions.forEach { tx ->
                    when (tx.type) {
                        TransactionType.INCOME -> {
                            if (tx.accountId == account.id) balance += tx.amount
                        }
                        TransactionType.EXPENSE -> {
                            if (tx.accountId == account.id) balance -= tx.amount
                        }
                        TransactionType.TRANSFER -> {
                            if (tx.accountId == account.id) balance -= tx.amount
                            if (tx.toAccountId == account.id) balance += tx.amount
                        }
                        TransactionType.DEBT_LEND -> {
                            // Money left your account to lend to someone
                            if (tx.accountId == account.id) balance -= tx.amount
                        }
                        TransactionType.DEBT_BORROW -> {
                            // Money came into your account as a loan from someone
                            if (tx.accountId == account.id) balance += tx.amount
                        }
                        TransactionType.DEBT_REPAY -> {
                            // If you received money back, accountId increases.
                            if (tx.accountId == account.id) balance += tx.amount
                        }
                        TransactionType.GOAL_CONTRIBUTION -> {
                            // Moving money from account to savings goal
                            if (tx.accountId == account.id) balance -= tx.amount
                        }
                        TransactionType.GOAL_WITHDRAWAL -> {
                            // Withdrawing money from savings goal back to account
                            if (tx.accountId == account.id) balance += tx.amount
                        }
                    }
                }
                AccountWithBalance(account = account, currentBalance = balance)
            }
        }
    }

    // 3. Calculated Total Balance / Net Worth
    fun observeTotalBalance(): Flow<Double> {
        return observeAccountsWithBalances().combine(observeGoalsWithProgress()) { accounts, goals ->
            val accountTotal = accounts.sumOf { it.currentBalance }
            val goalTotal = goals.sumOf { it.currentSaved }
            accountTotal + goalTotal
        }
    }

    // 4. Calculated Goals & Progress
    fun observeGoalsWithProgress(): Flow<List<GoalWithProgress>> {
        return combine(
            goalDao.getAllGoals(),
            transactionDao.getAllTransactions()
        ) { goals, transactions ->
            goals.map { goal ->
                val contributions = transactions
                    .filter { it.goalId == goal.id && it.type == TransactionType.GOAL_CONTRIBUTION }
                    .sumOf { it.amount }
                val withdrawals = transactions
                    .filter { it.goalId == goal.id && it.type == TransactionType.GOAL_WITHDRAWAL }
                    .sumOf { it.amount }
                val saved = (contributions - withdrawals).coerceAtLeast(0.0)
                val progress = if (goal.targetAmount > 0) {
                    (saved / goal.targetAmount).toFloat().coerceIn(0f, 1f)
                } else 0f

                GoalWithProgress(
                    goal = goal,
                    currentSaved = saved,
                    progressPercent = progress
                )
            }
        }
    }

    // 5. Calculated People & Debt Ledger
    fun observePeopleWithDebts(): Flow<List<PersonWithDebt>> {
        return combine(
            personDao.getAllPeople(),
            transactionDao.getAllTransactions()
        ) { people, transactions ->
            people.map { person ->
                val lent = transactions
                    .filter { it.personId == person.id && it.type == TransactionType.DEBT_LEND }
                    .sumOf { it.amount }
                val borrowed = transactions
                    .filter { it.personId == person.id && it.type == TransactionType.DEBT_BORROW }
                    .sumOf { it.amount }
                val repayments = transactions
                    .filter { it.personId == person.id && it.type == TransactionType.DEBT_REPAY }
                    .sumOf { it.amount }

                val netBalance = (lent - repayments) - borrowed

                PersonWithDebt(
                    person = person,
                    totalLent = lent,
                    totalBorrowed = borrowed,
                    netBalance = netBalance
                )
            }
        }
    }

    // 6. Calculated Budgets & Spending Progress
    fun observeBudgetsWithProgress(): Flow<List<BudgetWithProgress>> {
        return combine(
            budgetDao.getAllBudgets(),
            categoryDao.getAllCategories(),
            transactionDao.getAllTransactions()
        ) { budgets, categories, transactions ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startOfMonth = calendar.timeInMillis

            budgets.map { budget ->
                val category = categories.find { it.id == budget.categoryId }
                val spent = transactions
                    .filter { tx ->
                        tx.type == TransactionType.EXPENSE &&
                        tx.dateMillis >= startOfMonth &&
                        (budget.categoryId == null || tx.categoryId == budget.categoryId)
                    }
                    .sumOf { it.amount }

                val remaining = budget.limitAmount - spent
                val progress = if (budget.limitAmount > 0) {
                    (spent / budget.limitAmount).toFloat()
                } else 0f

                BudgetWithProgress(
                    budget = budget,
                    category = category,
                    spentAmount = spent,
                    remainingAmount = remaining,
                    progressPercent = progress,
                    isExceeded = spent > budget.limitAmount
                )
            }
        }
    }

    // 7. Categories & Lookups
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> = categoryDao.getCategoriesByType(type)

    fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()
    fun getAllPeople(): Flow<List<Person>> = personDao.getAllPeople()
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()
    fun getAllRecurringRules(): Flow<List<RecurringRule>> = recurringDao.getAllActiveRules()

    fun observeMonthlyStats(): Flow<MonthlyStats> {
        return transactionDao.getAllTransactions().map { transactions ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis

            var income = 0.0
            var expense = 0.0
            transactions.filter { it.dateMillis >= startOfMonth }.forEach { tx ->
                when (tx.type) {
                    TransactionType.INCOME -> income += tx.amount
                    TransactionType.EXPENSE -> expense += tx.amount
                    else -> {}
                }
            }
            MonthlyStats(income = income, expense = expense)
        }
    }

    suspend fun addAccount(account: Account): Long = accountDao.insert(account)
    suspend fun addCategory(category: Category): Long = categoryDao.insert(category)
    suspend fun addPerson(person: Person): Long = personDao.insert(person)
    suspend fun addGoal(goal: Goal): Long = goalDao.insert(goal)
    suspend fun addBudget(budget: Budget): Long = budgetDao.insert(budget)
    suspend fun addRecurringRule(rule: RecurringRule): Long = recurringDao.insert(rule)
}

data class MonthlyStats(
    val income: Double = 0.0,
    val expense: Double = 0.0
)
