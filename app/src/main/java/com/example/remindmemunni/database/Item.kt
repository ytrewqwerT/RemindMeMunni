package com.example.remindmemunni.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.remindmemunni.interfaces.ListItemViewable
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seriesId: Int = 0,
    val name: String = "",
    val cost: Double = 0.0,
    val time: Long = 0
) : ListItemViewable {

    override fun getListItemContents(): ListItemViewable.ListItemContents {
        val costString = if (cost < 0) "${-cost}" else "$cost cr"
        val offset = OffsetDateTime.now().offset
        val date = LocalDateTime.ofEpochSecond(time, 0, offset)
        val formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yy")
        return ListItemViewable.ListItemContents(
            name, date.format(formatter), "\$$costString"
        )
    }

    override fun toString(): String {
        val costString = if (cost < 0) "${-cost}" else "$cost cr"
        val offset = OffsetDateTime.now().offset
        val date = LocalDateTime.ofEpochSecond(time, 0, offset)
        return "$id $name: \$$costString, $date"
    }
}