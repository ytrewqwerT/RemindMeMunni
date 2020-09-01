package com.example.remindmemunni.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.remindmemunni.itemslist.ItemsFragment
import com.example.remindmemunni.serieslist.SeriesListFragment
import com.example.remindmemunni.utils.PrimitiveDateTime
import java.time.LocalDateTime

class ItemPagerAdapter(frag: Fragment) : FragmentStateAdapter(frag) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            POS_PAST_ITEMS -> overdueItemsListFragment()
            POS_FUTURE_ITEMS -> upcomingItemsListFragment()
            POS_SERIES -> seriesListFragment()
            else -> upcomingItemsListFragment()
        }
    }

    private fun overdueItemsListFragment() = ItemsFragment().apply {
        val nowEpoch = PrimitiveDateTime.fromLocalDateTime(LocalDateTime.now())
            .toEpoch()
        val bundle = Bundle()
        bundle.putLong(ItemsFragment.EXTRA_UPPER_TIME_BOUND, nowEpoch)
        arguments = bundle
    }

    private fun upcomingItemsListFragment() = ItemsFragment().apply {
        val nowEpoch = PrimitiveDateTime.fromLocalDateTime(LocalDateTime.now())
            .toEpoch()
        val bundle = Bundle()
        bundle.putLong(ItemsFragment.EXTRA_LOWER_TIME_BOUND, nowEpoch)
        arguments = bundle
    }

    private fun seriesListFragment() = SeriesListFragment()

    companion object {
        const val NUM_PAGES = 3

        const val POS_PAST_ITEMS = 0
        const val POS_FUTURE_ITEMS = 1
        const val POS_SERIES = 2
    }
}