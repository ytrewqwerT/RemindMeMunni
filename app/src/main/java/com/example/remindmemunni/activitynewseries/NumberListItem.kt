package com.example.remindmemunni.activitynewseries

import com.example.remindmemunni.ListItemViewable
import com.example.remindmemunni.ListItemViewable.ListItemContents

class NumberListItem(val num: Int? = null) : ListItemViewable {

    override fun getListItemContents(): ListItemContents = ListItemContents(
        num?.toString() ?: "",
        "",
        ""
    )

    companion object {
        fun createSequentialList(lower: Int, upper: Int, increment: Int = 1): MutableList<NumberListItem> {
            val list = ArrayList<NumberListItem>((upper - lower) / increment)
            for (i in lower..upper step increment) {
                list.add(NumberListItem(i))
            }
            return list
        }
    }
}