package com.veeraexp.app.ui.saha

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.veeraexp.app.ui.common.repositoryOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * SAHA insights — currently shows a real, data-derived summary (not
 * canned text). The full rule-based insight set (unusual spending,
 * month-over-month %, budget warnings) and the chat interface from
 * spec sections 10-11 are the next phase.
 */
class SahaFragment : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val text = TextView(requireContext()).apply {
            textSize = 15f
            setPadding(48, 48, 48, 48)
            text = "✨ SAHA is warming up..."
        }
        return ScrollView(requireContext()).apply { addView(text) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = repositoryOf(requireContext())
        val text = (view as ScrollView).getChildAt(0) as TextView

        repository.observeBalance().onEach { balance ->
            text.text = "✨ SAHA\n\nYour current balance is ₹%,.2f.\n\n".format(balance) +
                "Full spending analysis, budget warnings, and chat are coming in the next build phase."
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
