package com.example.remindmemunni.activitymain

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.R
import com.example.remindmemunni.database.Item

class ItemsFragment : Fragment() {

    private lateinit var itemViewModel: ItemViewModel
    private lateinit var itemsRecyclerViewAdapter: ItemsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemViewModel = activity?.run {
            ViewModelProvider(this)[ItemViewModel::class.java]
        } ?: throw Exception("RIP")
        itemsRecyclerViewAdapter =
            ItemsRecyclerViewAdapter(null)
        itemViewModel.allItems.observe(this, Observer { items ->
            items?.let { itemsRecyclerViewAdapter.setItems(it) }
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
                adapter = itemsRecyclerViewAdapter
            }
        }
        return view
    }
}
