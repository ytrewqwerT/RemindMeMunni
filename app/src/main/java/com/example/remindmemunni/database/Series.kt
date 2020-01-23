package com.example.remindmemunni.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.remindmemunni.ListItemViewable

@Entity(tableName = "series_table")
data class Series (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val cost: Double = 0.0,
    val curNum: Double = 0.0,
    val numPrefix: String = "",
    val recurMonths: Int = 0,
    val recurDays: Int = 0
) : ListItemViewable {

    override fun getListItemContents() = ListItemViewable.ListItemContents(
        name, "$numPrefix $curNum", "\$$cost"
    )

    override fun toString(): String = name      // TODO: Not do this
}