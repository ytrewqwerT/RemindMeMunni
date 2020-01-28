package com.example.remindmemunni.activitymain

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
import com.example.remindmemunni.R
import com.example.remindmemunni.database.Item

class ItemsFragment : Fragment() {

    private val viewModel: ItemViewModel by lazy {
        activity?.run { ViewModelProvider(this)[ItemViewModel::class.java] }
            ?: ViewModelProvider(this)[ItemViewModel::class.java]
    }

    private val recyclerViewAdapter: CustomRecyclerViewAdapter<Item> by lazy {
        CustomRecyclerViewAdapter<Item>(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allItems.observe(this, Observer { items ->
            items?.let { recyclerViewAdapter.setItems(it) }
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
                adapter = recyclerViewAdapter
                val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                this.addItemDecoration(decoration)
                registerForContextMenu(this)
            }
        }
        return view
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
            Toast.makeText(context, "Edit ${item?.name}", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.item_finish -> {
            val item = recyclerViewAdapter.contextMenuItem
            Toast.makeText(context, "Complete ${item?.name}", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.item_delete -> {
            val item = recyclerViewAdapter.contextMenuItem
            Toast.makeText(context, "Delete ${item?.name}", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onContextItemSelected(menuItem)
    }
}
