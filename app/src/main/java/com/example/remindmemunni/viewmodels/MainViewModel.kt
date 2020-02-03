package com.example.remindmemunni.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _munniRemaining = MutableLiveData<String>("Default Value")
    val munniRemaining: LiveData<String> = _munniRemaining

    fun setMunniCalcEndDist(months: Int) {
        // TODO: Get all items
        _munniRemaining.value = "After $months Months"
    }
}