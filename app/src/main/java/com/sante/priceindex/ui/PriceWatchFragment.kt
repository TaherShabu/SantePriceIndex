package com.sante.priceindex.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sante.priceindex.adapter.MandiAdapter
import com.sante.priceindex.databinding.FragmentPriceWatchBinding
import com.sante.priceindex.viewmodel.PriceViewModel

class PriceWatchFragment : Fragment() {

    private var _b: FragmentPriceWatchBinding? = null
    private val b get() = _b!!
    private val vm: PriceViewModel by activityViewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentPriceWatchBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MandiAdapter { price ->
            // Navigate to calc with pre-filled price
            val frag = ProfitCalcFragment.newInstance(price)
            parentFragmentManager.beginTransaction()
                .replace(com.sante.priceindex.R.id.fragmentContainer, frag)
                .addToBackStack(null).commit()
            (requireActivity() as com.sante.priceindex.MainActivity)
                .binding.bottomNav.selectedItemId = com.sante.priceindex.R.id.nav_calc
        }
        b.rvMandiPrices.adapter = adapter

        vm.mandiPrices.observe(viewLifecycleOwner) { prices ->
            applyFilter(prices)
        }

        b.chipGroupCategory.setOnCheckedStateChangeListener { _, _ ->
            vm.mandiPrices.value?.let { applyFilter(it) }
        }

        vm.loading.observe(viewLifecycleOwner) { loading ->
            b.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        b.btnRefresh.setOnClickListener { vm.loadPrices() }
    }

    private fun applyFilter(prices: List<com.sante.priceindex.model.MandiPrice>) {
        val filtered = when (b.chipGroupCategory.checkedChipId) {
            com.sante.priceindex.R.id.chipVeg   -> prices.filter { it.category == "Vegetable" }
            com.sante.priceindex.R.id.chipFruit -> prices.filter { it.category == "Fruit" }
            com.sante.priceindex.R.id.chipSpice -> prices.filter { it.category == "Spice" }
            com.sante.priceindex.R.id.chipGrain -> prices.filter { it.category == "Grain" }
            else -> prices
        }
        (b.rvMandiPrices.adapter as MandiAdapter).submitList(filtered)
        b.tvPriceCount.text = "${filtered.size} commodities — APMC today"
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
