package com.example.remindmemunni.itemslist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.common.CustomRecyclerViewAdapter
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.main.MainViewModel
import com.example.remindmemunni.newitem.NewItemActivity
import com.example.remindmemunni.utils.InjectorUtils
import com.google.android.material.snackbar.Snackbar

class ItemsFragment(private val seriesId: Int = 0) : Fragment() {

    private val uid = nextId++

    private val viewModel: ItemsListViewModel by viewModels {
        InjectorUtils.provideItemsListViewModelFactory(requireActivity(), seriesId)
    }
    private val mainViewModel: MainViewModel by activityViewModels {
        InjectorUtils.provideMainViewModelFactory(requireActivity())
    }

    private val recyclerViewAdapter by lazy {
        CustomRecyclerViewAdapter<Item>(
            null
        )
    }
    private lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.filteredItems.observe(this, Observer { items ->
            items?.let { recyclerViewAdapter.setItems(it) }
        })

        viewModel.newItemEvent.observe(this, Observer { itemId ->
            val intent = Intent(activity, NewItemActivity::class.java)
            intent.putExtra(NewItemActivity.EXTRA_ITEM_ID, itemId)
            startActivityForResult(intent,
                SAVE_ITEM_OR_DELETE
            )
        })

        val lowerBound = arguments?.getLong(EXTRA_LOWER_TIME_BOUND, 0L) ?: 0L
        val upperBound = arguments?.getLong(EXTRA_UPPER_TIME_BOUND, Long.MAX_VALUE) ?: Long.MAX_VALUE
        viewModel.lowerTimeBound.value = lowerBound
        viewModel.upperTimeBound.value = upperBound
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
            viewModel.setFilter(it)
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
                    val intent = Intent(activity, NewItemActivity::class.java)
                    intent.putExtra(NewItemActivity.EXTRA_ITEM_ID, item.id)
                    startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SAVE_ITEM_OR_DELETE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                val itemId = data?.getIntExtra(NewItemActivity.EXTRA_ITEM_ID, 0) ?: 0
                viewModel.delete(itemId)
            }
        }
    }

    companion object {
        const val SAVE_ITEM_OR_DELETE = 1
        const val EXTRA_LOWER_TIME_BOUND = "LOWER_TIME_BOUND"
        const val EXTRA_UPPER_TIME_BOUND = "UPPER_TIME_BOUND"

        private var nextId = 0
        private var contextMenuSourceId = 0
    }
}
