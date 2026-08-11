package com.veeraexp.app.data.dao

import androidx.room.*
import com.veeraexp.app.data.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth")
    fun getForMonth(yearMonth: String): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId IS NULL LIMIT 1")
    suspend fun getOverallForMonth(yearMonth: String): Budget?

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId = :categoryId LIMIT 1")
    suspend fun getForCategoryAndMonth(yearMonth: String, categoryId: Long): Budget?
}
