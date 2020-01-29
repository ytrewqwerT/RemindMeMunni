package com.example.remindmemunni.activitymain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.CustomRecyclerViewAdapter
import com.example.remindmemunni.OnListItemInteractionListener
import com.example.remindmemunni.R
import com.example.remindmemunni.activitynewseries.NewSeriesActivity
import com.example.remindmemunni.activityseries.SeriesActivity
import com.example.remindmemunni.database.AggregatedSeries
import com.google.android.material.snackbar.Snackbar

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

    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_item_list, container, false)
        if (mView is RecyclerView) {
            with(mView as RecyclerView) {
                layoutManager = LinearLayoutManager(context)
                adapter = recyclerViewAdapter
                val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                this.addItemDecoration(decoration)
                registerForContextMenu(mView)
            }
        }
        return mView
    }

    override fun onInteraction(item: AggregatedSeries) {
        val intent = Intent(activity, SeriesActivity::class.java)
        Log.d("Nice", "${item.series.id}")
        intent.putExtra(SeriesActivity.EXTRA_SERIES_ID, item.series.id)
        startActivity(intent)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.menu_series_context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.series_edit -> {
            val series = recyclerViewAdapter.contextMenuItem
            if (series != null) {
                val intent = Intent(activity, NewSeriesActivity::class.java)
                intent.putExtra(NewSeriesActivity.EXTRA_SERIES_ID, series.series.id)
                startActivity(intent)
            }
            true
        }
        R.id.series_delete -> {
            val series = recyclerViewAdapter.contextMenuItem
            if (series != null) {
                viewModel.delete(series.series)
                Snackbar.make(mView, "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.insert(series.series)
                    }.show()
            }
            true
        }
        else -> super.onContextItemSelected(item)
    }
}
