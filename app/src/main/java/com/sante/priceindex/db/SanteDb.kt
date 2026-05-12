package com.sante.priceindex.db

import android.content.Context
import androidx.room.*

@Database(entities = [PriceLogEntity::class], version = 1, exportSchema = false)
abstract class SanteDb : RoomDatabase() {
    abstract fun priceLogDao(): PriceLogDao

    companion object {
        @Volatile private var INSTANCE: SanteDb? = null
        fun getInstance(ctx: Context): SanteDb = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(ctx.applicationContext, SanteDb::class.java, "sante_db")
                .build().also { INSTANCE = it }
        }
    }
}
