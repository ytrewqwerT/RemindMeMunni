package com.example.remindmemunni.serieslist

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.common.CustomRecyclerViewAdapter
import com.example.remindmemunni.common.OnListItemInteractionListener
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.destinations.main.MainFragmentDirections
import com.example.remindmemunni.destinations.main.MainViewModel
import com.example.remindmemunni.destinations.newseries.NewSeriesFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.google.android.material.snackbar.Snackbar

class SeriesListFragment : Fragment(),
    OnListItemInteractionListener<AggregatedSeries> {

    private val viewModel: SeriesListViewModel by viewModels {
        InjectorUtils.provideSeriesListViewModelFactory(requireActivity())
    }
    private val mainViewModel: MainViewModel by viewModels(
        ownerProducer = { parentFragment ?: requireActivity() },
        factoryProducer = { ViewModelProvider.NewInstanceFactory() }
    )

    private val recyclerViewAdapter by lazy {
        @Suppress("RemoveExplicitTypeArguments")
        CustomRecyclerViewAdapter<AggregatedSeries>(this)
    }
    private lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.filteredSeries.observe(this) { series ->
            series?.let { recyclerViewAdapter.setItems(it) }
        }
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

        mainViewModel.filterText.observe(viewLifecycleOwner) {
            viewModel.filterStringChannel.offer(it ?: "")
        }
        mainViewModel.categoryFilter.observe(viewLifecycleOwner) {
            viewModel.filterCategoryChannel.offer(it)
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
                view?.findNavController()?.navigate(
                    R.id.newSeriesFragment,
                    bundleOf(NewSeriesFragment.EXTRA_SERIES_ID to series.series.id)
                )
            }
            true
        }
        R.id.series_delete -> {
            val series = recyclerViewAdapter.contextMenuItem
            if (series != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Deleting ${series.series.name}")
                    .setMessage("Do you want to delete the items in this series?")
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.delete(series)
                        Snackbar.make(
                            contentView,
                            "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG
                        ).setAction("Undo") {
                            viewModel.insert(series)
                        }.show()
                    }.setNegativeButton("No") { _, _ ->
                        viewModel.delete(series.series)
                        Snackbar.make(
                            contentView,
                            "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG
                        ).setAction("Undo") {
                            viewModel.insert(series.series)
                        }.show()
                    }.setNeutralButton("Cancel") { _, _ -> }
                    .create().show()
            }
            true
        }
        else -> super.onContextItemSelected(item)
    }

    override fun onInteraction(item: AggregatedSeries) {
        val action = MainFragmentDirections
            .actionMainFragmentToSeriesFragment(item.series.id)
        view?.findNavController()?.navigate(action)
    }
}
