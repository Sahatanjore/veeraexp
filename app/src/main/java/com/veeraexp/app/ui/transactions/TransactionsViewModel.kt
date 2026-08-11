package com.veeraexp.app.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeraexp.app.data.entity.Transaction
import com.veeraexp.app.data.repository.FinanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionsViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val query = MutableStateFlow("")

    fun setQuery(q: String) { query.value = q }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = query.flatMapLatest { q -> repository.observeTransactionsFiltered(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = repository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(transaction: Transaction) {
        viewModelScope.launch { repository.deleteTransaction(transaction) }
    }
}
