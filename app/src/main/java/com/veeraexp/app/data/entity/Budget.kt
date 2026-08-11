package com.veeraexp.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A budget for a given month (yearMonth = "2026-08").
 * categoryId == null means this row is the OVERALL monthly budget.
 */
@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val yearMonth: String,
    val categoryId: Long?,
    val limitAmount: Double
)
