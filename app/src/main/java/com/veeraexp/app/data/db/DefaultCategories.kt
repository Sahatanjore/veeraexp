package com.veeraexp.app.data.db

import com.veeraexp.app.data.entity.Category
import com.veeraexp.app.data.entity.CategoryType

object DefaultCategories {

    fun build(): List<Category> {
        val expenseNames = listOf(
            "Food", "Travel", "Fuel", "Rent", "Electricity", "Water", "Mobile",
            "Internet", "Shopping", "Medical", "Education", "EMI", "Loan",
            "Entertainment", "Temple", "Bills", "Other"
        )
        val incomeNames = listOf(
            "Salary", "Delivery", "Business", "Bonus", "Interest", "Investment", "Other"
        )

        val expense = expenseNames.mapIndexed { i, name ->
            Category(
                name = name,
                type = CategoryType.EXPENSE,
                iconKey = "ic_${name.lowercase()}",
                colorHex = EXPENSE_PALETTE[i % EXPENSE_PALETTE.size],
                isDefault = true
            )
        }
        val income = incomeNames.mapIndexed { i, name ->
            Category(
                name = name,
                type = CategoryType.INCOME,
                iconKey = "ic_${name.lowercase()}",
                colorHex = INCOME_PALETTE[i % INCOME_PALETTE.size],
                isDefault = true
            )
        }
        return expense + income
    }

    private val EXPENSE_PALETTE = listOf(
        "#EF5350", "#EC407A", "#AB47BC", "#7E57C2", "#5C6BC0",
        "#42A5F5", "#29B6F6", "#26C6DA", "#26A69A", "#66BB6A",
        "#9CCC65", "#D4E157", "#FFCA28", "#FFA726", "#FF7043",
        "#8D6E63", "#78909C"
    )
    private val INCOME_PALETTE = listOf(
        "#43A047", "#00897B", "#00ACC1", "#1E88E5", "#3949AB", "#7CB342", "#546E7A"
    )
}
