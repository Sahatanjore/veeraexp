package com.veeraexp.app.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeraexp.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(private val repository: FinanceRepository) : ViewModel() {

    val goals = repository.observeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createGoal(name: String, target: Double) {
        viewModelScope.launch { repository.createGoal(name, target) }
    }

    fun contribute(goalId: Long, amount: Double) {
        viewModelScope.launch {
            repository.contributeToGoal(goalId, amount, "Manual contribution", System.currentTimeMillis())
        }
    }
}
