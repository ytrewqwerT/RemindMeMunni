package com.example.remindmemunni.newitem

import androidx.lifecycle.*
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.utils.PrimitiveDateTime
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class NewItemViewModel(
    private val itemRepository: ItemRepository,
    templateItem: Item,
    private val isItemEdit: Boolean
) : ViewModel() {

    val allSeries: LiveData<List<AggregatedSeries>> = itemRepository.allSeries

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

    private val itemId = templateItem.id
    private var _costIsDebit: Boolean = false
    private var _time = PrimitiveDateTime()
    private var seriesId: Int = 0

    val name = MutableLiveData("")
    val cost = MutableLiveData("")
    val costType = MutableLiveData("")
    val timeText = MutableLiveData("")
    val series = MutableLiveData("")
    val incSeriesNum = MutableLiveData(false)
    val category = MutableLiveData("")
    val notify = MutableLiveData(false)

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

        viewModelScope.launch { setItem(templateItem) }
        if (itemId != 0) {
            viewModelScope.launch {
                val item = itemRepository.getDirectItem(itemId)
                setItem(item)
                incSeriesNum.value = false
            }
        }
    }

    private fun setCost(newCost: Double) {
        when {
            newCost == 0.0 -> {
                cost.value = ""
                setCostType("Debit")
            }
            newCost < 0 -> {
                cost.value = (-newCost).toString()
                setCostType("Debit")
            }
            else -> {
                setCostType("Credit")
                cost.value = newCost.toString()
            }
        }
    }

    fun setCostType(type: CharSequence?) {
        _costIsDebit = type == "Debit"
        costType.value = type.toString()
    }

    private suspend fun setItem(item: Item) {
        name.value = item.name
        setCost(item.cost)
        setTime(PrimitiveDateTime.fromEpoch(item.time))
        category.value = item.category
        notify.value = item.notify
        if (item.seriesId != 0) {
            val serie = itemRepository.getDirectSerie(item.seriesId)
            seriesId = item.seriesId
            series.value = serie.series.name
            incSeriesNum.value = !isItemEdit && serie.series.isNumbered()
        }
    }

    fun setSeries(newSeries: AggregatedSeries?) {
        if (newSeries == null) {
            seriesId = 0
            series.value = ""
        } else {
            val newSerie = newSeries.series
            seriesId = newSerie.id
            series.value = newSerie.name

            val nextItem = newSeries.generateNextInSeries()
            viewModelScope.launch { setItem(nextItem) }
        }
    }

    fun setTime(newTime: PrimitiveDateTime) {
        _time = newTime
        val retrievedTime = _time.toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yy")
        timeText.value = retrievedTime?.format(formatter) ?: ""
    }
    fun clearTime() { setTime(PrimitiveDateTime()) }

    suspend fun createItem(): String? {
        if (name.value.isNullOrEmpty()) return "Item needs a name!"

        var cc = if (cost.value?.isNotEmpty() == true) cost.value!!.toDouble() else 0.0
        if (_costIsDebit) cc = -cc
        val epochTime = _time.toEpoch()

        val item = Item(
            id = itemId, seriesId = seriesId, name = name.value!!,
            cost = cc, time = epochTime, category = category.value!!, notify = notify.value!!
        )
        itemRepository.insert(item)

        if (incSeriesNum.value == true && seriesId != 0) {
            val serie = itemRepository.getDirectSerie(seriesId)
            serie.series.curNum += 1
            itemRepository.insert(serie.series)
        }

        return null
    }
}