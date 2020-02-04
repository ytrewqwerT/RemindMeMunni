package com.example.remindmemunni.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.Series
import kotlinx.coroutines.launch

class NewSeriesViewModel(
    private val itemRepository: ItemRepository,
    private val seriesId: Int = 0
) : ViewModel() {

    private var isDebit: Boolean = false
    private var recurMonths: Int = 0
    private var recurDays: Int = 0

    val name = MutableLiveData<String>("")
    val cost = MutableLiveData<String>("")
    val costType = MutableLiveData<String>("")
    val nextNumInSeries = MutableLiveData<String>("")
    val numInSeriesPrefix = MutableLiveData<String>("")
    val recurrence = MutableLiveData<String>("")
    val autoCreateItems = MutableLiveData<Boolean>(true)

    init {
        setCostType("Debit")

        if (seriesId != 0) {
            viewModelScope.launch {
                val series = itemRepository.getDirectSerie(seriesId).series
                name.value = series.name
                if (series.cost < 0) {
                    cost.value = (-series.cost).toString()
                } else {
                    cost.value = series.cost.toString()
                    setCostType("Credit")
                }
                nextNumInSeries.value = series.curNum.toString()
                numInSeriesPrefix.value = series.numPrefix
                setRecurrence(series.recurMonths, series.recurDays)
            }
        }
    }

    fun setCostType(type: CharSequence?) {
        isDebit = type == "Debit"
        costType.value = type.toString()
    }

    fun setRecurrence(months: Int, days: Int) {
        recurMonths = months
        recurDays = days
        var recurText = "$recurMonths Month"
        if (recurMonths != 1) recurText += "s"
        recurText += ", $recurDays Day"
        if (recurDays != 1) recurText += "s"
        recurrence.value = recurText
    }

    fun createSeries(): String? {
        val name = name.value
        var absCost = cost.value?.toDoubleOrNull() ?: 0.0
        if (isDebit) absCost = -absCost
        val num = nextNumInSeries.value?.toDoubleOrNull() ?: 0.0
        val prefix = numInSeriesPrefix.value ?: ""
        val autoCreate = autoCreateItems.value ?: true

        if (name.isNullOrEmpty()) return "Series needs a name!"

        val series = Series(
            id = seriesId,
            name = name, cost = absCost,
            curNum = num, numPrefix = prefix,
            recurDays = recurDays, recurMonths = recurMonths,
            autoCreate = autoCreate
        )
        viewModelScope.launch { itemRepository.insert(series) }
        return null
    }

    class NewSeriesViewModelFactory(
        private val itemRepository: ItemRepository,
        private val seriesId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewSeriesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewSeriesViewModel(
                    itemRepository,
                    seriesId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}