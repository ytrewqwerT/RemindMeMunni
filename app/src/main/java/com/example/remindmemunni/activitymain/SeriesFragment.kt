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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.OnSeriesListInteractionListener
import com.example.remindmemunni.R
import com.example.remindmemunni.activityseries.SeriesActivity
import com.example.remindmemunni.database.AggregatedSeries

class SeriesFragment : Fragment(), OnSeriesListInteractionListener {

    private lateinit var viewModel: ItemViewModel
    private lateinit var seriesRecyclerViewAdapter: SeriesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this)[ItemViewModel::class.java]
        } ?: throw Exception("RIP")
        seriesRecyclerViewAdapter =
            SeriesRecyclerViewAdapter(this)
        viewModel.allSeries.observe(this, Observer { series ->
            series?.let { seriesRecyclerViewAdapter.setItems(it) }
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
                adapter = seriesRecyclerViewAdapter
            }
        }
        return view
    }

    override fun onInteraction(series: AggregatedSeries) {
        val intent = Intent(activity, SeriesActivity::class.java)
        Log.d("Nice", "${series.series.id}")
        intent.putExtra(SeriesActivity.EXTRA_SERIES_ID, series.series.id)
        startActivity(intent)
    }
}
