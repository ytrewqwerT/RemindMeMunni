package com.example.remindmemunni.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ListItemViewable
import com.example.remindmemunni.utils.Strings
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
        cost < 0.0 -> Strings.get(R.string.format_cost_debit, -cost)
        cost > 0.0 -> Strings.get(R.string.format_cost_credit, cost)
        else -> ""
    }

    fun getRecurrenceString(): String {
        var result = ""
        if (recurMonths > 0) {
            result += if (recurMonths == 1) {
                Strings.get(R.string.one_month)
            } else Strings.get(R.string.format_num_months, recurMonths)
        }
        if (recurDays > 0) {
            if (result.isNotEmpty()) result += Strings.get(R.string.and_conjunctor)
            result += if (recurDays == 1) {
                Strings.get(R.string.one_day)
            } else Strings.get(R.string.format_num_days, recurDays)
        }
        return result
    }

    fun isNumbered() = (numPrefix.isNotEmpty() || curNum != 0.0)

    fun hasFilterText(filter: String): Boolean {
        val lowerFilter = filter.toLowerCase(Locale.getDefault())
        if (name.toLowerCase(Locale.getDefault()).contains(lowerFilter)) return true
        if (category.toLowerCase(Locale.getDefault()).contains(lowerFilter)) return true
        val notifyStr = Strings.get(R.string.notify).toLowerCase(Locale.getDefault())
        if (notify && notifyStr.contains(lowerFilter)) return true
        return false
    }
}