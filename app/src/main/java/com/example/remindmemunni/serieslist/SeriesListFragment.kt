package com.example.remindmemunni.serieslist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.ActionViewModel
import com.example.remindmemunni.MainViewModel
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ListItemRecyclerViewAdapter
import com.example.remindmemunni.common.OnListItemInteractionListener
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.utils.InjectorUtils

class SeriesListFragment : Fragment(),
    OnListItemInteractionListener<AggregatedSeries> {

    private val viewModel: SeriesListViewModel by viewModels {
        InjectorUtils.provideSeriesListViewModelFactory(requireActivity())
    }
    private val actionViewModel: ActionViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { InjectorUtils.provideActionViewModelFactory(requireContext()) }
    )
    private val mainViewModel: MainViewModel by activityViewModels {
        InjectorUtils.provideMainViewModelFactory(requireContext())
    }

    private val recyclerViewAdapter by lazy {
        @Suppress("RemoveExplicitTypeArguments")
        ListItemRecyclerViewAdapter<AggregatedSeries>(this)
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

        mainViewModel.searchFilter.observe(viewLifecycleOwner) {
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
            recyclerViewAdapter.contextMenuParent?.let { actionViewModel.edit(it.series) }
            true
        }
        R.id.series_delete -> {
            recyclerViewAdapter.contextMenuParent?.let { actionViewModel.promptDelete(it) }
            true
        }
        else -> super.onContextItemSelected(item)
    }

    override fun onInteraction(item: AggregatedSeries) { actionViewModel.view(item.series) }
}
