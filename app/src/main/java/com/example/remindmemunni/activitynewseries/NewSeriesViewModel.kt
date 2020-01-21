package com.example.remindmemunni.activitynewseries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.*
import kotlinx.coroutines.launch

class NewSeriesViewModel(app: Application) : AndroidViewModel(app) {

    private val itemRepository: ItemRepository

    private var _costIsDebit: Boolean = false

    val name = MutableLiveData<String>("")
    val cost = MutableLiveData<String>("")
    val costType = MutableLiveData<String>("")

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
        setCostType("Debit")
    }

    fun setCostType(type: CharSequence?) {
        _costIsDebit = type == "Debit"
        costType.value = type.toString()
    }

    fun createSeries(): String? {
        if (name.value.isNullOrEmpty()) return "Series needs a name!"

        var cc = if (cost.value?.isNotEmpty() == true) cost.value!!.toDouble() else 0.0
        if (_costIsDebit) cc = -cc

        val series = Series(name = name.value!!, cost = cc)
        viewModelScope.launch { itemRepository.insert(series) }
        return null
    }
}