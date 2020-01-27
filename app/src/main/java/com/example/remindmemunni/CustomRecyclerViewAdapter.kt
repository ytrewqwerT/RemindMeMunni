package com.example.remindmemunni

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.SnapHelper
import kotlinx.android.synthetic.main.fragment_item.view.*

class CustomRecyclerViewAdapter<T : ListItemViewable?>(
    private val mListener: OnListItemInteractionListener<T>?
) : RecyclerView.Adapter<CustomRecyclerViewAdapter<T>.ViewHolder>() {

    private var items: List<T?> = emptyList()
    private val mOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { v ->
            mListener?.onInteraction(v.tag as T)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = items[position]?.getListItemContents()
        holder.mMainView.text = contents?.mMainText
        holder.mSubLeftView.text = contents?.mSubLeftText
        holder.mSubRightView.text = contents?.mSubRightText

        if (contents?.mSubLeftText?.isNotEmpty() != true) holder.mSubLeftView.visibility = View.GONE
        if (contents?.mSubRightText?.isNotEmpty() != true) holder.mSubRightView.visibility = View.GONE

        with(holder.mView) {
            tag = items[position]
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getItem(position: Int): T? = items.getOrNull(position)

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mMainView: TextView = mView.main_text
        val mSubLeftView: TextView = mView.sub_left_text
        val mSubRightView: TextView = mView.sub_right_text

        override fun toString(): String {
            return super.toString() + " '" + mMainView.text + "'"
        }
    }

    internal fun setItems(items: List<T?>) {
        this.items = items
        notifyDataSetChanged()
    }
}
