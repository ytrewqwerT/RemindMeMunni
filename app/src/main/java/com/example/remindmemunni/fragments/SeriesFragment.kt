package com.example.remindmemunni.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.activities.NewSeriesActivity
import com.example.remindmemunni.activities.SeriesActivity
import com.example.remindmemunni.adapters.CustomRecyclerViewAdapter
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.interfaces.OnListItemInteractionListener
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.viewmodels.SeriesListViewModel
import com.google.android.material.snackbar.Snackbar

class SeriesFragment : Fragment(),
    OnListItemInteractionListener<AggregatedSeries> {

    private val viewModel: SeriesListViewModel by activityViewModels {
        InjectorUtils.provideSeriesListViewModelFactory(requireActivity())
    }

    private val recyclerViewAdapter by lazy {
        @Suppress("RemoveExplicitTypeArguments")
        CustomRecyclerViewAdapter<AggregatedSeries>(
            this
        )
    }
    private lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.series.observe(this, Observer { series ->
            series?.let { recyclerViewAdapter.setItems(it) }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.fragment_item_list, container, false)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.list)
        if (recyclerView != null) with (recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            this.addItemDecoration(decoration)
            registerForContextMenu(this)
        }
        return contentView
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
                Snackbar.make(
                    contentView,
                    "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG
                ).setAction("Undo") {
                        viewModel.insert(series.series)
                }.show()
            }
            true
        }
        else -> super.onContextItemSelected(item)
    }

    override fun onInteraction(item: AggregatedSeries) {
        val intent = Intent(activity, SeriesActivity::class.java)
        intent.putExtra(SeriesActivity.EXTRA_SERIES_ID, item.series.id)
        startActivity(intent)
    }

    fun setFilter(filterText: String?) {
        viewModel.setFilter(filterText)
    }
}
