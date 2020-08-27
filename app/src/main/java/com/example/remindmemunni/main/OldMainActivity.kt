package com.example.remindmemunni.main

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ItemPagerAdapter
import com.example.remindmemunni.databinding.ActivityOldMainBinding
import com.example.remindmemunni.newitem.NewItemActivity
import com.example.remindmemunni.newseries.NewSeriesActivity
import com.example.remindmemunni.series.SeriesActivity
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.utils.toStringTrimmed
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OldMainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        InjectorUtils.provideMainViewModelFactory(applicationContext)
    }
    private lateinit var binding: ActivityOldMainBinding
//    private val itemPagerAdapter by lazy {
//        ItemPagerAdapter(this)
//    }
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_old_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager2 = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.pager_tabs)
//        viewPager2.adapter = itemPagerAdapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                ItemPagerAdapter.POS_PAST_ITEMS -> "Overdue"
                ItemPagerAdapter.POS_FUTURE_ITEMS -> "Upcoming"
                ItemPagerAdapter.POS_SERIES -> "Series"
                else -> "???"
            }
        }.attach()

        val endPointSlider = findViewById<Slider>(R.id.endpoint_slider)
        endPointSlider.addOnChangeListener { _, value, _ ->
            viewModel.monthsOffset = value.toInt()
        }

        val munniEditText = findViewById<EditText>(R.id.cur_munni_text)
        munniEditText.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KEYCODE_ENTER -> {
                    viewModel.curMunni.value = munniEditText.text.toString().toDoubleOrNull()
                    munniEditText.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(munniEditText.windowToken, 0)

                    true
                }
                else -> false
            }
        }

        viewModel.curMunni.observe(this) {
            title = getString(R.string.app_name) + ": \$${it.toStringTrimmed()}"
            munniEditText.setText(it.toStringTrimmed())
            viewModel.updateMunniCalc()
        }
        
        viewModel.allItems.observe(this) { viewModel.updateMunniCalc() }
        viewModel.allSeries.observe(this) { viewModel.updateMunniCalc() }

        createNotificationChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.search_button).actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.queryHint = getString(R.string.filter_hint)
        searchView.maxWidth = 900

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterText.value = newText
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add_button -> {
            when (viewPager2.currentItem) {
                ItemPagerAdapter.POS_PAST_ITEMS -> {
                    val intent = Intent(this, NewItemActivity::class.java)
                    startActivity(intent)
                }
                ItemPagerAdapter.POS_FUTURE_ITEMS -> {
                    val intent = Intent(this, NewItemActivity::class.java)
                    startActivity(intent)
                }
                ItemPagerAdapter.POS_SERIES -> {
                    val intent = Intent(this, NewSeriesActivity::class.java)
                    startActivityForResult(intent,
                        NEW_SERIES_REQUEST
                    )
                }
                else -> Log.w(
                    "MainActivity",
                    "No add action associated to PagerAdapter page ${viewPager2.currentItem}"
                )
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            NEW_SERIES_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val newSeriesId = data?.getIntExtra(NewSeriesActivity.EXTRA_SERIES_ID, 0) ?: 0
                    if (newSeriesId != 0) {
                        val intent = Intent(this, SeriesActivity::class.java)
                        intent.putExtra(SeriesActivity.EXTRA_SERIES_ID, newSeriesId)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val id = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val NEW_SERIES_REQUEST = 1
    }
}
