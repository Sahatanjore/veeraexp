package com.veeraexp.app.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.veeraexp.app.R
import com.veeraexp.app.ui.common.RepositoryViewModelFactory
import com.veeraexp.app.ui.common.repositoryOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels {
        RepositoryViewModelFactory(repositoryOf(requireContext())) { SettingsViewModel(it) }
    }

    private var suppressListeners = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val openingBalanceInput = view.findViewById<TextInputEditText>(R.id.inputOpeningBalance)
        val saveBalanceBtn = view.findViewById<View>(R.id.btnSaveBalance)
        val darkModeGroup = view.findViewById<RadioGroup>(R.id.radioDarkMode)
        val languageGroup = view.findViewById<RadioGroup>(R.id.radioLanguage)
        val soundSwitch = view.findViewById<SwitchMaterial>(R.id.switchSound)
        val vibrationSwitch = view.findViewById<SwitchMaterial>(R.id.switchVibration)
        val floatingSwitch = view.findViewById<SwitchMaterial>(R.id.switchFloatingQuickAdd)

        saveBalanceBtn.setOnClickListener {
            val amount = openingBalanceInput.text.toString().toDoubleOrNull()
            if (amount != null) viewModel.setOpeningBalance(amount)
        }

        darkModeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (suppressListeners) return@setOnCheckedChangeListener
            val mode = when (checkedId) {
                R.id.radioLight -> "light"
                R.id.radioDark -> "dark"
                else -> "system"
            }
            viewModel.updateSettings { it.copy(darkModeMode = mode) }
        }

        languageGroup.setOnCheckedChangeListener { _, checkedId ->
            if (suppressListeners) return@setOnCheckedChangeListener
            val lang = if (checkedId == R.id.radioTamil) "ta" else "en"
            viewModel.updateSettings { it.copy(languageCode = lang) }
        }

        soundSwitch.setOnCheckedChangeListener { _, checked ->
            if (!suppressListeners) viewModel.updateSettings { it.copy(soundEnabled = checked) }
        }
        vibrationSwitch.setOnCheckedChangeListener { _, checked ->
            if (!suppressListeners) viewModel.updateSettings { it.copy(vibrationEnabled = checked) }
        }
        floatingSwitch.setOnCheckedChangeListener { _, checked ->
            if (!suppressListeners) viewModel.updateSettings { it.copy(floatingQuickAddEnabled = checked) }
        }

        viewModel.settings.onEach { settings ->
            if (settings == null) return@onEach
            suppressListeners = true
            openingBalanceInput.setText(settings.openingBalance.toString())
            darkModeGroup.check(
                when (settings.darkModeMode) {
                    "light" -> R.id.radioLight
                    "dark" -> R.id.radioDark
                    else -> R.id.radioSystem
                }
            )
            languageGroup.check(if (settings.languageCode == "ta") R.id.radioTamil else R.id.radioEnglish)
            soundSwitch.isChecked = settings.soundEnabled
            vibrationSwitch.isChecked = settings.vibrationEnabled
            floatingSwitch.isChecked = settings.floatingQuickAddEnabled
            suppressListeners = false
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
