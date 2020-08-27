package com.example.remindmemunni.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.remindmemunni.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
    }
}