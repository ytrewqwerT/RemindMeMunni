package com.example.remindmemunni.destinations.newitem

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.remindmemunni.R
import com.example.remindmemunni.common.DatePickerFragment
import com.example.remindmemunni.common.TimePickerFragment
import com.example.remindmemunni.common.UnfilteredArrayAdapter
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.Series
import com.example.remindmemunni.databinding.FragmentNewItemBinding
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.utils.PrimitiveDateTime
import kotlinx.coroutines.launch

class NewItemFragment : Fragment()
    , TimePickerDialog.OnTimeSetListener
    , DatePickerDialog.OnDateSetListener {

    private var binding: FragmentNewItemBinding? = null
    private val viewModel: NewItemViewModel by viewModels {
        InjectorUtils.provideNewItemViewModelFactory(requireContext(), templateItem, isItemEdit)
    }

    private val time = PrimitiveDateTime()

    private lateinit var templateItem: Item
    private var isItemEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        templateItem = arguments?.getParcelable(EXTRA_ITEM_DATA) ?: Item()
        isItemEdit = templateItem.id != 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewItemBinding.inflate(inflater, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = if (isItemEdit) "Edit Item" else "New Item"

        val typeSpinner = view.findViewById<AutoCompleteTextView>(R.id.cost_type_dropdown)
        val typeSpinnerAdapter = UnfilteredArrayAdapter.createFromResource(
            requireContext(), R.array.cost_types_array, R.layout.dropdown_menu_popup_item
        )
        typeSpinner.setAdapter(typeSpinnerAdapter)
        typeSpinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.setCostType(typeSpinnerAdapter.getItem(position))
        }

        val timeEditText = view.findViewById<EditText>(R.id.time_input_field)
        timeEditText.setOnClickListener {
            DatePickerFragment().show(childFragmentManager, "date_dialog")
        }

        val seriesSpinner = view.findViewById<AutoCompleteTextView>(R.id.series_dropdown)
        val seriesSpinnerAdapter = UnfilteredArrayAdapter<AggregatedSeries>(
            requireContext(), R.layout.dropdown_menu_popup_item, ArrayList()
        )
        val dummySeries = AggregatedSeries(Series(), emptyList()) // For no series selected option
        seriesSpinner.setAdapter(seriesSpinnerAdapter)
        seriesSpinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.setSeries(seriesSpinnerAdapter.getItem(position))
        }
        viewModel.allSeries.observe(viewLifecycleOwner) {series ->
            seriesSpinnerAdapter.clear()
            seriesSpinnerAdapter.add(dummySeries)
            seriesSpinnerAdapter.addAll(series)
        }

        val checkBox = view.findViewById<CheckBox>(R.id.series_increment)
        viewModel.series.observe(viewLifecycleOwner) {
            checkBox.isEnabled = it.isNotEmpty()
            checkBox.isChecked = it.isNotEmpty()
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

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean  = when (menuItem.itemId) {
        R.id.done_button -> {
            lifecycleScope.launch {
                val itemCreationResult = viewModel.createItem()
                if (itemCreationResult != null) {
                    Toast.makeText(
                        requireContext(),
                        itemCreationResult,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setFragmentResult(REQUEST_RESULT, bundleOf(RESULT_SUCCESS to true))
                    view?.findNavController()?.popBackStack()
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        time.mYear = year
        time.mMonth = month + 1 // DatePicker returns month as 0-11 instead of 1-12
        time.mDayOfMonth = dayOfMonth
        val timeDialog =
            TimePickerFragment()
        timeDialog.show(childFragmentManager, "time_dialog")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        time.mHour = hourOfDay
        time.mMinute = minute
        viewModel.setTime(time)
    }

    companion object {
        const val REQUEST_RESULT = "NEW_ITEM_FRAGMENT_REQUEST_RESULT"
        const val RESULT_SUCCESS = "SUCCESS"
        const val EXTRA_ITEM_DATA = "ITEM_DATA"
    }
}