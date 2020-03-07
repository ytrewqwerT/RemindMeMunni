package com.example.remindmemunni.viewmodels

import androidx.lifecycle.*
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.Series
import kotlinx.coroutines.launch

class NewSeriesViewModel(
    private val itemRepository: ItemRepository,
    private val seriesId: Int = 0
) : ViewModel() {

    private val itemsTransformer: LiveData<List<String>> =
        Transformations.map(itemRepository.allItems) { items ->
            items.map { item -> item.category }
        }
    private val seriesTransformer: LiveData<List<String>> =
        Transformations.map(itemRepository.allSeries) { series ->
            series.map { serie -> serie.series.category }
        }
    private val _categories = MediatorLiveData<Set<String>>()
    val categories: LiveData<Set<String>> get() = _categories

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
    val category = MutableLiveData<String>("")
    val notify = MutableLiveData<Boolean>(false)

    init {
        setCostType("Debit")

        _categories.addSource(itemsTransformer) {
            val other = seriesTransformer.value ?: emptyList()
            _categories.value = other.union(it)
        }
        _categories.addSource(seriesTransformer) {
            val other = itemsTransformer.value ?: emptyList()
            _categories.value = other.union(it)
        }

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