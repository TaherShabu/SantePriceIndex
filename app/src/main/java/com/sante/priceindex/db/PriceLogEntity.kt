package com.sante.priceindex.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_logs")
data class PriceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val commodity: String,
    val mandiPrice: Double,
    val transportCost: Double,
    val wastagePct: Double,
    val profitPct: Double,
    val recommendedPrice: Double,
    val grossMargin: Double,
    val savedAt: Long = System.currentTimeMillis(),
    val date: String
)
