package com.sante.priceindex.model

data class PriceCalcResult(
    val commodity: String,
    val unit: String,
    val emoji: String,
    val mandiPrice: Double,        // What vendor paid at Mandi
    val transportCostPerUnit: Double,
    val wastagePercent: Double,    // % of stock lost / unsold
    val desiredProfitPercent: Double,
    val costPrice: Double,         // mandiPrice + transport + wastage adjustment
    val recommendedPrice: Double,  // costPrice × (1 + profit%)
    val grossMarginRs: Double,     // recommendedPrice - costPrice
    val netProfitPct: Double,      // actual margin %
    val breakEvenPrice: Double,    // costPrice (no profit)
    val isOverpriced: Boolean,     // vs market average
    val summary: String            // one-line insight
)
