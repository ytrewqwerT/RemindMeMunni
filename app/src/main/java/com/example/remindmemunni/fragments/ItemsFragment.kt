package com.example.remindmemunni.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.activities.NewItemActivity
import com.example.remindmemunni.adapters.CustomRecyclerViewAdapter
import com.example.remindmemunni.database.Item
import com.example.remindmemunni.viewmodels.ItemsListViewModel
import com.google.android.material.snackbar.Snackbar

class ItemsFragment(private val seriesId: Int = 0) : Fragment() {

    private val viewModel: ItemsListViewModel by activityViewModels {
        ItemsListViewModel.ItemsListViewModelFactory(requireActivity().application, seriesId)
    }

    private val recyclerViewAdapter by lazy {
        CustomRecyclerViewAdapter<Item>(
            null
        )
    }
    private lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.mItemsList.observe(this, Observer { items ->
            items?.let { recyclerViewAdapter.setItems(it) }
        })

        viewModel.newItemEvent.observe(this, Observer { itemId ->
            val intent = Intent(activity, NewItemActivity::class.java)
            intent.putExtra(NewItemActivity.EXTRA_ITEM_ID, itemId)
            startActivityForResult(intent, SAVE_ITEM_OR_DELETE)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.fragment_item_list, container, false)
        if (contentView is RecyclerView) with (contentView as RecyclerView) {
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
        activity?.menuInflater?.inflate(R.menu.menu_item_context, menu)
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
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
            Toast.makeText(context, "Complete ${item?.name}", Toast.LENGTH_SHORT).show()
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
    }
}
