package com.veeraexp.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investment_goals")
data class InvestmentGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,   // denormalized cache; kept in sync via GOAL_TRANSFER transactions
    val targetDate: Long? = null,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
