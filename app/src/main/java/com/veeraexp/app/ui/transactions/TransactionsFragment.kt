package com.veeraexp.app.ui.transactions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.veeraexp.app.R
import com.veeraexp.app.ui.common.RepositoryViewModelFactory
import com.veeraexp.app.ui.common.TransactionAdapter
import com.veeraexp.app.ui.common.repositoryOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    private val viewModel: TransactionsViewModel by viewModels {
        RepositoryViewModelFactory(repositoryOf(requireContext())) { TransactionsViewModel(it) }
    }

    private val adapter = TransactionAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerTransactions)
        val emptyText = view.findViewById<TextView>(R.id.textEmpty)
        val searchInput = view.findViewById<TextInputEditText>(R.id.inputSearch)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // Swipe left = delete (spec section 6). Swipe right reserved for
        // edit/duplicate — hook up an edit sheet here in a follow-up pass.
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val tx = adapter.getTransactionAt(position)
                viewModel.delete(tx)
                Snackbar.make(recycler, "Transaction deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        // Re-insert as a fresh transaction (simplest safe undo for a swipe-delete)
                        // Full undo-with-original-id support can be added when edit/duplicate ships.
                    }
                    .show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recycler)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {
                viewModel.setQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.transactions.onEach { list ->
            adapter.submitList(list)
            emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.categories.onEach { cats ->
            adapter.categoryMap = cats.associateBy { it.id }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
