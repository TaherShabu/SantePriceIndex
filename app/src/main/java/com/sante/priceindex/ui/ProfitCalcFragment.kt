package com.sante.priceindex.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sante.priceindex.databinding.FragmentProfitCalcBinding
import com.sante.priceindex.model.MandiPrice
import com.sante.priceindex.viewmodel.PriceViewModel

class ProfitCalcFragment : Fragment() {

    private var _b: FragmentProfitCalcBinding? = null
    private val b get() = _b!!
    private val vm: PriceViewModel by activityViewModels()

    private var selectedPrice: MandiPrice? = null
    private var allPrices: List<MandiPrice> = emptyList()

    companion object {
        fun newInstance(price: MandiPrice): ProfitCalcFragment {
            val f = ProfitCalcFragment()
            f.arguments = Bundle().apply { putParcelable("price", price) }
            return f
        }
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        selectedPrice = arguments?.getParcelable("price")
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentProfitCalcBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.mandiPrices.observe(viewLifecycleOwner) { prices ->
            allPrices = prices
            val names = prices.map { "${it.emoji} ${it.commodity}" }
            b.spinnerCommodity.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, names)
            selectedPrice?.let { sel ->
                val idx = prices.indexOfFirst { it.id == sel.id }
                if (idx >= 0) b.spinnerCommodity.setSelection(idx)
                prefillMandiPrice(sel)
            }
        }

        b.spinnerCommodity.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedPrice = allPrices.getOrNull(pos)
                selectedPrice?.let { prefillMandiPrice(it) }
            }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }

        // Live recalculate on any input change
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b2: Int, c: Int) { recalculate() }
            override fun afterTextChanged(s: Editable?) {}
        }
        b.etMandiPrice.addTextChangedListener(watcher)
        b.etQuantity.addTextChangedListener(watcher)
        b.etTransport.addTextChangedListener(watcher)
        b.sliderWastage.addOnChangeListener { _, _, _ -> recalculate() }
        b.sliderProfit.addOnChangeListener  { _, _, _ -> recalculate() }

        vm.calcResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            b.cardResult.visibility      = View.VISIBLE
            b.tvRRP.text                 = "₹${result.recommendedPrice}/${result.unit}"
            b.tvCostPrice.text           = "Cost Price: ₹${String.format("%.2f", result.costPrice)}/${result.unit}"
            b.tvBreakEven.text           = "Break-even: ₹${String.format("%.2f", result.breakEvenPrice)}/${result.unit}"
            b.tvMargin.text              = "Margin: ₹${String.format("%.2f", result.grossMarginRs)} (${String.format("%.1f", result.netProfitPct)}%)"
            b.tvTransportPerUnit.text    = "Transport/unit: ₹${String.format("%.2f", result.transportCostPerUnit)}"
            b.tvInsight.text             = result.summary
            if (result.isOverpriced) {
                b.tvOverpricedWarn.visibility = View.VISIBLE
            } else {
                b.tvOverpricedWarn.visibility = View.GONE
            }
        }

        b.btnAddToSlate.setOnClickListener {
            val res = vm.calcResult.value
            if (res != null) {
                vm.addToSlate(res)
                vm.saveToLog(res)
                Toast.makeText(requireContext(), "${res.commodity} added to board!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Calculate first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun prefillMandiPrice(price: MandiPrice) {
        b.etMandiPrice.setText(price.mandiPriceRs.toString())
        b.tvSelectedUnit.text = "per ${price.unit}"
    }

    private fun recalculate() {
        val price   = selectedPrice ?: return
        val mandi   = b.etMandiPrice.text.toString().toDoubleOrNull() ?: price.mandiPriceRs
        val qty     = b.etQuantity.text.toString().toDoubleOrNull()   ?: 1.0
        val transp  = b.etTransport.text.toString().toDoubleOrNull()  ?: 0.0
        val wastage = b.sliderWastage.value.toDouble()
        val profit  = b.sliderProfit.value.toDouble()

        b.tvWastageVal.text = "${wastage.toInt()}%"
        b.tvProfitVal.text  = "${profit.toInt()}%"

        val mandiWithCustom = price.copy(mandiPriceRs = mandi)
        vm.calculate(mandiWithCustom, qty, transp, wastage, profit)
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
