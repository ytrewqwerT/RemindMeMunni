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
import com.example.remindmemunni.MainViewModel
import com.example.remindmemunni.R
import com.example.remindmemunni.common.Action
import com.example.remindmemunni.common.ActionViewModel
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
        setupPager(view)
        observeViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pager = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.search_button).actionView as SearchView
        searchView.apply {
            isSubmitButtonEnabled = false
            queryHint = getString(R.string.filter_items_series)
            maxWidth = 900

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = onQueryTextChange(query)
                override fun onQueryTextChange(newText: String?): Boolean {
                    mainViewModel.searchFilter.value = newText
                    return true
                }
            })
        }
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
            processNewSeriesFragmentResult(result)
        }
        setFragmentResultListener(ItemFragment.REQUEST_RESULT) { _, result ->
            processItemFragmentResult(result)
        }
        setFragmentResultListener(SeriesFragment.REQUEST_RESULT) { _, result ->
            processSeriesFragmentResult(result)
        }
    }

    private fun processNewSeriesFragmentResult(result: Bundle) {
        val newSeriesId = result.getInt(NewSeriesFragment.EXTRA_SERIES_ID, 0)
        if (newSeriesId != 0) {
            view?.findNavController()?.navigate(
                MainFragmentDirections.actionMainFragmentToSeriesFragment(newSeriesId)
            )
        }
    }

    private fun processItemFragmentResult(result: Bundle) {
        result.getParcelable<Item>(ItemFragment.RESULT_DELETE)?.let {
            actionViewModel.deleteItem(it)
        }
        result.getParcelable<Item>(ItemFragment.RESULT_FINISH)?.let {
            actionViewModel.complete(it)
        }
    }

    private fun processSeriesFragmentResult(result: Bundle) {
        val deepDeleteId = result.getInt(SeriesFragment.RESULT_DELETE_DEEP, 0)
        if (deepDeleteId != 0) {
            lifecycleScope.launch {
                val serie = mainViewModel.getSerie(deepDeleteId)
                actionViewModel.deleteSerieDeep(serie)
            }
        }
        val shallowDeleteId = result.getInt(SeriesFragment.RESULT_DELETE_SHALLOW, 0)
        if (shallowDeleteId != 0) {
            lifecycleScope.launch {
                val serie = mainViewModel.getSerie(shallowDeleteId)
                actionViewModel.deleteSerieShallow(serie)
            }
        }
    }

    private fun setupPager(view: View) {
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
    }

    private fun observeViewModels() {
        mainViewModel.categoryFilter.observe(viewLifecycleOwner) {
            activity?.title = when (it) {
                null -> MainViewModel.CATEGORY_ALL
                "" -> MainViewModel.CATEGORY_NONE
                else -> it
            }
        }

        actionViewModel.oneTimeAction.observe(viewLifecycleOwner) { processAction(it) }
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
            is Action.SerieDelete -> promptSerieDelete(action)
        }
    }

    private fun promptSerieDelete(action: Action.SerieDelete) {
        val series = action.serie
        val view = view ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Deleting ${series.series.name}")
            .setMessage("Do you want to delete the items in this series?")
            .setPositiveButton("Yes") { _, _ ->
                actionViewModel.deleteSerieDeep(series)
                Snackbar.make(
                    view,
                    "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG
                ).setAction("Undo") {
                    actionViewModel.insert(series)
                }.show()
            }.setNegativeButton("No") { _, _ ->
                actionViewModel.deleteSerieShallow(series)
                Snackbar.make(
                    view,
                    "Series ${series.series.name} deleted.", Snackbar.LENGTH_LONG
                ).setAction("Undo") {
                    actionViewModel.insert(series.series)
                }.show()
            }.setNeutralButton("Cancel") { _, _ ->
            }.create().show()
    }
}