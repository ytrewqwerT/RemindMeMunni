package com.example.remindmemunni.common

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class UnfilteredArrayAdapter<T>(context: Context, resource: Int, objects: MutableList<T>) :
    ArrayAdapter<T>(context, resource, objects) {

    private val filter = NoFilter()

    val items: MutableList<T> = objects

    override fun add(obj: T?) { if (obj != null) items.add(obj) }
    override fun addAll(collection: MutableCollection<out T>) { items.addAll(collection) }
    override fun addAll(vararg items: T) { this.items.addAll(items) }
    override fun clear() { items.clear() }
    override fun getFilter(): Filter { return filter }
    override fun getItem(position: Int): T? = items.getOrNull(position)

    fun addAll(items: List<T>) { this.items.addAll(items) }

    companion object {
        fun createFromResource(context: Context, textArrayResId: Int, textViewResId: Int)
                : UnfilteredArrayAdapter<CharSequence> {

            val strings = context.resources.getTextArray(textArrayResId)
            return UnfilteredArrayAdapter(context, textViewResId, strings.toMutableList())
        }
    }

    inner class NoFilter: Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
            values = items
            count = items.size
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }
}