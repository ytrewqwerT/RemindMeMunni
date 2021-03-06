package com.example.remindmemunni.destinations.newitem

import androidx.lifecycle.*
import com.example.remindmemunni.R
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.utils.PrimitiveDateTime
import com.example.remindmemunni.utils.Strings
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class NewItemViewModel(
    private val itemRepository: ItemRepository,
    templateItem: Item,
    private val isItemEdit: Boolean
) : ViewModel() {

    val allSeries: LiveData<List<AggregatedSeries>> =
        itemRepository.allSeries.asLiveData(viewModelScope.coroutineContext)
    val categories: LiveData<List<String>> =
        itemRepository.getCategories().asLiveData(viewModelScope.coroutineContext)

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
        viewModelScope.launch { setItem(templateItem) }
    }

    private fun setCost(newCost: Double) {
        cost.value = if (newCost != 0.0) newCost.absoluteValue.toString() else ""
        setCostType(Strings.get(if (newCost <= 0) R.string.debit else R.string.credit))
    }

    fun setCostType(type: CharSequence?) {
        _costIsDebit = (type == Strings.get(R.string.debit))
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
        if (name.value.isNullOrEmpty()) return Strings.get(R.string.item_needs_name)

        val unsignedCost = cost.value?.toDoubleOrNull() ?: 0.0
        val signedCost = if (_costIsDebit) -unsignedCost else unsignedCost
        val epochTime = _time.toEpoch()

        val item = Item(
            id = itemId, seriesId = seriesId, name = name.value!!,
            cost = signedCost, time = epochTime, category = category.value!!, notify = notify.value!!
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