package com.example.remindmemunni.activitynewseries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.*
import kotlinx.coroutines.launch

class NewSeriesViewModel(app: Application) : AndroidViewModel(app) {

    private val itemRepository: ItemRepository

    private var mIsDebit: Boolean = false

    val mName = MutableLiveData<String>("")
    val mCost = MutableLiveData<String>("")
    val mCostType = MutableLiveData<String>("")
    val mNum = MutableLiveData<String>("")
    val mNumPrefix = MutableLiveData<String>("")

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
        setCostType("Debit")
    }

    fun setCostType(type: CharSequence?) {
        mIsDebit = type == "Debit"
        mCostType.value = type.toString()
    }

    fun createSeries(): String? {
        val name = mName.value
        var cost = mCost.value?.toDoubleOrNull() ?: 0.0
        if (mIsDebit) cost = -cost
        val num = mNum.value?.toDoubleOrNull() ?: 0.0
        val prefix = mNumPrefix.value ?: ""

        if (name.isNullOrEmpty()) return "Series needs a name!"

        val series = Series(name = name, cost = cost, curNum = num, numPrefix = prefix)
        viewModelScope.launch { itemRepository.insert(series) }
        return null
    }
}