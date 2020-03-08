package com.example.remindmemunni.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.remindmemunni.common.ListItemViewable
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Entity(tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seriesId: Int = 0,
    val name: String = "",
    val cost: Double = 0.0,
    val time: Long = 0,
    val category: String = "",
    val notify: Boolean = false
) : ListItemViewable {

    override fun getListItemContents(): ListItemViewable.ListItemContents {
        return ListItemViewable.ListItemContents(
            name, getDateString(), getCostString()
        )
    }

    override fun toString(): String {
        val offset = OffsetDateTime.now().offset
        val date = LocalDateTime.ofEpochSecond(time, 0, offset)
        return "$id $name: ${getCostString()}, $date"
    }

    fun getCostString(): String = when {
        cost < 0.0 -> "\$${-cost}"
        cost > 0.0 -> "\$${cost}cr"
        else -> ""
    }

    fun getDateString(): String = getDateString(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yy"))
    fun getDateString(formatter: DateTimeFormatter): String {
        if (time == 0L) return ""
        val offset = OffsetDateTime.now().offset
        val date = LocalDateTime.ofEpochSecond(time, 0, offset)
        return date.format(formatter)
    }

    fun hasFilterText(filter: String): Boolean {
        val lowerFilter = filter.toLowerCase(Locale.getDefault())
        return if (name.toLowerCase(Locale.getDefault()).contains(lowerFilter)) {
            true
        } else {
            category.toLowerCase(Locale.getDefault()).contains(lowerFilter)
        }
    }
}