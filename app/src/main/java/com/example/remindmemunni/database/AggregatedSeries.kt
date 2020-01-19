package com.example.remindmemunni.database

import androidx.room.Embedded
import androidx.room.Relation
import com.example.remindmemunni.ListItemViewable

data class AggregatedSeries (
    @Embedded val series: Series,
    @Relation(
        parentColumn = "id",
        entityColumn = "seriesId"
    ) val items: List<Item>
) : ListItemViewable {

    override fun getListItemContents() = ListItemViewable.ListItemContents(
        series.name, "${items.size} items", "\$${series.cost} each"
    )

    override fun toString(): String = series.toString()
}