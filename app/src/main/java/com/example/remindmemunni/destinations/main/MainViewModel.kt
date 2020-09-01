package com.example.remindmemunni.destinations.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val filterText = MutableLiveData<String?>()
    val categoryFilter = MutableLiveData<String?>()
}