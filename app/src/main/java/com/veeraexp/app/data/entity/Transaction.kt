package com.veeraexp.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType { INCOME, EXPENSE, GOAL_TRANSFER }

/**
 * A single ledger entry.
 *
 * IMPORTANT: Opening balance is NOT stored here — it lives in Settings.
 * Balance = opening balance + sum(INCOME) - sum(EXPENSE) - sum(GOAL_TRANSFER)
 *
 * GOAL_TRANSFER represents money explicitly moved out of the spendable
 * balance into an investment goal (see InvestmentGoal / GoalContribution).
 * It is the only bridge between the two modules, and it only exists
 * when the user explicitly records a transfer.
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("date"), Index("type")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val categoryId: Long?,
    val note: String = "",
    val date: Long,                 // epoch millis
    val paymentMethod: String = "",
    val receiptPath: String? = null,
    val relatedGoalId: Long? = null, // set when type == GOAL_TRANSFER
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
