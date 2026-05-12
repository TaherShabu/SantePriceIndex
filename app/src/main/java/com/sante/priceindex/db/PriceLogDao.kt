package com.sante.priceindex.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PriceLogDao {
    @Query("SELECT * FROM price_logs ORDER BY savedAt DESC")
    fun getAll(): LiveData<List<PriceLogEntity>>

    @Query("SELECT * FROM price_logs ORDER BY savedAt DESC LIMIT 30")
    fun getLast30(): LiveData<List<PriceLogEntity>>

    @Insert
    suspend fun insert(log: PriceLogEntity)

    @Query("SELECT AVG(recommendedPrice) FROM price_logs WHERE commodity = :commodity")
    suspend fun avgPrice(commodity: String): Double?

    @Query("SELECT COUNT(*) FROM price_logs")
    suspend fun count(): Int
}
