package com.veeraexp.app.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.veeraexp.app.R
import com.veeraexp.app.data.entity.InvestmentGoal

class GoalAdapter(
    private val onAddMoney: (InvestmentGoal) -> Unit
) : ListAdapter<InvestmentGoal, GoalAdapter.VH>(DIFF) {

    inner class VH(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textGoalName)
        val progressText: TextView = view.findViewById(R.id.textGoalProgress)
        val progressBar: ProgressBar = view.findViewById(R.id.progressGoal)
        val addMoneyBtn: android.view.View = view.findViewById(R.id.btnAddMoney)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val goal = getItem(position)
        holder.name.text = goal.name
        val pct = if (goal.targetAmount > 0) ((goal.savedAmount / goal.targetAmount) * 100).toInt().coerceIn(0, 100) else 0
        holder.progressText.text = "₹%,.2f of ₹%,.2f · %d%%".format(goal.savedAmount, goal.targetAmount, pct)
        holder.progressBar.progress = pct
        holder.addMoneyBtn.setOnClickListener { onAddMoney(goal) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<InvestmentGoal>() {
            override fun areItemsTheSame(old: InvestmentGoal, new: InvestmentGoal) = old.id == new.id
            override fun areContentsTheSame(old: InvestmentGoal, new: InvestmentGoal) = old == new
        }
    }
}
