package com.example.remindmemunni.utils

import android.content.Context
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase
import com.example.remindmemunni.viewmodels.*

object InjectorUtils {

    private fun getItemRepository(context: Context) =
        ItemRepository(ItemRoomDatabase.getDatabase(context.applicationContext).itemDao())

    fun provideItemsListViewModelFactory(context: Context, seriesId: Int = 0) =
        ItemsListViewModel.ItemsListViewModelFactory(getItemRepository(context), seriesId)
    fun provideMainViewModelFactory(context: Context) =
        MainViewModel.MainViewModelFactory(getItemRepository(context))
    fun provideNewItemViewModelFactory(context: Context, itemId: Int) =
        NewItemViewModel.NewItemViewModelFactory(getItemRepository(context), itemId)
    fun provideNewSeriesViewModelFactory(context: Context, seriesId: Int) =
        NewSeriesViewModel.NewSeriesViewModelFactory(getItemRepository(context), seriesId)
    fun provideSeriesListViewModelFactory(context: Context) =
        SeriesListViewModel.SeriesListViewModelFactory(getItemRepository(context))
    fun provideSeriesViewModelFactory(context: Context, seriesId: Int) =
        SeriesViewModel.SeriesViewModelFactory(getItemRepository(context), seriesId)
}