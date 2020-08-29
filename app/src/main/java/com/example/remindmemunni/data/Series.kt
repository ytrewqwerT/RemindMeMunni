package com.example.remindmemunni.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.remindmemunni.common.ListItemViewable
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "series_table")
data class Series (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val cost: Double = 0.0,
    var curNum: Double = 0.0, // TODO: Rename to nextNum
    val numPrefix: String = "",
    val recurMonths: Int = 0,
    val recurDays: Int = 0,
    val autoCreate: Boolean = true,
    val category: String = "",
    val notify: Boolean = false
) : ListItemViewable {

    override fun getListItemContents(): ListItemViewable.ListItemContents {
        return ListItemViewable.ListItemContents(
            name, "$numPrefix$curNum", getCostString()
        )
    }

    override fun toString(): String = name      // TODO: Not do this (?)

    fun addRecurrenceToTime(time: LocalDateTime): LocalDateTime {
        return time.plusMonths(recurMonths.toLong()).plusDays(recurDays.toLong())
    }

    fun getCostString(): String = when {
        cost < 0.0 -> "\$${-cost}"
        cost > 0.0 -> "\$${cost}cr"
        else -> ""
    }

    fun getRecurrenceString(): String {
        var result = ""
        if (recurMonths > 0) {
            result += if (recurMonths == 1) "$recurMonths Month" else "$recurMonths Months"
        }
        if (recurDays > 0) {
            if (result.isNotEmpty()) result += " and "
            result += if (recurDays == 1) "$recurDays Day" else "$recurDays Days"
        }
        return result
    }

    fun isNumbered() = (numPrefix.isNotEmpty() || curNum != 0.0)

    fun hasFilterText(filter: String): Boolean {
        val lowerFilter = filter.toLowerCase(Locale.getDefault())
        if (name.toLowerCase(Locale.getDefault()).contains(lowerFilter)) return true
        if (category.toLowerCase(Locale.getDefault()).contains(lowerFilter)) return true
        if (notify && "notification".contains(lowerFilter)) return true
        return false
    }
}