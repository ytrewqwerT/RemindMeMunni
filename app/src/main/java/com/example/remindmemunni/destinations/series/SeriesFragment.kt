package com.example.remindmemunni.destinations.series

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.remindmemunni.Action
import com.example.remindmemunni.ActionViewModel
import com.example.remindmemunni.R
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.destinations.item.ItemFragment
import com.example.remindmemunni.itemslist.ItemsListFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SeriesFragment : Fragment() {

    private val viewModel: SeriesViewModel by viewModels {
        InjectorUtils.provideSeriesViewModelFactory(requireContext(), seriesId)
    }
    private val actionViewModel: ActionViewModel by viewModels {
        InjectorUtils.provideActionViewModelFactory(requireContext())
    }

    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenForFragmentResults()

        seriesId = arguments?.getInt(EXTRA_SERIES_ID, 0) ?: 0

        viewModel.series.observe(viewLifecycleOwner) { activity?.title = it?.series?.name }

        val recurrenceTextView = view.findViewById<TextView>(R.id.subtitle)
        viewModel.series.observe(viewLifecycleOwner) {
            val series = it?.series
            var text = series?.getCostString()
            text += if (text?.isNotEmpty() == true) " repeating " else "Repeats "

            val recurrenceText = series?.getRecurrenceString()
            text += if (recurrenceText?.isNotEmpty() == true) "every $recurrenceText" else "never"
            recurrenceTextView.text = text
        }

        actionViewModel.oneTimeAction.observe(viewLifecycleOwner) { processAction(it) }

        childFragmentManager.commit { add(R.id.series_list_fragment, ItemsListFragment(seriesId)) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_series_fragment, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId){
        R.id.add_button -> {
            lifecycleScope.launch {
                val newItem = viewModel.generateNextItemInSeries()
                view?.findNavController()?.navigate(
                    SeriesFragmentDirections.actionSeriesFragmentToNewItemFragment(ITEMDATA = newItem)
                )
            }
            true
        }
        R.id.serie_edit -> {
            view?.findNavController()?.navigate(
                SeriesFragmentDirections.actionSeriesFragmentToNewSeriesFragment(seriesId)
            )
            true
        }
        R.id.serie_delete -> {
            promptSerieDelete()
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    private fun promptSerieDelete() {
        val series = viewModel.series.value ?: return
        view?.let { view ->
            AlertDialog.Builder(requireContext())
                .setTitle("Deleting ${series.series.name}")
                .setMessage("Do you want to delete the items in this series?")
                .setPositiveButton("Yes") { _, _ ->
                    setFragmentResult(REQUEST_RESULT, bundleOf(RESULT_DELETE_DEEP to seriesId))
                    view.findNavController().popBackStack()
                }.setNegativeButton("No") { _, _ ->
                    setFragmentResult(REQUEST_RESULT, bundleOf(RESULT_DELETE_SHALLOW to seriesId))
                    view.findNavController().popBackStack()
                }.setNeutralButton("Cancel") { _, _ -> }
                .create().show()
        }
    }

    private fun listenForFragmentResults() {
        setFragmentResultListener(ItemFragment.REQUEST_RESULT) { _, result ->
            result.getParcelable<Item>(ItemFragment.RESULT_DELETE)?.let {
                actionViewModel.deleteSerieShallow(it)
            }
            result.getParcelable<Item>(ItemFragment.RESULT_FINISH)?.let {
                actionViewModel.complete(it)
            }
        }
    }

    private fun processAction(action: Action) {
        when(action) {
            is Action.ItemView -> {
                view?.findNavController()?.navigate(
                    SeriesFragmentDirections.actionSeriesFragmentToItemFragment(action.item.id)
                )
            }
            is Action.ItemEdit -> {
                view?.findNavController()?.navigate(
                    SeriesFragmentDirections.actionSeriesFragmentToNewItemFragment(action.item)
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
        }
    }

    companion object {
        const val REQUEST_RESULT = "SERIES_FRAGMENT_REQUEST_RESULT"
        const val RESULT_DELETE_DEEP = "RESULT_DELETE_DEEP"
        const val RESULT_DELETE_SHALLOW = "RESULT_DELETE_SHALLOW"
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}