package com.darkside.wallet.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkside.wallet.data.domain.*
import com.darkside.wallet.data.entity.*
import com.darkside.wallet.utils.*
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class CategorySpending(
    val categoryId: Long,
    val categoryName: String,
    val amount: Double,
    val percentage: Float,
    val color: String
)

@Immutable
data class BudgetWithProgress(
    val budget: BudgetEntity,
    val category: CategoryEntity?,
    val spent: Double,
    val progress: Float
)

@Immutable
data class DailySummary(
    val date: Long,
    val amount: Double
)

class WalletViewModel(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val personRepository: PersonRepository,
    private val loanRepository: LoanRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: GoalRepository,
    private val recurringRepository: RecurringRepository,
    private val labelRepository: LabelRepository,
    private val transactionService: TransactionService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isDynamicToolbarEnabled = MutableStateFlow(sharedPreferences.getBoolean("dynamic_toolbar", false))
    val isDynamicToolbarEnabled: StateFlow<Boolean> = _isDynamicToolbarEnabled.asStateFlow()

    fun toggleDynamicToolbar(enabled: Boolean) {
        _isDynamicToolbarEnabled.value = enabled
        sharedPreferences.edit().putBoolean("dynamic_toolbar", enabled).apply()
    }

    private val _userName = MutableStateFlow(sharedPreferences.getString("user_name", "User") ?: "User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhotoPath = MutableStateFlow(sharedPreferences.getString("user_photo", null))
    val userPhotoPath: StateFlow<String?> = _userPhotoPath.asStateFlow()

    private val _currencyCode = MutableStateFlow(sharedPreferences.getString("currency_code", "INR") ?: "INR")  
    val currencyCode: StateFlow<String> = _currencyCode.asStateFlow()

    private val _isBalanceVisible = MutableStateFlow(sharedPreferences.getBoolean("balance_visible", true))     
    val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible.asStateFlow()

    fun toggleBalanceVisibility() {
        val newState = !_isBalanceVisible.value
        _isBalanceVisible.value = newState
        sharedPreferences.edit().putBoolean("balance_visible", newState).apply()
    }

    fun updateUserName(name: String) {
        _userName.value = name
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    fun updateCurrency(code: String) {
        _currencyCode.value = code
        sharedPreferences.edit().putString("currency_code", code).apply()
    }

    fun updateUserPhoto(path: String?) {
        _userPhotoPath.value = path
        sharedPreferences.edit().putString("user_photo", path).apply()
    }

    // --- Personalization & Theme Preferences ---
    // Theme mode: 0 = System, 1 = Light, 2 = Dark
    private val _themeMode = MutableStateFlow(sharedPreferences.getInt("theme_mode", 0))
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
        sharedPreferences.edit().putInt("theme_mode", mode).apply()
        LogService.info("Theme mode updated to $mode")
    }

    private val _useDynamicColor = MutableStateFlow(sharedPreferences.getBoolean("use_dynamic_color", true))
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor.asStateFlow()

    fun setUseDynamicColor(enabled: Boolean) {
        _useDynamicColor.value = enabled
        sharedPreferences.edit().putBoolean("use_dynamic_color", enabled).apply()
        LogService.info("Dynamic Color set to $enabled")
    }

    private val _colorSchemeVariant = MutableStateFlow(sharedPreferences.getString("color_scheme_variant", "vibrant") ?: "vibrant")
    val colorSchemeVariant: StateFlow<String> = _colorSchemeVariant.asStateFlow()

    fun setColorSchemeVariant(variant: String) {
        _colorSchemeVariant.value = variant
        sharedPreferences.edit().putString("color_scheme_variant", variant).apply()
        LogService.info("Color scheme variant set to $variant")
    }

    // --- Typography Preferences ---
    private val _useGoogleSansFlex = MutableStateFlow(sharedPreferences.getBoolean("use_google_sans_flex", true))
    val useGoogleSansFlex: StateFlow<Boolean> = _useGoogleSansFlex.asStateFlow()

    fun toggleGoogleSans(enabled: Boolean) {
        _useGoogleSansFlex.value = enabled
        sharedPreferences.edit().putBoolean("use_google_sans_flex", enabled).apply()
    }

    private val _fontGrade = MutableStateFlow(sharedPreferences.getFloat("font_grade", 0f))
    val fontGrade: StateFlow<Float> = _fontGrade.asStateFlow()

    fun updateGrade(v: Float) {
        _fontGrade.value = v
        sharedPreferences.edit().putFloat("font_grade", v).apply()
    }

    private val _fontWeight = MutableStateFlow(sharedPreferences.getFloat("font_weight", 400f))
    val fontWeight: StateFlow<Float> = _fontWeight.asStateFlow()

    fun updateWeight(v: Float) {
        _fontWeight.value = v
        sharedPreferences.edit().putFloat("font_weight", v).apply()
    }

    private val _fontWidth = MutableStateFlow(sharedPreferences.getFloat("font_width", 100f))
    val fontWidth: StateFlow<Float> = _fontWidth.asStateFlow()

    fun updateWidth(v: Float) {
        _fontWidth.value = v
        sharedPreferences.edit().putFloat("font_width", v).apply()
    }

    private val _fontRoundness = MutableStateFlow(sharedPreferences.getFloat("font_roundness", 0f))
    val fontRoundness: StateFlow<Float> = _fontRoundness.asStateFlow()

    fun updateFontRoundness(v: Float) {
        _fontRoundness.value = v
        sharedPreferences.edit().putFloat("font_roundness", v).apply()
    }

    private val _fontOpticalSize = MutableStateFlow(sharedPreferences.getFloat("font_optical_size", 14f))
    val fontOpticalSize: StateFlow<Float> = _fontOpticalSize.asStateFlow()

    fun updateOpticalSize(v: Float) {
        _fontOpticalSize.value = v
        sharedPreferences.edit().putFloat("font_optical_size", v).apply()
    }

    fun resetTypography() {
        _fontGrade.value = 0f
        _fontWeight.value = 400f
        _fontWidth.value = 100f
        _fontRoundness.value = 0f
        _fontOpticalSize.value = 14f
        sharedPreferences.edit()
            .putFloat("font_grade", 0f)
            .putFloat("font_weight", 400f)
            .putFloat("font_width", 100f)
            .putFloat("font_roundness", 0f)
            .putFloat("font_optical_size", 14f)
            .apply()
        LogService.info("Typography reset to default settings")
    }

    // --- Feedback & Behavior ---
    private val _vibrateOnTransaction = MutableStateFlow(sharedPreferences.getBoolean("vibrate_on_transaction", true))
    val vibrateOnTransaction: StateFlow<Boolean> = _vibrateOnTransaction.asStateFlow()

    fun toggleVibrateOnTransaction(v: Boolean) {
        _vibrateOnTransaction.value = v
        sharedPreferences.edit().putBoolean("vibrate_on_transaction", v).apply()
    }

    private val _shouldRestartOnCurrencyChange = MutableStateFlow(sharedPreferences.getBoolean("restart_on_currency_change", false))
    val shouldRestartOnCurrencyChange: StateFlow<Boolean> = _shouldRestartOnCurrencyChange.asStateFlow()

    fun toggleRestartOnCurrencyChange(v: Boolean) {
        _shouldRestartOnCurrencyChange.value = v
        sharedPreferences.edit().putBoolean("restart_on_currency_change", v).apply()
    }

    // --- Error Collector (Logcat) ---
    private val _isErrorCollectorEnabled = MutableStateFlow(sharedPreferences.getBoolean("error_collector_enabled", false))
    val isErrorCollectorEnabled: StateFlow<Boolean> = _isErrorCollectorEnabled.asStateFlow()

    fun enableErrorCollector() {
        _isErrorCollectorEnabled.value = true
        sharedPreferences.edit().putBoolean("error_collector_enabled", true).apply()
        LogService.info("Error Collector (LogCat) Activated by Developer Easter Egg")
    }

    fun toggleErrorCollector(enabled: Boolean) {
        _isErrorCollectorEnabled.value = enabled
        sharedPreferences.edit().putBoolean("error_collector_enabled", enabled).apply()
    }

    val logs: StateFlow<List<LogEntry>> = LogService.logs

    fun clearLogs() {
        LogService.clear()
    }

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = transactionRepository.getActiveTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = budgetRepository.getAllBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgetProgress: StateFlow<List<BudgetWithProgress>> = combine(budgets, recentTransactions, categories) { budgetList, transactions, cats ->
        budgetList.map { budget ->
            val spent = transactions
                .filter { it.categoryId == budget.categoryId && it.type == TransactionType.EXPENSE && it.date >= budget.startDate }
                .sumOf { it.amount }
            val progress = if (budget.amount > 0) (spent / budget.amount).toFloat() else 0f
            BudgetWithProgress(
                budget = budget,
                category = cats.find { it.id == budget.categoryId },
                spent = spent,
                progress = progress
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = goalRepository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurringTransactions: StateFlow<List<RecurringEntity>> = recurringRepository.getAllRecurring()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val persons: StateFlow<List<PersonEntity>> = personRepository.getAllPeople()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanEntity>> = loanRepository.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val labels: StateFlow<List<LabelEntity>> = labelRepository.getAllLabels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailySpending: StateFlow<List<DailySummary>> = recentTransactions.map { transactions ->
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        transactions
            .filter { it.type == TransactionType.EXPENSE && it.date >= thirtyDaysAgo }
            .groupBy { 
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = it.date }
                cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                cal.set(java.util.Calendar.MINUTE, 0)
                cal.set(java.util.Calendar.SECOND, 0)
                cal.set(java.util.Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            .map { (date, list) -> DailySummary(date, list.sumOf { it.amount }) }
            .sortedBy { it.date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                transactionService.deleteTransaction(transaction)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    val totalIncome: StateFlow<Double> = recentTransactions.map { txs ->
        txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = recentTransactions.map { txs ->
        txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalBalance: StateFlow<Double> = accounts.map { accs ->
        accs.sumOf { it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val spendingByCategory: StateFlow<List<CategorySpending>> = combine(recentTransactions, categories, totalExpense) { transactions, cats, expense ->
        if (expense <= 0) return@combine emptyList<CategorySpending>()

        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, list) ->
                val category = cats.find { it.id == categoryId } ?: return@mapNotNull null
                val amount = list.sumOf { it.amount }
                CategorySpending(
                    categoryId = categoryId,
                    categoryName = category.name,
                    amount = amount,
                    percentage = (amount / expense).toFloat(),
                    color = category.color
                )
            }
            .sortedByDescending { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(
        amount: Double,
        note: String?,
        categoryId: Long,
        type: TransactionType,
        accountId: Long,
        transferAccountId: Long? = null,
        personId: Long = 0,
        date: Long = System.currentTimeMillis(),
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                transactionService.addTransaction(
                    amount = amount,
                    note = note,
                    categoryId = categoryId,
                    type = type,
                    accountId = accountId,
                    transferAccountId = transferAccountId,
                    personId = personId,
                    date = date
                )
                withContext(Dispatchers.Main) { onComplete() }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.insertTransaction(transaction)
        }
    }

    fun addCategory(name: String, type: CategoryType = CategoryType.EXPENSE) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.insertCategory(CategoryEntity(name = name, type = type))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.deleteCategory(category)
        }
    }

    fun addBudget(amount: Double, categoryId: Long, period: BudgetPeriod) {
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.insertBudget(
                BudgetEntity(
                    amount = amount,
                    categoryId = categoryId,
                    period = period
                )
            )
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.deleteBudget(budget)
        }
    }

    fun addGoal(name: String, targetAmount: Double, deadline: Long, color: String = "0xFF2196F3") {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.insertGoal(GoalEntity(name = name, targetAmount = targetAmount, deadline = deadline, color = color))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.deleteGoal(goal)
        }
    }

    fun updateGoalSavedAmount(goal: GoalEntity, amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.updateGoal(goal.copy(currentAmount = goal.currentAmount + amount))
        }
    }

    fun addLoan(personId: Long, amount: Double, type: LoanType, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            loanRepository.insertLoan(LoanEntity(personId = personId, amount = amount, type = type, note = note))
        }
    }

    fun addLoans(personIds: List<Long>, amount: Double, type: LoanType, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            personIds.forEach { personId ->
                loanRepository.insertLoan(LoanEntity(personId = personId, amount = amount, type = type, note = note))
            }
        }
    }

    fun toggleLoanSettled(loan: LoanEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            loanRepository.updateLoan(loan.copy(isPaid = !loan.isPaid))
        }
    }

    fun addPerson(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            personRepository.insertPerson(PersonEntity(name = name))
        }
    }

    fun addPerson(context: Context, name: String, photoUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            val photoPath = photoUri?.let { FileUtils.saveUriToFile(context, it) }
            personRepository.insertPerson(PersonEntity(name = name, avatar = photoPath))
        }
    }

    fun deletePerson(person: PersonEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            personRepository.deletePerson(person)
        }
    }

    fun addLabel(name: String, color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            labelRepository.insertLabel(LabelEntity(name = name, color = color))
        }
    }

    fun deleteLabel(label: LabelEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            labelRepository.deleteLabel(label)
        }
    }

    fun addAccount(name: String, type: AccountType, balance: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.insertAccount(AccountEntity(name = name, type = type, balance = balance))
        }
    }

    fun addRecurring(
        amount: Double,
        name: String,
        categoryId: Long,
        type: TransactionType,
        frequency: RecurrenceFrequency,
        accountId: Long = 1, // Default to first account for now
        startDate: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            recurringRepository.insertRecurring(
                RecurringEntity(
                    amount = amount,
                    name = name,
                    categoryId = categoryId,
                    type = type,
                    frequency = frequency,
                    accountId = accountId,
                    nextDate = startDate
                )
            )
        }
    }

    fun deleteRecurring(recurring: RecurringEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            recurringRepository.deleteRecurring(recurring)
        }
    }

    fun clearError() { _error.value = null }

    fun exportTransactions(context: Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = transactionRepository.getAllTransactions().first()
            val success = CsvExportUtil.exportTransactionsToCsv(context, transactions)
            withContext(Dispatchers.Main) { onComplete(success) }
        }
    }

    fun importTransactions(context: Context, uri: android.net.Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = CsvImportUtil.importTransactionsFromCsv(context, uri, transactionService)
            withContext(Dispatchers.Main) { onComplete(success) }
        }
    }

    fun exportData(context: Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val accounts = accountRepository.getAllAccounts().first()
            val categories = categoryRepository.getAllCategories().first()
            val transactions = transactionRepository.getAllTransactions().first()
            val budgets = budgetRepository.getAllBudgets().first()
            val goals = goalRepository.getAllGoals().first()
            val people = personRepository.getAllPeople().first()
            val loans = loanRepository.getAllLoans().first()
            val recurring = recurringRepository.getAllRecurring().first()

            val success = JsonDataUtil.exportDataToJson(
                context, accounts, categories, transactions, budgets, goals, people, loans, recurring
            )
            withContext(Dispatchers.Main) { onComplete(success) }
        }
    }

    fun importData(context: Context, uri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = JsonDataUtil.importDataFromJson(context, uri) { root ->
                try {
                    transactionService.clearAllData()

                    val accountsArray = root.getJSONArray("accounts")
                    for (i in 0 until accountsArray.length()) {
                        val obj = accountsArray.getJSONObject(i)
                        accountRepository.insertAccount(AccountEntity(
                            uuid = obj.getString("uuid"),
                            name = obj.getString("name"),
                            bankName = obj.getString("bankName"),
                            number = obj.getString("number"),
                            validThru = obj.getLong("validThru"),
                            icon = obj.getString("icon"),
                            color = obj.getString("color"),
                            isPredefined = obj.getBoolean("isPredefined"),
                            balance = obj.getDouble("balance"),
                            isArchived = obj.getBoolean("isArchived"),
                            isDeleted = obj.getBoolean("isDeleted"),
                            isDefault = obj.getBoolean("isDefault"),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt"),
                            order = obj.getInt("order"),
                            type = AccountType.valueOf(obj.getString("type"))
                        ))
                    }

                    val categoriesArray = root.getJSONArray("categories")
                    for (i in 0 until categoriesArray.length()) {
                        val obj = categoriesArray.getJSONObject(i)
                        categoryRepository.insertCategory(CategoryEntity(
                            uuid = obj.getString("uuid"),
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            icon = obj.getString("icon"),
                            color = obj.getString("color"),
                            budgetLimit = if (obj.isNull("budgetLimit")) null else obj.getDouble("budgetLimit"),
                            isBudget = obj.getBoolean("isBudget"),
                            isPredefined = obj.getBoolean("isPredefined"),
                            isDeleted = obj.getBoolean("isDeleted"),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt"),
                            type = CategoryType.valueOf(obj.getString("type"))
                        ))
                    }

                    val transactionsArray = root.getJSONArray("transactions")
                    for (i in 0 until transactionsArray.length()) {
                        val obj = transactionsArray.getJSONObject(i)
                        transactionRepository.insertTransaction(TransactionEntity(
                            uuid = obj.getString("uuid"),
                            amount = obj.getDouble("amount"),
                            note = if (obj.isNull("note")) null else obj.getString("note"),
                            date = obj.getLong("date"),
                            type = TransactionType.valueOf(obj.getString("type")),
                            categoryId = obj.getLong("categoryId"),
                            accountId = obj.getLong("accountId"),
                            personId = if (obj.isNull("personId")) 0L else obj.getLong("personId"),
                            transferAccountId = if (obj.isNull("transferAccountId")) null else obj.getLong("transferAccountId"),
                            tags = if (obj.isNull("tags")) null else obj.getString("tags"),
                            icon = if (obj.isNull("icon")) null else obj.getString("icon"),
                            color = if (obj.isNull("color")) null else obj.getString("color"),
                            isTemplate = obj.optBoolean("isTemplate", false),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt")
                        ))
                    }

                    val budgetsArray = root.getJSONArray("budgets")
                    for (i in 0 until budgetsArray.length()) {
                        val obj = budgetsArray.getJSONObject(i)
                        budgetRepository.insertBudget(BudgetEntity(
                            uuid = obj.getString("uuid"),
                            amount = obj.getDouble("amount"),
                            categoryId = obj.getLong("categoryId"),
                            period = BudgetPeriod.valueOf(obj.getString("period")),
                            startDate = obj.getLong("startDate"),
                            endDate = obj.getLong("endDate"),
                            isActive = obj.getBoolean("isActive"),
                            isDeleted = obj.getBoolean("isDeleted"),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt")
                        ))
                    }

                    val goalsArray = root.getJSONArray("goals")
                    for (i in 0 until goalsArray.length()) {
                        val obj = goalsArray.getJSONObject(i)
                        goalRepository.insertGoal(GoalEntity(
                            uuid = obj.getString("uuid"),
                            name = obj.getString("name"),
                            targetAmount = obj.getDouble("targetAmount"),
                            currentAmount = obj.getDouble("currentAmount"),
                            deadline = obj.getLong("deadline"),
                            color = obj.getString("color"),
                            icon = if (obj.isNull("icon")) null else obj.getString("icon"),
                            accountId = if (obj.isNull("accountId")) null else obj.getLong("accountId"),
                            isCompleted = obj.getBoolean("isCompleted"),
                            isDeleted = obj.getBoolean("isDeleted"),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt")
                        ))
                    }

                    val peopleArray = root.getJSONArray("people")
                    for (i in 0 until peopleArray.length()) {
                        val obj = peopleArray.getJSONObject(i)
                        personRepository.insertPerson(PersonEntity(
                            uuid = obj.getString("uuid"),
                            name = obj.getString("name"),
                            contact = if (obj.isNull("contact")) null else obj.getString("contact"),
                            avatar = if (obj.isNull("avatar")) null else obj.getString("avatar"),
                            color = obj.getString("color"),
                            isDeleted = obj.getBoolean("isDeleted"),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt")
                        ))
                    }

                    val loansArray = root.getJSONArray("loans")
                    for (i in 0 until loansArray.length()) {
                        val obj = loansArray.getJSONObject(i)
                        loanRepository.insertLoan(LoanEntity(
                            uuid = obj.getString("uuid"),
                            personId = obj.getLong("personId"),
                            amount = obj.getDouble("amount"),
                            type = LoanType.valueOf(obj.getString("type")),
                            dueDate = if (obj.isNull("dueDate")) null else obj.getLong("dueDate"),
                            isPaid = obj.getBoolean("isPaid"),
                            isActive = obj.getBoolean("isActive"),
                            note = if (obj.isNull("note")) null else obj.getString("note"),
                            isDeleted = obj.getBoolean("isDeleted"),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt")
                        ))
                    }

                    val recurringArray = root.getJSONArray("recurring")
                    for (i in 0 until recurringArray.length()) {
                        val obj = recurringArray.getJSONObject(i)
                        recurringRepository.insertRecurring(RecurringEntity(
                            uuid = obj.getString("uuid"),
                            name = obj.getString("name"),
                            amount = obj.getDouble("amount"),
                            type = TransactionType.valueOf(obj.getString("type")),
                            accountId = obj.getLong("accountId"),
                            categoryId = obj.getLong("categoryId"),
                            transferAccountId = if (obj.isNull("transferAccountId")) null else obj.getLong("transferAccountId"),
                            frequency = RecurrenceFrequency.valueOf(obj.getString("frequency")),
                            nextDate = obj.getLong("nextDate"),
                            endDate = if (obj.isNull("endDate")) null else obj.getLong("endDate"),
                            isActive = obj.getBoolean("isActive"),
                            isDeleted = obj.getBoolean("isDeleted"),
                            createdAt = obj.getLong("createdAt"),
                            updatedAt = obj.getLong("updatedAt")
                        ))
                    }

                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            withContext(Dispatchers.Main) { onComplete(success) }
        }
    }
    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.updateAccount(account)
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.updateCategory(category)
        }
    }

    fun updatePerson(person: PersonEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            personRepository.updatePerson(person)
        }
    }

    fun deleteLoan(loan: LoanEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            loanRepository.deleteLoan(loan)
        }
    }

    fun factoryReset(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                transactionService.clearAllData()
                sharedPreferences.edit().clear().apply()
                LogService.clear()
                withContext(Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    class Factory(
        private val accountRepository: AccountRepository,
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository,
        private val personRepository: PersonRepository,
        private val loanRepository: LoanRepository,
        private val budgetRepository: BudgetRepository,
        private val goalRepository: GoalRepository,
        private val recurringRepository: RecurringRepository,
        private val labelRepository: LabelRepository,
        private val transactionService: TransactionService,
        private val sharedPreferences: SharedPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(
                    accountRepository, transactionRepository, categoryRepository,
                    personRepository, loanRepository, budgetRepository,
                    goalRepository, recurringRepository, labelRepository,
                    transactionService, sharedPreferences
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
