package com.veeraexp.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.veeraexp.app.data.repository.FinanceRepository

class RepositoryViewModelFactory(
    private val repository: FinanceRepository,
    private val creator: (FinanceRepository) -> ViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator(repository) as T
}

fun repositoryOf(context: android.content.Context): FinanceRepository {
    val app = context.applicationContext as com.veeraexp.app.VeeraExpApplication
    val db = app.database
    return FinanceRepository(
        db.transactionDao(), db.categoryDao(), db.goalDao(), db.budgetDao(), db.settingsDao()
    )
}
