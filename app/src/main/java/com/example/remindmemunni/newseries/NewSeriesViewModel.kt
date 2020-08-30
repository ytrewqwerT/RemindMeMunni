package com.example.remindmemunni.newseries

import androidx.lifecycle.*
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.data.Series
import kotlinx.coroutines.launch

class NewSeriesViewModel(
    private val itemRepository: ItemRepository,
    private val seriesId: Int = 0
) : ViewModel() {

    val categories: LiveData<List<String>> =
        itemRepository.getCategories().asLiveData(viewModelScope.coroutineContext)

    private var isDebit: Boolean = false
    private var recurMonths: Int = 0
    private var recurDays: Int = 0

    val name = MutableLiveData("")
    val cost = MutableLiveData("")
    val costType = MutableLiveData("")
    val nextNumInSeries = MutableLiveData("")
    val numInSeriesPrefix = MutableLiveData("")
    val recurrence = MutableLiveData("")
    val autoCreateItems = MutableLiveData(true)
    val category = MutableLiveData("")
    val notify = MutableLiveData(false)

    init {
        setCostType("Debit")

        if (seriesId != 0) {
            viewModelScope.launch {
                val series = itemRepository.getDirectSerie(seriesId).series
                name.value = series.name
                cost.value = if (series.cost < 0) {
                    (-series.cost).toString()
                } else {
                    setCostType("Credit")
                    series.cost.toString()
                }
                nextNumInSeries.value = series.curNum.toString()
                numInSeriesPrefix.value = series.numPrefix
                category.value = series.category
                notify.value = series.notify
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

        val temp = Series(recurMonths = months, recurDays = days)
        recurrence.value = temp.getRecurrenceString()
    }

    fun validateInput(): String? {
        val name = name.value
        if (name.isNullOrEmpty()) return "Series needs a name!"
        return null
    }

    suspend fun createSeries(): Int {
        if (validateInput() != null) return 0

        val name = name.value ?: ""
        var absCost = cost.value?.toDoubleOrNull() ?: 0.0
        if (isDebit) absCost = -absCost
        val num = nextNumInSeries.value?.toDoubleOrNull() ?: 0.0
        val prefix = numInSeriesPrefix.value ?: ""
        val autoCreate = autoCreateItems.value ?: true
        val category = category.value ?: ""
        val notify = notify.value ?: false

        val series = Series(
            id = seriesId,
            name = name, cost = absCost,
            curNum = num, numPrefix = prefix,
            recurDays = recurDays, recurMonths = recurMonths,
            autoCreate = autoCreate, category = category, notify = notify
        )
        return itemRepository.insert(series)
    }
}