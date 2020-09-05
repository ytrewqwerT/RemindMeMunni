package com.example.remindmemunni.itemslist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.MainViewModel
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ActionViewModel
import com.example.remindmemunni.common.ListItemRecyclerViewAdapter
import com.example.remindmemunni.common.OnListItemInteractionListener
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.utils.InjectorUtils

class ItemsListFragment(private val seriesId: Int = 0) : Fragment(),
    OnListItemInteractionListener<Item> {

    private val uid = nextId++

    private val viewModel: ItemsListViewModel by viewModels {
        InjectorUtils.provideItemsListViewModelFactory(requireContext(), seriesId)
    }
    private val actionViewModel: ActionViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { InjectorUtils.provideActionViewModelFactory(requireContext()) }
    )
    private val mainViewModel: MainViewModel by activityViewModels {
        InjectorUtils.provideMainViewModelFactory(requireContext())
    }

    private val recyclerViewAdapter by lazy {
        ListItemRecyclerViewAdapter(this)
    }
    private lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.filteredItems.observe(this) { items ->
            items?.let { recyclerViewAdapter.setItems(it) }
        }

        val lowerBound = arguments?.getLong(EXTRA_LOWER_TIME_BOUND, 0L) ?: 0L
        val upperBound = arguments?.getLong(EXTRA_UPPER_TIME_BOUND, Long.MAX_VALUE) ?: Long.MAX_VALUE
        viewModel.lowerTimeBoundChannel.offer(lowerBound)
        viewModel.upperTimeBoundChannel.offer(upperBound)
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
        activity?.menuInflater?.inflate(R.menu.menu_item_context, menu)
        contextMenuSourceId = uid
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        if (contextMenuSourceId != uid) return super.onContextItemSelected(menuItem)
        return when (menuItem.itemId) {
            R.id.item_edit -> {
                recyclerViewAdapter.contextMenuParent?.let { actionViewModel.edit(it) }
                true
            }
            R.id.item_finish -> {
                recyclerViewAdapter.contextMenuParent?.let { actionViewModel.complete(it) }
                true
            }
            R.id.item_delete -> {
                recyclerViewAdapter.contextMenuParent?.let { actionViewModel.deleteItem(it) }
                true
            }
            else -> super.onContextItemSelected(menuItem)
        }
    }

    companion object {
        const val EXTRA_LOWER_TIME_BOUND = "LOWER_TIME_BOUND"
        const val EXTRA_UPPER_TIME_BOUND = "UPPER_TIME_BOUND"

        private var nextId = 0
        private var contextMenuSourceId = 0
    }

    override fun onInteraction(item: Item) { actionViewModel.view(item) }
}
