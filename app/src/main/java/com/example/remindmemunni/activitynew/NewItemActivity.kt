package com.example.remindmemunni.activitynew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.remindmemunni.R

class NewItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        setSupportActionBar(findViewById(R.id.toolbar_new))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            finish()
            true
        }

        R.id.done_button -> {
            // TODO: Validate input. Create and finish activity if valid. Complain otherwise.
            finish()
            true
        }

        else -> {
            Log.d("Nice", "NewActivity unknown button press: ${item?.itemId}")
            super.onOptionsItemSelected(item)
        }
    }
}
