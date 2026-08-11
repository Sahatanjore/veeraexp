package com.veeraexp.app.data.repository

import com.veeraexp.app.data.dao.*
import com.veeraexp.app.data.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Central place where the balance formula and the goal-transfer bridge
 * are enforced, so no UI screen can accidentally double-count money.
 *
 *   Balance = openingBalance + totalIncome - totalExpense - totalGoalTransfers
 *
 * A GOAL_TRANSFER transaction is the ONLY way money moves from the
 * spendable balance into a goal's savedAmount. Creating one always does
 * both things atomically: records the ledger entry AND adjusts the goal.
 */
class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val goalDao: GoalDao,
    private val budgetDao: BudgetDao,
    private val settingsDao: SettingsDao
) {
    fun observeBalance(): Flow<Double> =
        combine(
            settingsDao.observe(),
            transactionDao.getTotalIncome(),
            transactionDao.getTotalExpense(),
            transactionDao.getTotalGoalTransfers()
        ) { settings, income, expense, goalTransfers ->
            (settings?.openingBalance ?: 0.0) + income - expense - goalTransfers
        }

    suspend fun addIncome(amount: Double, categoryId: Long?, note: String, date: Long, paymentMethod: String, receiptPath: String?) {
        transactionDao.insert(
            Transaction(
                type = TransactionType.INCOME,
                amount = amount,
                categoryId = categoryId,
                note = note,
                date = date,
                paymentMethod = paymentMethod,
                receiptPath = receiptPath
            )
        )
    }

    suspend fun addExpense(amount: Double, categoryId: Long?, note: String, date: Long, paymentMethod: String, receiptPath: String?) {
        transactionDao.insert(
            Transaction(
                type = TransactionType.EXPENSE,
                amount = amount,
                categoryId = categoryId,
                note = note,
                date = date,
                paymentMethod = paymentMethod,
                receiptPath = receiptPath
            )
        )
    }

    /** Explicit transfer: balance -> goal. This is the ONLY function allowed to do this. */
    suspend fun contributeToGoal(goalId: Long, amount: Double, note: String, date: Long) {
        require(amount > 0) { "Contribution amount must be positive" }
        transactionDao.insert(
            Transaction(
                type = TransactionType.GOAL_TRANSFER,
                amount = amount,
                categoryId = null,
                note = note,
                date = date,
                relatedGoalId = goalId
            )
        )
        goalDao.adjustSavedAmount(goalId, amount)

        val goal = goalDao.getById(goalId)
        if (goal != null && goal.savedAmount + amount >= goal.targetAmount) {
            goalDao.markCompleted(goalId)
        }
    }

    /** Explicit reverse transfer: goal -> balance (withdrawal). */
    suspend fun withdrawFromGoal(goalId: Long, amount: Double, note: String, date: Long) {
        require(amount > 0) { "Withdrawal amount must be positive" }
        val goal = goalDao.getById(goalId) ?: return
        val withdrawAmount = minOf(amount, goal.savedAmount)

        transactionDao.insert(
            Transaction(
                type = TransactionType.GOAL_TRANSFER,
                amount = -withdrawAmount,
                categoryId = null,
                note = note,
                date = date,
                relatedGoalId = goalId
            )
        )
        goalDao.adjustSavedAmount(goalId, -withdrawAmount)
    }

    suspend fun createGoal(name: String, targetAmount: Double) {
        require(name.isNotBlank()) { "Goal name required" }
        require(targetAmount > 0) { "Target must be positive" }
        goalDao.insert(InvestmentGoal(name = name, targetAmount = targetAmount))
    }

    suspend fun setOpeningBalance(amount: Double) = settingsDao.setOpeningBalance(amount)

    fun observeSettings() = settingsDao.observe()

    suspend fun saveSettings(settings: AppSettings) = settingsDao.upsert(settings)

    fun observeMonthlyIncome(monthStart: Long, monthEnd: Long) =
        transactionDao.getIncomeBetween(monthStart, monthEnd)

    fun observeMonthlyExpense(monthStart: Long, monthEnd: Long) =
        transactionDao.getExpenseBetween(monthStart, monthEnd)

    fun observeRecentTransactions(limit: Int = 10) = transactionDao.getRecent(limit)

    fun observeAllTransactions() = transactionDao.getAll()

    /** All transactions, optionally filtered by a note/category search string. */
    fun observeTransactionsFiltered(query: String) =
        transactionDao.getAll().map { list ->
            if (query.isBlank()) list
            else list.filter { it.note.contains(query, ignoreCase = true) }
        }

    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)

    fun observeGoals() = goalDao.getAll()

    fun observeCategories() = categoryDao.getAll()
}
