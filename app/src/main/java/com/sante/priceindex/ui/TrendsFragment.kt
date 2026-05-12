package com.sante.priceindex.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.core.content.ContextCompat
import com.sante.priceindex.R
import com.sante.priceindex.adapter.LogAdapter
import com.sante.priceindex.databinding.FragmentTrendsBinding
import com.sante.priceindex.viewmodel.PriceViewModel

class TrendsFragment : Fragment() {

    private var _b: FragmentTrendsBinding? = null
    private val b get() = _b!!
    private val vm: PriceViewModel by activityViewModels()
    private val logAdapter = LogAdapter()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentTrendsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.rvLogs.adapter = logAdapter

        vm.priceLogs.observe(viewLifecycleOwner) { logs ->
            logAdapter.submitList(logs)
            b.tvLogCount.text = "${logs.size} saved calculations"
            b.tvEmptyLogs.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
        }

        // Trend summary cards from Mandi prices
        vm.mandiPrices.observe(viewLifecycleOwner) { prices ->
            val rising  = prices.count { it.trend == "Rising" }
            val falling = prices.count { it.trend == "Falling" }
            val stable  = prices.count { it.trend == "Stable" }
            b.tvRisingCount.text  = "$rising Rising ▲"
            b.tvFallingCount.text = "$falling Falling ▼"
            b.tvStableCount.text  = "$stable Stable ●"

            b.tvRisingCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRising))
            b.tvFallingCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorFalling))
            b.tvStableCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorStable))

            val topRising = prices.filter { it.trend == "Rising" }
                .sortedByDescending { it.trendPct }.take(3)
                .joinToString("\n") { "${it.emoji} ${it.commodity}: ₹${it.mandiPriceRs} (+${it.trendPct}%)" }
            b.tvTopRising.text = if (topRising.isNotEmpty()) topRising else "No rising commodities today."
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
