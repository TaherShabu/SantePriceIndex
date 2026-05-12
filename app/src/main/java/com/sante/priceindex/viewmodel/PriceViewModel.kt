package com.sante.priceindex.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sante.priceindex.model.MandiPrice
import com.sante.priceindex.model.PriceCalcResult
import com.sante.priceindex.repository.PriceRepository
import kotlinx.coroutines.launch

class PriceViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PriceRepository(app)

    val priceLogs = repo.getPriceLogs()

    private val _mandiPrices = MutableLiveData<List<MandiPrice>>()
    val mandiPrices: LiveData<List<MandiPrice>> = _mandiPrices

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _calcResult = MutableLiveData<PriceCalcResult?>()
    val calcResult: LiveData<PriceCalcResult?> = _calcResult

    // Prices shown on the Digital Slate
    private val _slateItems = MutableLiveData<List<PriceCalcResult>>(emptyList())
    val slateItems: LiveData<List<PriceCalcResult>> = _slateItems

    init {
        loadPrices()
        seedInitialSlate()
    }

    private fun seedInitialSlate() {
        val today = repo.simulatedPrices()
        val onion = today.find { it.id == "onion" } ?: today[0]
        val tomato = today.find { it.id == "tomato" } ?: today[1]
        
        // Add some default items so board isn't empty on first run
        calculate(onion, 100.0, 200.0, 10.0, 25.0)
        calcResult.value?.let { addToSlate(it) }
        
        calculate(tomato, 50.0, 100.0, 15.0, 30.0)
        calcResult.value?.let { addToSlate(it) }
        
        clearCalcResult()
    }

    fun loadPrices() {
        viewModelScope.launch {
            _loading.value = true
            // Show simulated prices first for immediate feedback
            _mandiPrices.value = repo.simulatedPrices()
            
            // Try to sync with Firebase in background
            repo.seedMandiPricesIfEmpty()
            _mandiPrices.value = repo.fetchMandiPrices()
            _loading.value = false
        }
    }

    fun calculate(
        price: MandiPrice,
        quantityKg: Double,
        totalTransportRs: Double,
        wastagePct: Double,
        profitPct: Double
    ) {
        val result = repo.calculate(price, quantityKg, totalTransportRs, wastagePct, profitPct)
        _calcResult.value = result
    }

    fun saveToLog(result: PriceCalcResult) {
        viewModelScope.launch { repo.saveLog(result) }
    }

    fun addToSlate(result: PriceCalcResult) {
        val current = _slateItems.value?.toMutableList() ?: mutableListOf()
        current.removeAll { it.commodity == result.commodity }
        current.add(result)
        _slateItems.value = current
    }

    fun removeFromSlate(commodity: String) {
        _slateItems.value = _slateItems.value?.filter { it.commodity != commodity }
    }

    fun clearCalcResult() { _calcResult.value = null }

    fun getSimulatedPrices() = repo.simulatedPrices()
}
