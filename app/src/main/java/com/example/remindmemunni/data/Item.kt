package com.example.remindmemunni.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ListItemViewable
import com.example.remindmemunni.utils.Strings
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
) : ListItemViewable, Parcelable {

    override fun getListItemContents(): ListItemViewable.ListItemContents {
        return ListItemViewable.ListItemContents(name, getDateString(), getCostString())
    }

    override fun toString(): String {
        val offset = OffsetDateTime.now().offset
        val date = LocalDateTime.ofEpochSecond(time, 0, offset)
        return "$id $name: ${getCostString()}, $date"
    }

    fun getCostString(): String = when {
        cost < 0.0 -> Strings.get(R.string.format_cost_debit, -cost)
        cost > 0.0 -> Strings.get(R.string.format_cost_credit, cost)
        else -> ""
    }

    fun getDateString(): String =
        getDateString(DateTimeFormatter.ofPattern("EEEE, d MMMM - HH:mm"))
    private fun getDateString(formatter: DateTimeFormatter): String {
        if (time == 0L) return ""
        val offset = OffsetDateTime.now().offset
        val date = LocalDateTime.ofEpochSecond(time, 0, offset)
        return date.format(formatter)
    }

    fun hasFilterText(filter: String): Boolean {
        val lowerFilter = filter.toLowerCase(Locale.getDefault())
        if (name.toLowerCase(Locale.getDefault()).contains(lowerFilter)) return true
        if (category.toLowerCase(Locale.getDefault()).contains(lowerFilter)) return true
        val notifyStr = Strings.get(R.string.notify).toLowerCase(Locale.getDefault())
        if (notify && notifyStr.contains(lowerFilter)) return true
        return false
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(seriesId)
        parcel.writeString(name)
        parcel.writeDouble(cost)
        parcel.writeLong(time)
        parcel.writeString(category)
        parcel.writeByte(if (notify) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readDouble(),
                parcel.readLong(),
                parcel.readString()!!,
                parcel.readByte() != 0.toByte()
            )
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}