package com.example.remindmemunni

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_item.view.*

class CustomRecyclerViewAdapter<T : ListItemViewable>(
    private val mListener: OnListItemInteractionListener<T>?
) : RecyclerView.Adapter<CustomRecyclerViewAdapter<T>.ViewHolder>() {

    private var items = emptyList<T>()
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as T
            mListener?.onInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = items[position].getListItemContents()
        holder.mMainView.text = contents.mMainText
        holder.mSubLeftView.text = contents.mSubLeftText
        holder.mSubRightView.text = contents.mSubRightText

        with(holder.mView) {
            tag = items[position]
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mMainView: TextView = mView.main_text
        val mSubLeftView: TextView = mView.sub_left_text
        val mSubRightView: TextView = mView.sub_right_text

        override fun toString(): String {
            return super.toString() + " '" + mMainView.text + "'"
        }
    }

    internal fun setItems(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }
}
