package com.example.remindmemunni.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.remindmemunni.R
import com.example.remindmemunni.adapters.ItemPagerAdapter

class MainActivity : AppCompatActivity() {

    private val itemPagerAdapter by lazy {
        ItemPagerAdapter(
            supportFragmentManager
        )
    }
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager = findViewById(R.id.pager)
        viewPager.adapter = itemPagerAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.add_button -> {
            val intent = when (viewPager.currentItem) {
                ItemPagerAdapter.POS_ITEMS -> Intent(this, NewItemActivity::class.java)
                ItemPagerAdapter.POS_SERIES -> Intent(this, NewSeriesActivity::class.java)
                else -> null
            }
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}
