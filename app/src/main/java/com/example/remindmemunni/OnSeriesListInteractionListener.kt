package com.example.remindmemunni

import com.example.remindmemunni.database.AggregatedSeries

interface OnSeriesListInteractionListener {
    fun onInteraction(series: AggregatedSeries)
}