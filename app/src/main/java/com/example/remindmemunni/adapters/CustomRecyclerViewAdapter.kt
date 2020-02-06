package com.example.remindmemunni.adapters

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.interfaces.ListItemViewable
import com.example.remindmemunni.interfaces.OnListItemInteractionListener
import kotlinx.android.synthetic.main.fragment_item.view.*

class CustomRecyclerViewAdapter<T : ListItemViewable?>(
    private val listener: OnListItemInteractionListener<T>?
) : RecyclerView.Adapter<CustomRecyclerViewAdapter<T>.ViewHolder>() {

    private var items: List<T?> = emptyList()
    private val onClickListener: View.OnClickListener by lazy {
        View.OnClickListener { v ->
            @Suppress("UNCHECKED_CAST")
            listener?.onInteraction(v.tag as T)
        }
    }
    var contextMenuItem: T? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = items[position]?.getListItemContents()
        holder.mainView.text = contents?.mMainText
        holder.subLeftView.text = contents?.mSubLeftText
        holder.subRightView.text = contents?.mSubRightText

        if (contents?.mSubLeftText?.isNotEmpty() != true) {
            holder.subLeftView.visibility = View.GONE
        } else {
            holder.subLeftView.visibility = View.VISIBLE
        }
        if (contents?.mSubRightText?.isNotEmpty() != true) {
            holder.subRightView.visibility = View.GONE
        } else {
            holder.subRightView.visibility = View.VISIBLE
        }

        with(holder.view) {
            tag = items[position]
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = items.size
    fun getItem(position: Int): T? = items.getOrNull(position)

    inner class ViewHolder(val view: View)
        : RecyclerView.ViewHolder(view)
        , View.OnCreateContextMenuListener {

        val mainView: TextView = view.main_text
        val subLeftView: TextView = view.sub_left_text
        val subRightView: TextView = view.sub_right_text

        init {
            view.setOnCreateContextMenuListener(this)
        }
        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            @Suppress("UNCHECKED_CAST")
            contextMenuItem = view.tag as? T?
        }

        override fun toString(): String = super.toString() + " '" + mainView.text + "'"
    }

    internal fun setItems(items: List<T?>) {
        this.items = items
        notifyDataSetChanged()
    }
}
