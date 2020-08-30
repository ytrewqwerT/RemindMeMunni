package com.example.remindmemunni.utils

import android.content.Context
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.data.ItemRoomDatabase
import com.example.remindmemunni.itemslist.ItemsListViewModelFactory
import com.example.remindmemunni.main.MainViewModelFactory
import com.example.remindmemunni.newitem.NewItemViewModelFactory
import com.example.remindmemunni.newseries.NewSeriesViewModelFactory
import com.example.remindmemunni.notifications.NotificationScheduler
import com.example.remindmemunni.series.SeriesViewModelFactory
import com.example.remindmemunni.serieslist.SeriesListViewModelFactory

object InjectorUtils {

    private fun getItemRepository(context: Context): ItemRepository {
        val itemDao = ItemRoomDatabase.getDatabase(context.applicationContext).itemDao()
        val sharedPref = context.getSharedPreferences(
            "com.example.remindmemunni.GLOBAL_PREFERENCES",
            Context.MODE_PRIVATE
        )
        val notificationScheduler = NotificationScheduler(context.applicationContext)
        return ItemRepository(itemDao, sharedPref, notificationScheduler)
    }

    fun provideItemsListViewModelFactory(context: Context, seriesId: Int = 0) =
        ItemsListViewModelFactory(getItemRepository(context), seriesId)
    fun provideMainViewModelFactory(context: Context) =
        MainViewModelFactory(getItemRepository(context))
    fun provideNewItemViewModelFactory(context: Context, templateItem: Item, isItemEdit: Boolean) =
        NewItemViewModelFactory(getItemRepository(context), templateItem, isItemEdit)
    fun provideNewSeriesViewModelFactory(context: Context, seriesId: Int) =
        NewSeriesViewModelFactory(getItemRepository(context), seriesId)
    fun provideSeriesListViewModelFactory(context: Context) =
        SeriesListViewModelFactory(getItemRepository(context))
    fun provideSeriesViewModelFactory(context: Context, seriesId: Int) =
        SeriesViewModelFactory(getItemRepository(context), seriesId)
}