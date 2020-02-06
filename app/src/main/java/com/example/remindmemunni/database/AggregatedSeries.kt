package com.example.remindmemunni.database

import androidx.room.Embedded
import androidx.room.Relation
import com.example.remindmemunni.interfaces.ListItemViewable
import com.example.remindmemunni.utils.PrimitiveDateTime
import java.time.LocalDateTime

data class AggregatedSeries (
    @Embedded val series: Series,
    @Relation(
        parentColumn = "id",
        entityColumn = "seriesId"
    ) val items: List<Item>
) : ListItemViewable {

    override fun getListItemContents(): ListItemViewable.ListItemContents {
        val costString = series.getCostString()
        val itemCostString = if (costString.isNotEmpty()) "$costString per item" else ""
        return ListItemViewable.ListItemContents(
            series.name, "${items.size} items", itemCostString
        )
    }

    override fun toString(): String = series.toString()

    // Generates the next item in the series (curNum is NOT changed).
    fun generateNextInSeries(): Item? {
        if (series.recurMonths == 0 && series.recurDays == 0) return null

        val lastItem = items.lastOrNull()
        var newTime = 0L
        if (lastItem != null) {
            var lastTime = PrimitiveDateTime.fromEpoch(lastItem.time).toLocalDateTime()
            newTime = if (lastTime != null) {
                lastTime = lastTime.plusMonths(series.recurMonths.toLong())
                lastTime = lastTime.plusDays(series.recurDays.toLong())
                PrimitiveDateTime.fromLocalDateTime(lastTime).toEpoch()
            } else {
                0L
            }
        }
        val name = "${series.name} ${series.numPrefix}${series.curNum}"

        return Item(name = name, seriesId = series.id, cost = series.cost, time = newTime)
    }

    fun getHiddenCost(until: LocalDateTime): Double {
        if (series.recurMonths == 0 && series.recurDays == 0) return 0.0

        var hiddenCost = 0.0

        val lastItemTime = if (items.isNotEmpty()) {
            PrimitiveDateTime.fromEpoch(items.last().time).toLocalDateTime() ?: LocalDateTime.now()
        } else {
            LocalDateTime.now()
        }

        var currentHiddenTime = series.addRecurrenceToTime(lastItemTime)
        while (currentHiddenTime < until) {
            hiddenCost += series.cost
            currentHiddenTime = series.addRecurrenceToTime(currentHiddenTime)
        }

        return hiddenCost
    }
}