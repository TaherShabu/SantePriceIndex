package com.sante.priceindex.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MandiPrice(
    val id: String = "",
    val commodity: String = "",        // "Onion", "Tomato", etc.
    val unit: String = "kg",
    val mandiPriceRs: Double = 0.0,    // Price at city Mandi (per unit)
    val trend: String = "Stable",      // "Rising" | "Falling" | "Stable"
    val trendPct: Double = 0.0,        // % change from yesterday
    val date: String = "",             // "dd MMM yyyy"
    val category: String = "Vegetable", // "Vegetable", "Fruit", "Spice", "Grain"
    val mandiName: String = "",        // "APMC Dharwad"
    val emoji: String = "🛒"
) : Parcelable
