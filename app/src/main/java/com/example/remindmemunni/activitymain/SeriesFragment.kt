package com.example.remindmemunni.activitymain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.CustomRecyclerViewAdapter
import com.example.remindmemunni.OnListItemInteractionListener
import com.example.remindmemunni.R
import com.example.remindmemunni.activityseries.SeriesActivity
import com.example.remindmemunni.database.AggregatedSeries

class SeriesFragment : Fragment(), OnListItemInteractionListener<AggregatedSeries> {

    private val viewModel: ItemViewModel by lazy {
        activity?.run { ViewModelProvider(this)[ItemViewModel::class.java] }
            ?: ViewModelProvider(this)[ItemViewModel::class.java]
    }

    private val recyclerViewAdapter: CustomRecyclerViewAdapter<AggregatedSeries> by lazy {
        CustomRecyclerViewAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allSeries.observe(this, Observer { series ->
            series?.let { recyclerViewAdapter.setItems(it) }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = recyclerViewAdapter
                val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                this.addItemDecoration(decoration)
            }
        }
        return view
    }

    override fun onInteraction(item: AggregatedSeries) {
        val intent = Intent(activity, SeriesActivity::class.java)
        Log.d("Nice", "${item.series.id}")
        intent.putExtra(SeriesActivity.EXTRA_SERIES_ID, item.series.id)
        startActivity(intent)
    }
}
