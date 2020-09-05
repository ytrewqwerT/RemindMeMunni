package com.example.remindmemunni.common

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import kotlinx.android.synthetic.main.fragment_list_item.view.*

class ListItemRecyclerViewAdapter<T : ListItemViewable?>(
    private val listener: OnListItemInteractionListener<T>?
) : RecyclerView.Adapter<ListItemRecyclerViewAdapter<T>.ViewHolder>() {

    private var items: List<T?> = emptyList()
    private val onClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            @Suppress("UNCHECKED_CAST")
            listener?.onInteraction(it.tag as T)
        }
    }
    var contextMenuParent: T? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = items[position]?.getListItemContents()
        holder.mainView.text = contents?.mMainText
        holder.subLeftView.text = contents?.mSubLeftText
        holder.subRightView.text = contents?.mSubRightText

        holder.subLeftView.visibility = if (contents?.mSubLeftText?.isNotEmpty() != true) {
            View.GONE
        } else {
            View.VISIBLE
        }

        holder.subRightView.visibility = if (contents?.mSubRightText?.isNotEmpty() != true) {
            View.GONE
        } else {
            View.VISIBLE
        }

        holder.view.tag = items[position]
        holder.view.setOnClickListener(onClickListener)
    }

    override fun getItemCount(): Int = items.size
    fun getItem(position: Int): T? = items[position]

    inner class ViewHolder(val view: View)
        : RecyclerView.ViewHolder(view)
        , View.OnCreateContextMenuListener {

        val mainView: TextView = view.main_text
        val subLeftView: TextView = view.sub_left_text
        val subRightView: TextView = view.sub_right_text

        init { view.setOnCreateContextMenuListener(this) }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            @Suppress("UNCHECKED_CAST")
            contextMenuParent = view.tag as? T?
        }

        override fun toString(): String = super.toString() + " '" + mainView.text + "'"
    }

    fun setItems(items: List<T?>) {
        this.items = items
        notifyDataSetChanged()
    }
}
