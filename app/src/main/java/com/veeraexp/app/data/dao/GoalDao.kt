package com.veeraexp.app.data.dao

import androidx.room.*
import com.veeraexp.app.data.entity.InvestmentGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(goal: InvestmentGoal): Long

    @Update
    suspend fun update(goal: InvestmentGoal)

    @Delete
    suspend fun delete(goal: InvestmentGoal)

    @Query("SELECT * FROM investment_goals ORDER BY isCompleted ASC, createdAt DESC")
    fun getAll(): Flow<List<InvestmentGoal>>

    @Query("SELECT * FROM investment_goals WHERE id = :id")
    suspend fun getById(id: Long): InvestmentGoal?

    // Called after inserting/removing a GOAL_TRANSFER transaction to keep
    // the denormalized savedAmount cache in sync.
    @Query("UPDATE investment_goals SET savedAmount = savedAmount + :delta WHERE id = :goalId")
    suspend fun adjustSavedAmount(goalId: Long, delta: Double)

    @Query("UPDATE investment_goals SET isCompleted = 1 WHERE id = :goalId")
    suspend fun markCompleted(goalId: Long)
}
