package com.example.remindmemunni.utils

import android.content.Context
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase
import com.example.remindmemunni.viewmodels.MainViewModel

object InjectorUtils {

    private fun getItemRepository(context: Context) =
        ItemRepository(ItemRoomDatabase.getDatabase(context.applicationContext).itemDao())

    fun provideMainViewModelFactory(context: Context) =
        MainViewModel.MainViewModelFactory(getItemRepository(context))
}