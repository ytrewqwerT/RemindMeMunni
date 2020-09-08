package com.example.remindmemunni.destinations.newseries

import androidx.lifecycle.*
import com.example.remindmemunni.R
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.data.Series
import com.example.remindmemunni.utils.Strings
import com.example.remindmemunni.utils.toStringTrimmed
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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
                cost.value = series.cost.absoluteValue.toStringTrimmed()
                if (series.cost > 0) setCostType(Strings.get(R.string.credit))
                nextNumInSeries.value = series.curNum.toString()
                numInSeriesPrefix.value = series.numPrefix
                category.value = series.category
                notify.value = series.notify
                setRecurrence(series.recurMonths, series.recurDays)
            }
        }
    }

    fun setCostType(type: CharSequence?) {
        isDebit = type == Strings.get(R.string.debit)
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
        if (name.isNullOrEmpty()) return Strings.get(R.string.series_needs_name)
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