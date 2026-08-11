package com.veeraexp.app.data.dao

import androidx.room.*
import com.veeraexp.app.data.entity.Transaction
import com.veeraexp.app.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?

    @Query(
        """
        SELECT * FROM transactions
        WHERE (:type IS NULL OR type = :type)
        AND (:categoryId IS NULL OR categoryId = :categoryId)
        AND (date BETWEEN :startDate AND :endDate)
        AND (note LIKE '%' || :query || '%')
        ORDER BY 
            CASE WHEN :sortByAmount = 1 THEN amount END DESC,
            date DESC
        """
    )
    fun search(
        type: TransactionType?,
        categoryId: Long?,
        startDate: Long,
        endDate: Long,
        query: String = "",
        sortByAmount: Boolean = false
    ): Flow<List<Transaction>>

    // SUM helpers — the single source of truth for balance calculations.
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'GOAL_TRANSFER'")
    fun getTotalGoalTransfers(): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end"
    )
    fun getIncomeBetween(start: Long, end: Long): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end"
    )
    fun getExpenseBetween(start: Long, end: Long): Flow<Double>

    @Query(
        """
        SELECT categoryId, SUM(amount) as total FROM transactions
        WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end
        GROUP BY categoryId ORDER BY total DESC
        """
    )
    suspend fun getExpenseTotalsByCategory(start: Long, end: Long): List<CategoryTotal>
}

data class CategoryTotal(val categoryId: Long?, val total: Double)
