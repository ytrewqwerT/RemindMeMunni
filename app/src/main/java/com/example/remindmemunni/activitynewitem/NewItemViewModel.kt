package com.example.remindmemunni.activitynewitem

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.PrimitiveDateTime
import com.example.remindmemunni.database.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NewItemViewModel(app: Application) : AndroidViewModel(app) {

    private val itemRepository: ItemRepository
    val allSeries: LiveData<List<AggregatedSeries>>

    private var _costIsDebit: Boolean = false
    private var _time = PrimitiveDateTime()
    private var seriesId: Int = 0

    val name = MutableLiveData<String>("")
    val cost = MutableLiveData<String>("")
    val costType = MutableLiveData<String>("")
    val series = MutableLiveData<String>("")

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
        allSeries = itemRepository.allSeries
        setCostType("Debit")
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
        _time = newTime
        val retrievedTime = _time.toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yy")
        return retrievedTime?.format(formatter)
    }

    fun createItem(): String? {
        if (name.value.isNullOrEmpty()) return "Item needs a name!"

        var cc = if (cost.value?.isNotEmpty() == true) cost.value!!.toDouble() else 0.0
        if (_costIsDebit) cc = -cc
        val localDateTime: LocalDateTime? = _time.toLocalDateTime()
        val epochTime = localDateTime?.atZone(ZoneId.systemDefault())?.toEpochSecond() ?: 0

        val item = Item(name = name.value!!, seriesId = seriesId, cost = cc, time = epochTime)
        viewModelScope.launch { itemRepository.insert(item) }
        return null
    }
}