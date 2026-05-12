package com.sante.priceindex.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sante.priceindex.R
import com.sante.priceindex.databinding.ItemMandiPriceBinding
import com.sante.priceindex.model.MandiPrice

class MandiAdapter(
    private val onCalcClick: (MandiPrice) -> Unit
) : ListAdapter<MandiPrice, MandiAdapter.MVH>(MDiff()) {

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        MVH(ItemMandiPriceBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: MVH, pos: Int) = h.bind(getItem(pos))

    inner class MVH(private val b: ItemMandiPriceBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(m: MandiPrice) {
            b.tvEmoji.text       = m.emoji
            b.tvCommodity.text   = m.commodity
            b.tvMandiName.text   = m.mandiName
            b.tvMandiPrice.text  = "₹${m.mandiPriceRs}/${m.unit}"
            b.tvDate.text        = m.date

            val (trendIcon, trendColor) = when (m.trend) {
                "Rising"  -> Pair("▲ +${m.trendPct}%", R.color.colorRising)
                "Falling" -> Pair("▼ ${m.trendPct}%",  R.color.colorFalling)
                else      -> Pair("● Stable",           R.color.colorStable)
            }
            b.tvTrend.text = trendIcon
            b.tvTrend.setTextColor(ContextCompat.getColor(b.root.context, trendColor))

            b.btnCalc.setOnClickListener { onCalcClick(m) }
        }
    }

    class MDiff : DiffUtil.ItemCallback<MandiPrice>() {
        override fun areItemsTheSame(o: MandiPrice, n: MandiPrice) = o.id == n.id
        override fun areContentsTheSame(o: MandiPrice, n: MandiPrice) = o == n
    }
}
