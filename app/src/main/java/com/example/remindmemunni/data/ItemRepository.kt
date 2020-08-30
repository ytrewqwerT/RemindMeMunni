package com.example.remindmemunni.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.remindmemunni.notifications.NotificationScheduler

class ItemRepository(
    private val itemDao: ItemDao,
    private val sharedPref: SharedPreferences,
    private val notificationScheduler: NotificationScheduler
) {

    val allItems: LiveData<List<Item>> = itemDao.getItems()
    val allSeries: LiveData<List<AggregatedSeries>> = itemDao.getSeries()

    private val sharedPrefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            "MUNNI" -> munni.value = sharedPref.getFloat("MUNNI", 0F).toDouble()
            "CALC_MONTH" -> munniCalcEndMonth = sharedPref.getInt("CALC_MONTH", 0)
        }
    }

    // TODO: Either leave preference as Float and convert all other munnis to Float
    //  or hack a solution to store doubles in preferences.
    val munni = MutableLiveData(sharedPref.getFloat("MUNNI", 0F).toDouble())
    var munniCalcEndMonth: Int = sharedPref.getInt("CALC_MONTH", 0)
        set(value) {
            field = value
            with(sharedPref.edit()) {
                putInt("CALC_MONTH", value)
                commit()
            }
        }

    init {
        munni.observeForever {
            val newVal = it.toFloat()
            val curVal = sharedPref.getFloat("MUNNI", 0F)
            if (newVal != curVal) {
                with (sharedPref.edit()) {
                    putFloat("MUNNI", it.toFloat())
                    commit()
                }
            }
        }
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    suspend fun insert(item: Item): Int {
        val itemId = itemDao.insert(item).toInt()
        if (item.notify) notificationScheduler.scheduleNotificationForItem(itemId, item)
        return itemId
    }
    suspend fun insert(series: Series): Int = itemDao.insert(series).toInt()

    suspend fun getDirectItem(itemId: Int): Item = itemDao.getDirectItem(itemId)
    suspend fun completeItem(item: Item): Item? {
        var nextItem: Item? = null
        if (item.seriesId != 0) {
            val series = getDirectSerie(item.seriesId)
            if (item == series.items.last()) nextItem = series.generateNextInSeries()
        }
        delete(item)
        munni.value = (munni.value ?: 0.0) + item.cost
        return nextItem
    }
    fun getItemsInSeries(seriesId: Int): LiveData<List<Item>> = itemDao.getItemsInSeries(seriesId)

    fun getSerie(seriesId: Int): LiveData<AggregatedSeries> = itemDao.getSerie(seriesId)
    suspend fun getDirectSerie(seriesId: Int): AggregatedSeries = itemDao.getDirectSerie(seriesId)
    suspend fun incrementSeries(seriesId: Int, increment: Double = 1.0) {
        if (seriesId == 0) return
        val series = itemDao.getDirectSerie(seriesId)
        if (series.series.curNum == 0.0) return
        series.series.curNum += increment
        itemDao.insert(series.series)
    }

    suspend fun delete(item: Item) { itemDao.delete(item) }
    suspend fun delete(series: Series) { itemDao.delete(series) }
}