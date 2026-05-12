package com.sante.priceindex.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sante.priceindex.db.PriceLogEntity
import com.sante.priceindex.db.SanteDb
import com.sante.priceindex.model.MandiPrice
import com.sante.priceindex.model.PriceCalcResult
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.*

class PriceRepository(context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val mandiRef  = firestore.collection("mandi_prices")
    private val dao       = SanteDb.getInstance(context).priceLogDao()

    // ── LiveData ──────────────────────────────────────────────────────────────
    fun getPriceLogs() = dao.getLast30()

    // ── Firebase: fetch today's Mandi prices ──────────────────────────────────
    suspend fun fetchMandiPrices(): List<MandiPrice> {
        Log.d("SanteBackend", "Fetching prices from Firestore...")
        return try {
            val snap = withTimeoutOrNull(30000) { mandiRef.get().await() }
            if (snap == null) {
                Log.w("SanteBackend", "Firestore fetch timed out (30s)")
                return simulatedPrices()
            }
            val list = snap.documents.mapNotNull { doc ->
                doc.toObject(MandiPrice::class.java)?.copy(id = doc.id)
            }.sortedBy { it.commodity }
            Log.d("SanteBackend", "Fetched ${list.size} items from Firestore")
            if (list.isEmpty()) simulatedPrices() else list
        } catch (e: Exception) {
            Log.e("SanteBackend", "Firestore fetch error: ${e.message}", e)
            simulatedPrices()
        }
    }

    // ── Firebase: seed sample Mandi data ─────────────────────────────────────
    suspend fun seedMandiPricesIfEmpty() {
        Log.d("SanteBackend", "Checking if seeding is needed...")
        try {
            val snap = withTimeoutOrNull(30000) { mandiRef.limit(1).get().await() }
            if (snap != null && !snap.isEmpty) {
                Log.d("SanteBackend", "Database not empty, skipping seed.")
                return
            }
            
            Log.d("SanteBackend", "Seeding database with initial data...")
            val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            val batch = firestore.batch()
            simulatedPrices().forEach { price ->
                // Clean the ID: Firestore document IDs cannot contain slashes if they are part of the path
                val safeId = price.commodity.replace("/", "_").replace(" ", "_")
                val doc = mandiRef.document(safeId)
                batch.set(doc, price.copy(date = today), SetOptions.merge())
            }
            batch.commit().await()
            Log.d("SanteBackend", "Seeding successful!")
        } catch (e: Exception) {
            Log.e("SanteBackend", "Seeding failed: ${e.message}", e)
        }
    }

    // ── Cost-Plus Pricing Algorithm ───────────────────────────────────────────
    fun calculate(
        price: MandiPrice,
        quantityKg: Double,
        totalTransportRs: Double,
        wastagePct: Double,
        desiredProfitPct: Double
    ): PriceCalcResult {
        val mandi              = price.mandiPriceRs
        val transportPerUnit   = if (quantityKg > 0) totalTransportRs / quantityKg else 0.0
        val wastageAdjustment  = mandi * (wastagePct / 100.0)
        val costPrice          = mandi + transportPerUnit + wastageAdjustment
        val recommendedPrice   = costPrice * (1.0 + desiredProfitPct / 100.0)
        val grossMargin        = recommendedPrice - costPrice
        val netProfitPct       = if (costPrice > 0) (grossMargin / costPrice) * 100.0 else 0.0
        val breakEven          = costPrice

        // Round to nearest ₹0.50
        val rrp = (Math.round(recommendedPrice * 2.0) / 2.0).toDouble()

        val summary = when {
            price.trend == "Rising"  -> "📈 Mandi prices rising — RRP is good now. Stock up!"
            price.trend == "Falling" -> "📉 Prices falling — sell fast before Mandi drops more."
            wastagePct > 20          -> "⚠️ High wastage — buy smaller batches to reduce loss."
            desiredProfitPct < 10    -> "💡 Profit margin below 10% — consider raising price slightly."
            else                     -> "✅ Fair price for your village customers."
        }

        return PriceCalcResult(
            commodity            = price.commodity,
            unit                 = price.unit,
            emoji                = price.emoji,
            mandiPrice           = mandi,
            transportCostPerUnit = transportPerUnit,
            wastagePercent       = wastagePct,
            desiredProfitPercent = desiredProfitPct,
            costPrice            = costPrice,
            recommendedPrice     = rrp,
            grossMarginRs        = grossMargin,
            netProfitPct         = netProfitPct,
            breakEvenPrice       = breakEven,
            isOverpriced         = rrp > mandi * 2.5,
            summary              = summary
        )
    }

    suspend fun saveLog(result: PriceCalcResult) {
        val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        dao.insert(PriceLogEntity(
            commodity        = result.commodity,
            mandiPrice       = result.mandiPrice,
            transportCost    = result.transportCostPerUnit,
            wastagePct       = result.wastagePercent,
            profitPct        = result.desiredProfitPercent,
            recommendedPrice = result.recommendedPrice,
            grossMargin      = result.grossMarginRs,
            date             = today
        ))
    }

    // ── Simulated Mandi prices (used when Firebase offline) ──────────────────
    fun simulatedPrices(): List<MandiPrice> {
        val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        return listOf(
            // Vegetables
            MandiPrice("onion",     "Onion / Eerulli",   "kg",  18.0, "Rising",  12.5, today, "Vegetable", "APMC Dharwad",  "🧅"),
            MandiPrice("tomato",    "Tomato / Tomato",   "kg",  24.0, "Falling", -8.0, today, "Vegetable", "APMC Dharwad",  "🍅"),
            MandiPrice("potato",    "Potato / Aaloo",    "kg",  22.0, "Stable",   1.0, today, "Vegetable", "APMC Hubli",    "🥔"),
            MandiPrice("cabbage",   "Cabbage / Mutte",   "kg",  15.0, "Stable",   0.0, today, "Vegetable", "APMC Hubli",    "🥬"),
            MandiPrice("cauliflower","Cauliflower",      "piece",25.0,"Rising",   5.0, today, "Vegetable", "APMC Dharwad",  "🥦"),
            MandiPrice("carrot",    "Carrot / Gajjari",  "kg",  45.0, "Falling", -3.0, today, "Vegetable", "APMC Gadag",    "🥕"),
            MandiPrice("ginger",    "Ginger / Shunti",   "kg",  120.0,"Rising",  15.0, today, "Spice", "APMC Hubli",    "🫚"),
            MandiPrice("garlic",    "Garlic / Bellulli", "kg",  180.0,"Stable",   2.0, today, "Spice", "APMC Dharwad",  "🧄"),
            MandiPrice("beans",     "Beans / Hurali",    "kg",  40.0, "Rising",   9.0, today, "Vegetable", "APMC Gadag",    "🫘"),
            MandiPrice("brinjal",   "Brinjal / Badanekai","kg", 20.0, "Falling", -4.0, today, "Vegetable", "APMC Hubli",   "🍆"),
            MandiPrice("drumstick", "Drumstick / Nugge", "kg",  55.0, "Stable",   1.5, today, "Vegetable", "APMC Dharwad",  "🌱"),

            // Fruits
            MandiPrice("banana",    "Banana / Bale",     "dz",  30.0, "Rising",   6.0, today, "Fruit", "APMC Dharwad",  "🍌"),
            MandiPrice("apple",     "Apple / Sebu",      "kg",  150.0,"Stable",   0.0, today, "Fruit", "APMC Hubli",    "🍎"),
            MandiPrice("mango",     "Mango / Mavina",    "kg",  80.0, "Rising",  20.0, today, "Fruit", "APMC Dharwad",  "🥭"),
            MandiPrice("lemon",     "Lemon / Nimbe",     "dz",  20.0, "Stable",   2.0, today, "Fruit", "APMC Dharwad",  "🍋"),
            MandiPrice("orange",    "Orange / Kittale",  "kg",  60.0, "Falling", -5.0, today, "Fruit", "APMC Hubli",    "🍊"),

            // Spices & Others
            MandiPrice("chilli",    "Green Chilli",      "kg",  60.0, "Falling", -5.0, today, "Vegetable", "APMC Gadag",    "🌶️"),
            MandiPrice("coriander", "Coriander / Kothamri","bunch", 8.0,"Stable", 0.0, today, "Vegetable", "APMC Hubli",   "🌿"),
            MandiPrice("turmeric",  "Turmeric / Arishina","kg", 140.0,"Rising",   4.0, today, "Spice", "APMC Gadag",    "🟡"),
            MandiPrice("toordal",   "Toor Dal / Bele",   "kg",  160.0,"Stable",   1.0, today, "Grain", "APMC Dharwad",  "🥣"),
            MandiPrice("sugar",     "Sugar / Sakkare",   "kg",  42.0, "Stable",   0.5, today, "Grain", "APMC Hubli",    "⚪"),
            MandiPrice("oil",       "Cooking Oil",       "ltr", 115.0,"Falling", -2.0, today, "Grain", "APMC Dharwad",  "🛢️")
        )
    }
}
