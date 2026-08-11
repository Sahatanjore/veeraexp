package com.veeraexp.app.ui.quickadd

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.veeraexp.app.R
import com.veeraexp.app.data.entity.Category
import com.veeraexp.app.data.entity.CategoryType
import com.veeraexp.app.ui.common.RepositoryViewModelFactory
import com.veeraexp.app.ui.common.repositoryOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Fast entry sheet for Income / Expense (spec section 4).
 * Saving is a real DB write through FinanceRepository — balance and
 * home dashboard update immediately because they observe Room via Flow.
 */
class QuickAddSheet : BottomSheetDialogFragment() {

    private var isIncome: Boolean = true

    private val viewModel: QuickAddViewModel by viewModels {
        RepositoryViewModelFactory(repositoryOf(requireContext())) { QuickAddViewModel(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isIncome = arguments?.getBoolean(ARG_IS_INCOME) ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.sheet_quick_add, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.textSheetTitle).text =
            if (isIncome) "Add Income" else "Add Expense"

        val amountInput = view.findViewById<TextInputEditText>(R.id.inputAmount)
        val categoryInput = view.findViewById<AutoCompleteTextView>(R.id.inputCategory)
        val noteInput = view.findViewById<TextInputEditText>(R.id.inputNote)
        val paymentInput = view.findViewById<TextInputEditText>(R.id.inputPaymentMethod)
        val saveButton = view.findViewById<View>(R.id.btnSave)

        var selectedCategory: Category? = null

        lifecycleScope.launch {
            val type = if (isIncome) CategoryType.INCOME else CategoryType.EXPENSE
            val categories = viewModel.categoriesOfType(type).first()
            val names = categories.map { it.name }
            categoryInput.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
            )
            categoryInput.setOnItemClickListener { _, _, position, _ ->
                selectedCategory = categories[position]
            }
            if (categories.isNotEmpty()) {
                categoryInput.setText(categories.first().name, false)
                selectedCategory = categories.first()
            }
        }

        saveButton.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull()
            if (amount == null || amount <= 0) {
                amountInput.error = "Enter a valid amount"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (isIncome) {
                    viewModel.saveIncome(
                        amount, selectedCategory?.id, noteInput.text?.toString().orEmpty(),
                        paymentInput.text?.toString().orEmpty()
                    )
                } else {
                    viewModel.saveExpense(
                        amount, selectedCategory?.id, noteInput.text?.toString().orEmpty(),
                        paymentInput.text?.toString().orEmpty()
                    )
                }
                vibrateShort()
                Toast.makeText(
                    requireContext(),
                    if (isIncome) "Income added" else "Expense added",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
    }

    private fun vibrateShort() {
        val vibrator = requireContext().getSystemService(Vibrator::class.java) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(40)
        }
    }

    companion object {
        private const val ARG_IS_INCOME = "is_income"
        fun newInstance(isIncome: Boolean) = QuickAddSheet().apply {
            arguments = Bundle().apply { putBoolean(ARG_IS_INCOME, isIncome) }
        }
    }
}
