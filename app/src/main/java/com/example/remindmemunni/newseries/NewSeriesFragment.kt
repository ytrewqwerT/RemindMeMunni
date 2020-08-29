package com.example.remindmemunni.newseries

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.remindmemunni.R
import com.example.remindmemunni.common.RecurrenceSelectFragment
import com.example.remindmemunni.common.UnfilteredArrayAdapter
import com.example.remindmemunni.databinding.FragmentNewSeriesBinding
import com.example.remindmemunni.newitem.NewItemFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class NewSeriesFragment : Fragment()
    , RecurrenceSelectFragment.RecurrenceSelectListener {

    private var binding: FragmentNewSeriesBinding? = null
    private val viewModel: NewSeriesViewModel by viewModels {
        InjectorUtils.provideNewSeriesViewModelFactory(requireContext(), seriesId)
    }

    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        seriesId = arguments?.getInt(EXTRA_SERIES_ID, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewSeriesBinding.inflate(inflater, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = "New Series"
        if (seriesId != 0) activity?.title = "Edit Series"

        val typeSpinner = view.findViewById<AutoCompleteTextView>(R.id.cost_type_dropdown)
        val typeSpinnerAdapter = UnfilteredArrayAdapter.createFromResource(
            requireContext(), R.array.cost_types_array, R.layout.dropdown_menu_popup_item
        )
        typeSpinner.setAdapter(typeSpinnerAdapter)
        typeSpinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.setCostType(typeSpinnerAdapter.getItem(position))
        }

        val recurrenceEditText = view.findViewById<TextInputEditText>(R.id.repeat)
        recurrenceEditText.setOnClickListener {
            RecurrenceSelectFragment()
                .show(childFragmentManager, "frequency_dialog")
        }

        val categoryEditText = view.findViewById<AutoCompleteTextView>(R.id.category_input_field)
        val categoryEditTextAdapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_menu_popup_item)
        categoryEditText.setAdapter(categoryEditTextAdapter)
        viewModel.categories.observe(viewLifecycleOwner) {
            categoryEditTextAdapter.clear()
            categoryEditTextAdapter.addAll(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new, menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
        R.id.done_button -> {
            lifecycleScope.launch {
                val newSeriesId = viewModel.createSeries()
                if (newSeriesId == 0) {
                    Toast.makeText(
                        requireContext(),
                        viewModel.validateInput(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setFragmentResult("requestKey", bundleOf(NewItemFragment.RESULT_SUCCESS to true))
                    view?.findNavController()?.popBackStack()

//                    val resultIntent = Intent()
//                    resultIntent.putExtra(EXTRA_SERIES_ID, newSeriesId)
//                    setResult(Activity.RESULT_OK, resultIntent)
//                    finish()
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    override fun onDialogConfirm(dialog: DialogFragment, months: Int, days: Int) {
        viewModel.setRecurrence(months, days)
    }

    companion object {
        const val REQUEST_SUCCESSFUL = "SUCCESSFUL"
        const val RESULT_SUCCESS = "SUCCESS"
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}
