package com.example.remindmemunni.activitymain

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.remindmemunni.CustomRecyclerViewAdapter
import com.example.remindmemunni.ItemsListViewModel
import com.example.remindmemunni.R
import com.example.remindmemunni.activitynewitem.NewItemActivity
import com.example.remindmemunni.database.Item
import com.google.android.material.snackbar.Snackbar

class ItemsFragment(private val seriesId: Int = 0) : Fragment() {

    private val viewModel: ItemsListViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ItemsListViewModel.ItemsListViewModelFactory(requireActivity().application, seriesId)
        )[ItemsListViewModel::class.java]
    }

    private val recyclerViewAdapter: CustomRecyclerViewAdapter<Item> by lazy {
        CustomRecyclerViewAdapter<Item>(null)
    }

    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.mItemsList.observe(this, Observer { items ->
            items?.let { recyclerViewAdapter.setItems(it) }
        })
    }

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
                registerForContextMenu(this)
            }
        }
        return mView
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
            if (item != null) {
                viewModel.complete(item)
            }
            true
        }
        R.id.item_delete -> {
            val item = recyclerViewAdapter.contextMenuItem
            if (item != null) {
                viewModel.delete(item)
                Snackbar.make(mView, "Item ${item.name} deleted.", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.insert(item)
                    }.show()
            }
            true
        }
        else -> super.onContextItemSelected(menuItem)
    }
}
