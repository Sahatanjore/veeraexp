package com.veeraexp.app.ui.quickadd

import androidx.lifecycle.ViewModel
import com.veeraexp.app.data.entity.CategoryType
import com.veeraexp.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.map

class QuickAddViewModel(private val repository: FinanceRepository) : ViewModel() {

    fun categoriesOfType(type: CategoryType) =
        repository.observeCategories().map { list -> list.filter { it.type == type } }

    suspend fun saveIncome(amount: Double, categoryId: Long?, note: String, paymentMethod: String) {
        repository.addIncome(amount, categoryId, note, System.currentTimeMillis(), paymentMethod, null)
    }

    suspend fun saveExpense(amount: Double, categoryId: Long?, note: String, paymentMethod: String) {
        repository.addExpense(amount, categoryId, note, System.currentTimeMillis(), paymentMethod, null)
    }
}
