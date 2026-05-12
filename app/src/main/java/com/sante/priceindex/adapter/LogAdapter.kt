package com.sante.priceindex.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sante.priceindex.databinding.ItemLogBinding
import com.sante.priceindex.db.PriceLogEntity
import java.text.SimpleDateFormat
import java.util.*

class LogAdapter : ListAdapter<PriceLogEntity, LogAdapter.LVH>(LDiff()) {

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        LVH(ItemLogBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: LVH, pos: Int) = h.bind(getItem(pos))

    inner class LVH(private val b: ItemLogBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(l: PriceLogEntity) {
            b.tvLogCommodity.text   = l.commodity
            b.tvLogDate.text        = l.date
            b.tvLogMandi.text       = "Mandi: ₹${l.mandiPrice}/unit"
            b.tvLogRRP.text         = "RRP: ₹${l.recommendedPrice}/unit"
            b.tvLogMargin.text      = "Margin: ₹${String.format("%.2f", l.grossMargin)}"
            b.tvLogTransport.text   = "Transport: ₹${l.transportCost}/unit · Wastage: ${l.wastagePct}%"
        }
    }

    class LDiff : DiffUtil.ItemCallback<PriceLogEntity>() {
        override fun areItemsTheSame(o: PriceLogEntity, n: PriceLogEntity) = o.id == n.id
        override fun areContentsTheSame(o: PriceLogEntity, n: PriceLogEntity) = o == n
    }
}
