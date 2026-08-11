package com.veeraexp.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeraexp.app.data.entity.AppSettings
import com.veeraexp.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: FinanceRepository) : ViewModel() {

    val settings = repository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setOpeningBalance(amount: Double) {
        viewModelScope.launch { repository.setOpeningBalance(amount) }
    }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            val current = settings.value ?: AppSettings()
            repository.saveSettings(transform(current))
        }
    }
}
