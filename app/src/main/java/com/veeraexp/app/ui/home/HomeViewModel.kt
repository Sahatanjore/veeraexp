package com.veeraexp.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeraexp.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class HomeViewModel(private val repository: FinanceRepository) : ViewModel() {

    private fun monthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }

    val balance = repository.observeBalance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlySummary = run {
        val (start, end) = monthRange()
        combine(
            repository.observeMonthlyIncome(start, end),
            repository.observeMonthlyExpense(start, end)
        ) { income, expense -> income to expense }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0 to 0.0)
    }

    val recentTransactions = repository.observeRecentTransactions(8)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = repository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
