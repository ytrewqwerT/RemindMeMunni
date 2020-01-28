package com.example.remindmemunni.activityseries

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.CustomRecyclerViewAdapter
import com.example.remindmemunni.activitynewitem.NewItemActivity
import com.example.remindmemunni.database.Item

// TODO: Use the existing [ItemsFragment] to display the list
class SeriesActivity : AppCompatActivity() {

    private val viewModel: SeriesViewModel by lazy {
        ViewModelProvider(
            this, SeriesViewModel.SeriesViewModelFactory(application, seriesId)
        )[SeriesViewModel::class.java]
    }

    private val mRecyclerViewAdapter: CustomRecyclerViewAdapter<Item> by lazy {
        CustomRecyclerViewAdapter<Item>(null)
    }

    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        seriesId = intent.getIntExtra(EXTRA_SERIES_ID, 0)
        Log.d("Nice", "$seriesId")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = mRecyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        registerForContextMenu(recyclerView)

        Log.d("Nice", "${viewModel.series.value}")
        viewModel.series.observe(this, Observer { series ->
            title = series?.series?.name
            series?.items?.let { mRecyclerViewAdapter.setItems(it) }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean = when (menuItem?.itemId){
        android.R.id.home -> {
            finish()
            true
        }
        R.id.add_button -> {
            // TODO: Start NewItemActivity with series auto-set to current series
            val intent = Intent(this, NewItemActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_item_context, menu)
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
        R.id.item_edit -> {
            val item = mRecyclerViewAdapter.contextMenuItem
            Toast.makeText(applicationContext, "Edit ${item?.name}", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.item_finish -> {
            val item = mRecyclerViewAdapter.contextMenuItem
            Toast.makeText(applicationContext, "Complete ${item?.name}", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.item_delete -> {
            val item = mRecyclerViewAdapter.contextMenuItem
            Toast.makeText(applicationContext, "Delete ${item?.name}", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onContextItemSelected(menuItem)
    }

    companion object {
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}
