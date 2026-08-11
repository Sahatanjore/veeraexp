package com.veeraexp.app.ui.goals

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.veeraexp.app.R
import com.veeraexp.app.data.entity.InvestmentGoal
import com.veeraexp.app.ui.common.RepositoryViewModelFactory
import com.veeraexp.app.ui.common.repositoryOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GoalsFragment : Fragment(R.layout.fragment_goals) {

    private val viewModel: GoalsViewModel by viewModels {
        RepositoryViewModelFactory(repositoryOf(requireContext())) { GoalsViewModel(it) }
    }

    private lateinit var adapter: GoalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GoalAdapter(onAddMoney = { goal -> showAddMoneyDialog(goal) })

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerGoals)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fabAddGoal).setOnClickListener {
            showCreateGoalDialog()
        }

        viewModel.goals.onEach { adapter.submitList(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showCreateGoalDialog() {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }
        val nameInput = EditText(requireContext()).apply { hint = "Goal name (e.g. New Phone)" }
        val targetInput = EditText(requireContext()).apply {
            hint = "Target amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        container.addView(nameInput)
        container.addView(targetInput)

        AlertDialog.Builder(requireContext())
            .setTitle("New Investment Goal")
            .setView(container)
            .setPositiveButton("Create") { _, _ ->
                val name = nameInput.text.toString().trim()
                val target = targetInput.text.toString().toDoubleOrNull()
                if (name.isNotBlank() && target != null && target > 0) {
                    viewModel.createGoal(name, target)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddMoneyDialog(goal: InvestmentGoal) {
        val amountInput = EditText(requireContext()).apply {
            hint = "Amount to add"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(48, 24, 48, 0)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Add money to ${goal.name}")
            .setMessage("This transfers from your spendable balance into this goal.")
            .setView(amountInput)
            .setPositiveButton("Add") { _, _ ->
                val amount = amountInput.text.toString().toDoubleOrNull()
                if (amount != null && amount > 0) {
                    viewModel.contribute(goal.id, amount)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
