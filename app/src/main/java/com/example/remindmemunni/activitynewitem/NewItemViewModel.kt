package com.example.remindmemunni.activitynewitem

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.remindmemunni.PrimitiveDateTime
import com.example.remindmemunni.activityseries.SeriesViewModel
import com.example.remindmemunni.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
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

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
        allSeries = itemRepository.allSeries
        setCostType("Debit")

        if (itemId != 0) {
            viewModelScope.launch {
                val item = itemRepository.getDirectItem(itemId)
                name.value = item.name
                if (item.cost < 0) {
                    cost.value = (-item.cost).toString()
                } else {
                    cost.value = item.cost.toString()
                    setCostType("Credit")
                }
                setTime(PrimitiveDateTime.fromEpoch(item.time))
                if (item.seriesId != 0) {
                    val serie = itemRepository.getDirectSerie(item.seriesId)
                    seriesId = item.seriesId
                    series.value = serie.series.name
                }
            }
        }
    }

    fun setCostType(type: CharSequence?) {
        _costIsDebit = type == "Debit"
        costType.value = type.toString()
    }

    fun setSeries(newSeries: Series?) {
        seriesId = newSeries?.id ?: 0
        series.value = newSeries?.name ?: ""
    }

    fun setTime(newTime: PrimitiveDateTime): String? {
        Log.d("Nice", "${newTime.toEpoch()}")
        _time = newTime
        val retrievedTime = _time.toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yy")
        timeText.value = retrievedTime?.format(formatter) ?: ""
        return retrievedTime?.format(formatter)
    }

    fun clearTime() {
        _time = PrimitiveDateTime()
        timeText.value = ""
    }

    fun createItem(): String? {
        if (name.value.isNullOrEmpty()) return "Item needs a name!"

        var cc = if (cost.value?.isNotEmpty() == true) cost.value!!.toDouble() else 0.0
        if (_costIsDebit) cc = -cc
        val epochTime = _time.toEpoch()

        val item = Item(id = itemId, name = name.value!!, seriesId = seriesId, cost = cc, time = epochTime)
        viewModelScope.launch { itemRepository.insert(item) }
        return null
    }

    class NewItemViewModelFactory(
        private val application: Application,
        private val itemId: Int
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            NewItemViewModel(application, itemId) as T
    }
}