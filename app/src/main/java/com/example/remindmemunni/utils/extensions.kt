package com.example.remindmemunni.utils

import com.example.remindmemunni.ActionViewModel
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.Series

fun Double?.toStringTrimmed(): String = when (this) {
    null -> "null"
    this.toLong().toDouble() -> this.toLong().toString()
    else -> this.toString()
}

fun ActionViewModel.createNewItem() = edit(Item())
fun ActionViewModel.createNewSerie() = edit(Series())