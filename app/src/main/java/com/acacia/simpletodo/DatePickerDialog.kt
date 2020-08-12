package com.acacia.simpletodo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.acacia.simpletodo.databinding.DialogDatePickerBinding
import com.acacia.simpletodo.viewmodel.TodoDateViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class DatePickerDialog(private val cal: Calendar?,
                       private val callback: OnDateSelectedListener) : BottomSheetDialogFragment() {

    interface OnDateSelectedListener {
        fun onResult(date: DateSelected)
    }

    data class DateSelected(
        val day: String,
        val hour: String,
        val min: String
    )

    private val viewModel by viewModels<TodoDateViewModel>()

    private lateinit var binding: DialogDatePickerBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(
            recyclerView: RecyclerView,
            newState: Int
        ) {
            if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                val lm = (recyclerView.layoutManager as LinearLayoutManager)
                when(recyclerView.id) {
                    binding.datePickerRvHour.id -> {
                        Log.d("vvv", "rv hour  = ${lm.findFirstVisibleItemPosition()} ")
                        viewModel.setSelectedHour(lm.findFirstVisibleItemPosition())
                    }
                    binding.datePickerRvMin.id -> {
                        Log.d("vvv", "rv min   = ${lm.findFirstVisibleItemPosition()} ")
                        viewModel.setSelectedMin(lm.findFirstVisibleItemPosition())
                    }
                }
            }
        }
    }

    private val touchListener = OnTouchListener { v, event ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        v.onTouchEvent(event)
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogDatePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        viewModel.init(cal)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
//        bottomSheetBehavior.isHideable = false


        binding.datePickerRvHour.isNestedScrollingEnabled = true
        binding.datePickerRvHour.setHasFixedSize(true)
        binding.datePickerRvMin.isNestedScrollingEnabled = true
        binding.datePickerRvMin.setHasFixedSize(true)

        val snap01 = LinearSnapHelper()
        val snap02 = LinearSnapHelper()
        snap01.attachToRecyclerView(binding.datePickerRvHour)
        snap02.attachToRecyclerView(binding.datePickerRvMin)

        binding.datePickerRvHour.addOnScrollListener(scrollListener)
        binding.datePickerRvMin.addOnScrollListener(scrollListener)


        binding.datePickerRvHour.setOnTouchListener(touchListener)
        binding.datePickerRvMin.setOnTouchListener(touchListener)

        binding.datePickerBtnSave.setOnClickListener {
            dismiss()
            callback.onResult(DateSelected("11","13","17"))
        }

        binding.datePickerRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.datePicker_radio_01 -> { viewModel.setSelectedDay(0) }
                R.id.datePicker_radio_02 -> { viewModel.setSelectedDay(1) }
                R.id.datePicker_radio_03 -> { viewModel.setSelectedDay(2) }
                R.id.datePicker_radio_04 -> { viewModel.setSelectedDay(3) }
                R.id.datePicker_radio_05 -> { viewModel.setSelectedDay(4) }
                R.id.datePicker_radio_06 -> { viewModel.setSelectedDay(5) }
                R.id.datePicker_radio_07 -> { viewModel.setSelectedDay(6) }
            }
        }

        viewModel.selectedHour.observe(viewLifecycleOwner, Observer {
            val pos = viewModel.getHour()
            Log.d("eee", "getHour = $pos")
            binding.datePickerRvHour.smoothScrollToPosition(pos)
        })

        viewModel.selectedMin.observe(viewLifecycleOwner, Observer {
            val pos = viewModel.getMin()
            Log.d("eee", "getMin = $pos")
            binding.datePickerRvMin.smoothScrollToPosition(pos)
        })
    }


    override fun onStart() {
        super.onStart()

        dialog?.also {
//            it.window?.setDimAmount(0F)
//            val bottomSheet = it.findViewById<View>(R.id.datePicker_constraintLayout)
//            bottomSheet.layoutParams.height = NsApp.instance.getDeviceHeight() - 83.dpToPx()
//            bottomSheet.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_dialog_border)
//            binding.datePickerConstraintLayout.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_dialog_border)
//            binding.root.requestLayout()
//            val behavior = BottomSheetBehavior.from<View>(bottomSheet)
//            behavior.peekHeight = resources.displayMetrics.heightPixels
//            view?.requestLayout()
        }

    }
}