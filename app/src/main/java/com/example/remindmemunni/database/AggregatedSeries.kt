package com.example.remindmemunni.database

import androidx.lifecycle.LiveData
import androidx.room.Embedded
import androidx.room.Relation

data class AggregatedSeries (
    @Embedded val series: Series,
    @Relation(
        parentColumn = "id",
        entityColumn = "seriesId"
    )
    val items: List<Item>
) {

    override fun toString(): String {
        return series.toString()
    }

}