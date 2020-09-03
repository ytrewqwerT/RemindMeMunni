package com.example.remindmemunni

import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.Series

sealed class Action {
    data class ItemView(val item: Item) : Action()
    data class ItemEdit(val item: Item) : Action()
    data class ItemFinish(val item: Item) : Action()
    data class ItemDelete(val item: Item) : Action()
    data class SerieView(val serie: Series) : Action()
    data class SerieEdit(val serie: Series) : Action()
    data class SerieDelete(val serie: AggregatedSeries) : Action()
}