package com.example.remindmemunni.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.remindmemunni.notifications.NotificationScheduler
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull

@Suppress("EXPERIMENTAL_API_USAGE")
class ItemRepository(
    private val itemDao: ItemDao,
    private val sharedPref: SharedPreferences,
    private val notificationScheduler: NotificationScheduler
) {

    val allItems: Flow<List<Item>> = itemDao.getItems()
    val allSeries: Flow<List<AggregatedSeries>> = itemDao.getSeries()

    // TODO: Either leave preference as Float and convert all other munnis to Float
    //  or hack a solution to store doubles in preferences.
    var latestMunni: Double = sharedPref.getFloat("MUNNI", 0F).toDouble()
        private set
    val munni: Flow<Double> = channelFlow {
        offer(latestMunni)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == "MUNNI") {
                latestMunni = sharedPref.getFloat("MUNNI", 0F).toDouble()
                offer(latestMunni)
            }
        }
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPref.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    var munniCalcEndMonth: Int = sharedPref.getInt("CALC_MONTH", 0)
        set(value) {
            field = value
            sharedPref.edit(true) {
                putInt("CALC_MONTH", value)
            }
        }

    init {
        sharedPref.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "CALC_MONTH") munniCalcEndMonth = sharedPref.getInt("CALC_MONTH", 0)
        }
    }

    fun setMunni(value: Double) {
        sharedPref.edit(true) {
            putFloat("MUNNI", value.toFloat())
        }
    }

    suspend fun insert(item: Item): Int {
        val itemId = itemDao.insert(item).toInt()
        if (item.notify) notificationScheduler.scheduleNotificationForItem(itemId, item)
        return itemId
    }
    suspend fun insert(series: Series): Int = itemDao.insert(series).toInt()

    fun getItem(itemId: Int): Flow<Item> = itemDao.getItem(itemId).filterNotNull()
    suspend fun getDirectItem(itemId: Int): Item = itemDao.getDirectItem(itemId) ?: Item()
    suspend fun completeItem(item: Item): Item? {
        var nextItem: Item? = null
        if (item.seriesId != 0) {
            val series = getDirectSerie(item.seriesId)
            if (item == series.items.last()) nextItem = series.generateNextInSeries()
        }
        delete(item)
        setMunni(latestMunni + item.cost)
        return nextItem
    }
    fun getItemsInSeries(seriesId: Int): Flow<List<Item>> = itemDao.getItemsInSeries(seriesId)

    fun getSerie(seriesId: Int): Flow<AggregatedSeries> = itemDao.getSerie(seriesId).filterNotNull()
    suspend fun getDirectSerie(seriesId: Int): AggregatedSeries = itemDao.getDirectSerie(seriesId) ?: AggregatedSeries(Series(), emptyList())
    suspend fun incrementSeries(seriesId: Int, increment: Double = 1.0) {
        if (seriesId == 0) return
        val series = getDirectSerie(seriesId)
        if (series.series.curNum == 0.0) return
        series.series.curNum += increment
        itemDao.insert(series.series)
    }

    fun getCategories(): Flow<List<String>> = itemDao.getCategories()

    suspend fun delete(item: Item) { itemDao.delete(item) }
    suspend fun delete(series: Series) { itemDao.delete(series) }
}