package com.example.remindmemunni.itemslist

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.common.CustomRecyclerViewAdapter
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.main.MainViewModel
import com.example.remindmemunni.newitem.NewItemFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.google.android.material.snackbar.Snackbar

class ItemsFragment(private val seriesId: Int = 0) : Fragment() {

    private val uid = nextId++

    private val viewModel: ItemsListViewModel by viewModels {
        InjectorUtils.provideItemsListViewModelFactory(requireActivity(), seriesId)
    }
    private val mainViewModel: MainViewModel by viewModels(
        ownerProducer = { parentFragment ?: requireActivity() },
        factoryProducer = { InjectorUtils.provideMainViewModelFactory(requireActivity()) }
    )

    private val recyclerViewAdapter by lazy {
        CustomRecyclerViewAdapter<Item>(null)
    }
    private lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.filteredItems.observe(this) { items ->
            items?.let { recyclerViewAdapter.setItems(it) }
        }

        viewModel.newItemEvent.observe(this) { item ->
            view?.findNavController()?.navigate(
                R.id.newItemFragment,
                bundleOf(NewItemFragment.EXTRA_ITEM_DATA to item)
            )
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

        mainViewModel.filterText.observe(viewLifecycleOwner) {
            viewModel.filterStringChannel.offer(it ?: "")
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
                val item = recyclerViewAdapter.contextMenuItem
                if (item != null) {
                    view?.findNavController()?.navigate(
                        R.id.newItemFragment,
                        bundleOf(NewItemFragment.EXTRA_ITEM_DATA to item)
                    )
                }
                true
            }
            R.id.item_finish -> {
                val item = recyclerViewAdapter.contextMenuItem
                Snackbar.make(contentView, "Complete ${item?.name}", Snackbar.LENGTH_LONG).show()
                if (item != null) viewModel.complete(item)
                true
            }
            R.id.item_delete -> {
                val item = recyclerViewAdapter.contextMenuItem
                if (item != null) {
                    viewModel.delete(item)
                    Snackbar.make(contentView, "Item ${item.name} deleted.", Snackbar.LENGTH_LONG)
                        .setAction("Undo") { viewModel.insert(item) }
                        .show()
                }
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
}
