package com.example.remindmemunni.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.database.Item
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase
import com.example.remindmemunni.utils.PrimitiveDateTime
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class NewItemViewModel(
    app: Application,
    private val itemId: Int = 0
) : AndroidViewModel(app) {

    private val itemRepository: ItemRepository
    val allSeries: LiveData<List<AggregatedSeries>>

    private var _costIsDebit: Boolean = false
    private var _time = PrimitiveDateTime()
    private var seriesId: Int = 0

    val name = MutableLiveData<String>("")
    val cost = MutableLiveData<String>("")
    val costType = MutableLiveData<String>("")
    val timeText = MutableLiveData<String>("")
    val series = MutableLiveData<String>("")
    var incSeriesNum = MutableLiveData<Boolean>(false)

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
        allSeries = itemRepository.allSeries
        setCostType("Debit")

        if (itemId != 0) {
            viewModelScope.launch {
                val item = itemRepository.getDirectItem(itemId)
                setItem(item)
            }
        }
    }

    fun setCost(newCost: Double) {
        if (newCost < 0) {
            cost.value = (-newCost).toString()
            setCostType("Debit")
        } else {
            setCostType("Credit")
            cost.value = newCost.toString()
        }
    }

    fun setCostType(type: CharSequence?) {
        _costIsDebit = type == "Debit"
        costType.value = type.toString()
    }

    private fun setItem(item: Item) {
        name.value = item.name
        if (item.cost < 0) {
            cost.value = (-item.cost).toString()
        } else {
            cost.value = item.cost.toString()
            setCostType("Credit")
        }
        setTime(PrimitiveDateTime.fromEpoch(item.time))
        if (item.seriesId != 0) {
            viewModelScope.launch {
                val serie = itemRepository.getDirectSerie(item.seriesId)
                seriesId = item.seriesId
                series.value = serie.series.name
            }
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
            if (nextItem != null) setItem(nextItem)

        }
    }

    fun setSeries(newSeries: Int) {
        viewModelScope.launch { setSeries(itemRepository.getDirectSerie(newSeries)) }
    }

    fun setTime(newTime: PrimitiveDateTime) {
        Log.d("Nice", "$newTime")
        _time = newTime
        val retrievedTime = _time.toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yy")
        timeText.value = retrievedTime?.format(formatter) ?: ""
    }
    fun clearTime() { setTime(PrimitiveDateTime()) }

    fun createItem(): String? {
        if (name.value.isNullOrEmpty()) return "Item needs a name!"

        var cc = if (cost.value?.isNotEmpty() == true) cost.value!!.toDouble() else 0.0
        if (_costIsDebit) cc = -cc
        val epochTime = _time.toEpoch()

        val item = Item(id = itemId, name = name.value!!, seriesId = seriesId, cost = cc, time = epochTime)
        viewModelScope.launch { itemRepository.insert(item) }

        if (incSeriesNum.value == true && seriesId != 0) {
            viewModelScope.launch {
                val serie = itemRepository.getDirectSerie(seriesId)
                serie.series.curNum += 1
                itemRepository.insert(serie.series)
            }
        }

        return null
    }

    class NewItemViewModelFactory(
        private val application: Application,
        private val itemId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewItemViewModel(
                    application,
                    itemId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}