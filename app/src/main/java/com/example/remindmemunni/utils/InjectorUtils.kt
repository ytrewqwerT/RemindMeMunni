package com.example.remindmemunni.utils

import android.content.Context
import com.example.remindmemunni.MainViewModelFactory
import com.example.remindmemunni.common.ActionViewModelFactory
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.data.ItemRoomDatabase
import com.example.remindmemunni.destinations.item.ItemViewModelFactory
import com.example.remindmemunni.destinations.newitem.NewItemViewModelFactory
import com.example.remindmemunni.destinations.newseries.NewSeriesViewModelFactory
import com.example.remindmemunni.destinations.series.SeriesViewModelFactory
import com.example.remindmemunni.itemslist.ItemsListViewModelFactory
import com.example.remindmemunni.notifications.NotificationScheduler
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

    fun provideActionViewModelFactory(context: Context) =
        ActionViewModelFactory(getItemRepository(context))
    fun provideItemViewModelFactory(context: Context, itemId: Int) =
        ItemViewModelFactory(getItemRepository(context), itemId)
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