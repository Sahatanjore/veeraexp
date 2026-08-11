package com.veeraexp.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.veeraexp.app.R
import com.veeraexp.app.data.entity.Category
import com.veeraexp.app.ui.common.RepositoryViewModelFactory
import com.veeraexp.app.ui.common.TransactionAdapter
import com.veeraexp.app.ui.common.repositoryOf
import com.veeraexp.app.ui.quickadd.QuickAddSheet
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels {
        RepositoryViewModelFactory(repositoryOf(requireContext())) { HomeViewModel(it) }
    }

    private val adapter = TransactionAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textBalance = view.findViewById<TextView>(R.id.textBalance)
        val textMonthIncome = view.findViewById<TextView>(R.id.textMonthIncome)
        val textMonthExpense = view.findViewById<TextView>(R.id.textMonthExpense)
        val textSaha = view.findViewById<TextView>(R.id.textSahaInsight)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerRecent)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<View>(R.id.btnAddIncome).setOnClickListener {
            QuickAddSheet.newInstance(isIncome = true).show(childFragmentManager, "quickAddIncome")
        }
        view.findViewById<View>(R.id.btnAddExpense).setOnClickListener {
            QuickAddSheet.newInstance(isIncome = false).show(childFragmentManager, "quickAddExpense")
        }

        viewModel.balance.onEach { balance ->
            textBalance.text = "₹%,.2f".format(balance)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.monthlySummary.onEach { (income, expense) ->
            textMonthIncome.text = "₹%,.2f".format(income)
            textMonthExpense.text = "₹%,.2f".format(expense)
            textSaha.text = buildSahaSummary(income, expense)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.recentTransactions.onEach { list ->
            adapter.submitList(list)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.categories.onEach { cats: List<Category> ->
            adapter.categoryMap = cats.associateBy { it.id }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun buildSahaSummary(income: Double, expense: Double): String {
        if (income == 0.0 && expense == 0.0) {
            return "Add a few transactions and SAHA will start giving you insights."
        }
        val saved = income - expense
        return if (saved >= 0)
            "You've saved ₹%,.2f this month so far. Keep it up!".format(saved)
        else
            "You've spent ₹%,.2f more than you've earned this month.".format(-saved)
    }
}
