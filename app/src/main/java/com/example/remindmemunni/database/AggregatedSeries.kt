package com.example.remindmemunni.database

import androidx.room.Embedded
import androidx.room.Relation
import com.example.remindmemunni.ListItemViewable
import com.example.remindmemunni.PrimitiveDateTime
import java.time.LocalDateTime
import kotlin.math.floor

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

    // Generates the next item if there is only one left and a series recurrence is set
    fun completeLastItem(): Item? {
        if (items.size != 1) return null
        if (series.recurDays == 0 && series.recurMonths == 0) return null

        var oldTime: LocalDateTime = PrimitiveDateTime.fromEpoch(items[0].time).toLocalDateTime()
            ?: return null
        oldTime = oldTime.plusMonths(series.recurMonths.toLong())
        oldTime = oldTime.plusDays(series.recurDays.toLong())
        val newTime = PrimitiveDateTime.fromLocalDateTime(oldTime).toEpoch()

        var name = "${series.name} ${series.numPrefix}${series.curNum}"
        series.curNum = floor(series.curNum) + 1

        return Item(name = name, seriesId = series.id, cost = series.cost, time = newTime)
    }
}