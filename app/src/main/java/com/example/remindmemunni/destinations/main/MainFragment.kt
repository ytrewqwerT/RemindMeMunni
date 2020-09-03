package com.example.remindmemunni.destinations.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.remindmemunni.Action
import com.example.remindmemunni.ActionViewModel
import com.example.remindmemunni.MainViewModel
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ItemPagerAdapter
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.destinations.item.ItemFragment
import com.example.remindmemunni.destinations.newseries.NewSeriesFragment
import com.example.remindmemunni.destinations.series.SeriesFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.utils.createNewItem
import com.example.remindmemunni.utils.createNewSerie
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels {
        InjectorUtils.provideMainViewModelFactory(requireContext())
    }
    private val actionViewModel: ActionViewModel by viewModels {
        InjectorUtils.provideActionViewModelFactory(requireContext())
    }

    private lateinit var itemPagerAdapter: ItemPagerAdapter
    private var pager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenForFragmentResults()

        itemPagerAdapter = ItemPagerAdapter(this)
        val pagerTabs = view.findViewById<TabLayout>(R.id.pager_tabs)
        pager = view.findViewById(R.id.pager)
        pager!!.adapter = itemPagerAdapter
        TabLayoutMediator(pagerTabs, pager!!) { tab, position ->
            tab.text = when (position) {
                ItemPagerAdapter.POS_PAST_ITEMS -> "Overdue"
                ItemPagerAdapter.POS_FUTURE_ITEMS -> "Upcoming"
                ItemPagerAdapter.POS_SERIES -> "Series"
                else -> "???"
            }
        }.apply { attach() }

        mainViewModel.categoryFilter.observe(viewLifecycleOwner) {
            activity?.title = when(it) {
                null -> MainViewModel.CATEGORY_ALL
                "" -> MainViewModel.CATEGORY_NONE
                else -> it
            }
        }

        actionViewModel.oneTimeAction.observe(viewLifecycleOwner) { processAction(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pager = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.search_button).actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.queryHint = getString(R.string.filter_hint)
        searchView.maxWidth = 900

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainViewModel.searchFilter.value = newText
                return true
            }

        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add_button -> {
            when (pager?.currentItem) {
                ItemPagerAdapter.POS_PAST_ITEMS, ItemPagerAdapter.POS_FUTURE_ITEMS -> {
                    actionViewModel.createNewItem()
                }
                ItemPagerAdapter.POS_SERIES -> {
                    actionViewModel.createNewSerie()
                }
                else -> Log.w(
                    "MainFragment",
                    "No add action associated to PagerAdapter page ${pager?.currentItem}"
                )
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun listenForFragmentResults() {
        setFragmentResultListener(NewSeriesFragment.REQUEST_RESULT) { _, result ->
            val newSeriesId = result.getInt(NewSeriesFragment.EXTRA_SERIES_ID, 0)
            if (newSeriesId != 0) {
                val action = MainFragmentDirections
                    .actionMainFragmentToSeriesFragment(newSeriesId)
                view?.findNavController()?.navigate(action)
            }
        }
        setFragmentResultListener(ItemFragment.REQUEST_RESULT) { _, result ->
            result.getParcelable<Item>(ItemFragment.RESULT_DELETE)?.let {
                actionViewModel.delete(it)
            }
            result.getParcelable<Item>(ItemFragment.RESULT_FINISH)?.let {
                actionViewModel.complete(it)
            }
        }
        setFragmentResultListener(SeriesFragment.REQUEST_RESULT) { _, result ->
            val deepDeleteId = result.getInt(SeriesFragment.RESULT_DELETE_DEEP, 0)
            if (deepDeleteId != 0) {
                lifecycleScope.launch {
                    val serie = mainViewModel.getSerie(deepDeleteId)
                    actionViewModel.delete(serie)
                }
            }
            val shallowDeleteId = result.getInt(SeriesFragment.RESULT_DELETE_SHALLOW, 0)
            if (shallowDeleteId != 0) {
                lifecycleScope.launch {
                    val serie = mainViewModel.getSerie(shallowDeleteId)
                    actionViewModel.delete(serie.series)
                }
            }
        }
    }

    private fun processAction(action: Action) {
        when(action) {
            is Action.ItemView -> {
                view?.findNavController()?.navigate(
                    MainFragmentDirections.actionMainFragmentToItemFragment(action.item.id)
                )
            }
            is Action.ItemEdit -> {
                view?.findNavController()?.navigate(
                    MainFragmentDirections.actionMainFragmentToNewItemFragment(action.item)
                )
            }
            is Action.ItemFinish -> {
                view?.let {
                    Snackbar.make(it, "Complete ${action.item.name}", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
            is Action.ItemDelete -> {
                view?.let {
                    val item = action.item
                    Snackbar.make(it, "Item ${item.name} deleted.", Snackbar.LENGTH_LONG)
                        .setAction("Undo") { actionViewModel.insert(item) }
                        .show()
                }
            }

            is Action.SerieView -> {
                view?.findNavController()?.navigate(
                    MainFragmentDirections.actionMainFragmentToSeriesFragment(action.serie.id)
                )
            }
            is Action.SerieEdit -> {
                view?.findNavController()?.navigate(
                    MainFragmentDirections.actionMainFragmentToNewSeriesFragment(action.serie.id)
                )
            }
            is Action.SerieDelete -> {
                val series = action.serie
                view?.let { view ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("Deleting ${series.series.name}")
                        .setMessage("Do you want to delete the items in this series?")
                        .setPositiveButton("Yes") { _, _ ->
                            actionViewModel.delete(series)
                            Snackbar.make(
                                view,
                                "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG
                            ).setAction("Undo") {
                                actionViewModel.insert(series)
                            }.show()
                        }.setNegativeButton("No") { _, _ ->
                            actionViewModel.delete(series.series)
                            Snackbar.make(
                                view,
                                "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG
                            ).setAction("Undo") {
                                actionViewModel.insert(series.series)
                            }.show()
                        }.setNeutralButton("Cancel") { _, _ -> }
                        .create().show()
                }
            }
        }
    }
}