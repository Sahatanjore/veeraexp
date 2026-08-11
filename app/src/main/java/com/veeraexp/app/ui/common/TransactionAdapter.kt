package com.veeraexp.app.ui.common

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.veeraexp.app.R
import com.veeraexp.app.data.entity.Category
import com.veeraexp.app.data.entity.Transaction
import com.veeraexp.app.data.entity.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val onClick: (Transaction) -> Unit = {},
    private val onDelete: (Transaction) -> Unit = {}
) : ListAdapter<Transaction, TransactionAdapter.VH>(DIFF) {

    // categoryId -> Category, kept in sync by the fragment so we can show names/colors
    var categoryMap: Map<Long, Category> = emptyMap()
        set(value) { field = value; notifyDataSetChanged() }

    private val dateFmt = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    inner class VH(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val colorDot: android.view.View = view.findViewById(R.id.colorDot)
        val category: TextView = view.findViewById(R.id.textCategory)
        val noteDate: TextView = view.findViewById(R.id.textNoteDate)
        val amount: TextView = view.findViewById(R.id.textAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val tx = getItem(position)
        val cat = tx.categoryId?.let { categoryMap[it] }

        holder.category.text = cat?.name ?: "Other"
        holder.noteDate.text = if (tx.note.isNotBlank()) "${tx.note} · ${dateFmt.format(Date(tx.date))}"
        else dateFmt.format(Date(tx.date))

        val isIncome = tx.type == TransactionType.INCOME
        val sign = if (isIncome) "+" else if (tx.type == TransactionType.EXPENSE) "-" else if (tx.amount >= 0) "→" else "←"
        holder.amount.text = "$sign₹%,.2f".format(kotlin.math.abs(tx.amount))
        holder.amount.setTextColor(
            if (isIncome) Color.parseColor("#2E7D32")
            else if (tx.type == TransactionType.EXPENSE) Color.parseColor("#C62828")
            else Color.parseColor("#1565C0")
        )

        try {
            holder.colorDot.setBackgroundColor(Color.parseColor(cat?.colorHex ?: "#9E9E9E"))
        } catch (_: Exception) {
            holder.colorDot.setBackgroundColor(Color.GRAY)
        }

        holder.itemView.setOnClickListener { onClick(tx) }
    }

    fun getTransactionAt(position: Int): Transaction = getItem(position)
    fun deleteAt(position: Int) = onDelete(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(old: Transaction, new: Transaction) = old.id == new.id
            override fun areContentsTheSame(old: Transaction, new: Transaction) = old == new
        }
    }
}
