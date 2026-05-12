package com.sante.priceindex.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sante.priceindex.databinding.ItemSlateBinding
import com.sante.priceindex.model.PriceCalcResult

class SlateAdapter(
    private val onRemove: (PriceCalcResult) -> Unit
) : ListAdapter<PriceCalcResult, SlateAdapter.SVH>(SDiff()) {

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        SVH(ItemSlateBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: SVH, pos: Int) = h.bind(getItem(pos))

    inner class SVH(private val b: ItemSlateBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(r: PriceCalcResult) {
            b.tvSlateEmoji.text     = r.emoji
            b.tvSlateCommodity.text = r.commodity
            b.tvSlatePrice.text     = "₹${r.recommendedPrice}/${r.unit}"
            b.btnRemoveSlate.setOnClickListener { onRemove(r) }
        }
    }

    class SDiff : DiffUtil.ItemCallback<PriceCalcResult>() {
        override fun areItemsTheSame(o: PriceCalcResult, n: PriceCalcResult) = o.commodity == n.commodity
        override fun areContentsTheSame(o: PriceCalcResult, n: PriceCalcResult) = o == n
    }
}
