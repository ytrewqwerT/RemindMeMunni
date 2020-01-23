package com.example.remindmemunni.activitynewseries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.*
import kotlinx.coroutines.launch

class NewSeriesViewModel(app: Application) : AndroidViewModel(app) {

    private val mRepository: ItemRepository

    private var mIsDebit: Boolean = false
    private var mMonths: Int = 0
    private var mDays: Int = 0

    val mName = MutableLiveData<String>("")
    val mCost = MutableLiveData<String>("")
    val mCostType = MutableLiveData<String>("")
    val mNum = MutableLiveData<String>("")
    val mNumPrefix = MutableLiveData<String>("")
    val mRecurrence = MutableLiveData<String>("")

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        mRepository = ItemRepository(itemDao)
        setCostType("Debit")
    }

    fun setCostType(type: CharSequence?) {
        mIsDebit = type == "Debit"
        mCostType.value = type.toString()
    }

    fun setRecurrence(months: Int, days: Int) {
        mMonths = months
        mDays = days
        var recurText = "$mMonths Month"
        if (mMonths != 1) recurText += "s"
        recurText += ", $mDays Day"
        if (mDays != 1) recurText += "s"
        mRecurrence.value = recurText
    }

    fun createSeries(): String? {
        val name = mName.value
        var cost = mCost.value?.toDoubleOrNull() ?: 0.0
        if (mIsDebit) cost = -cost
        val num = mNum.value?.toDoubleOrNull() ?: 0.0
        val prefix = mNumPrefix.value ?: ""

        if (name.isNullOrEmpty()) return "Series needs a name!"

        val series = Series(
            name = name, cost = cost,
            curNum = num, numPrefix = prefix,
            recurDays = mDays, recurMonths = mMonths
        )
        viewModelScope.launch { mRepository.insert(series) }
        return null
    }
}