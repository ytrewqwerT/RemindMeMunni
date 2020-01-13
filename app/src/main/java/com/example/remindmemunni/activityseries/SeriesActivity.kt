package com.example.remindmemunni.activityseries

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.view.menu.MenuView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.activitymain.ItemViewModel
import com.example.remindmemunni.activitymain.ItemsRecyclerViewAdapter
import com.example.remindmemunni.database.Item

class SeriesActivity : AppCompatActivity() {

    private lateinit var viewModel: SeriesViewModel
    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)


        seriesId = intent.getIntExtra(EXTRA_SERIES_ID, 0)
        Log.d("Nice", "$seriesId")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ItemsRecyclerViewAdapter(null)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this, SeriesViewModel.SeriesViewModelFactory(application, seriesId))[SeriesViewModel::class.java]
        Log.d("Nice", "${viewModel.series.value}")
        viewModel.series.observe(this, Observer { series ->
            series?.items?.let { adapter.setItems(it) }
        })
    }

    companion object {
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}
