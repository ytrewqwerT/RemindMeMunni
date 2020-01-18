package com.example.remindmemunni.activitymain

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.remindmemunni.CustomRecyclerViewAdapter
import com.example.remindmemunni.R
import com.example.remindmemunni.database.Item

class ItemsFragment : Fragment() {

    private lateinit var viewModel: ItemViewModel
    private lateinit var recyclerViewAdapter: CustomRecyclerViewAdapter<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this)[ItemViewModel::class.java]
        } ?: throw Exception("RIP")
        recyclerViewAdapter = CustomRecyclerViewAdapter(null)
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
                this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
        return view
    }
}
