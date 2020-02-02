package com.example.remindmemunni.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase
import com.example.remindmemunni.database.Series
import kotlinx.coroutines.launch

class NewSeriesViewModel(
    app: Application,
    private val seriesId: Int = 0
) : AndroidViewModel(app) {

    private val itemRepository: ItemRepository

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
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
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

        if (name.isNullOrEmpty()) return "Series needs a name!"

        val series = Series(
            id = seriesId,
            name = name, cost = absCost,
            curNum = num, numPrefix = prefix,
            recurDays = recurDays, recurMonths = recurMonths
        )
        viewModelScope.launch { itemRepository.insert(series) }
        return null
    }

    class NewSeriesViewModelFactory(
        private val application: Application,
        private val seriesId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewSeriesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewSeriesViewModel(
                    application,
                    seriesId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}