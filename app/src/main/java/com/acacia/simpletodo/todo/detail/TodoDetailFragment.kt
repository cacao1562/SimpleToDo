package com.acacia.simpletodo.todo.detail

import android.app.AlarmManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.acacia.simpletodo.todo.datedialog.DatePickerDialog
import com.acacia.simpletodo.R
import com.acacia.simpletodo.TodoApplication
import com.acacia.simpletodo.databinding.FragmentTodoDetailBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.todo.dialog.CustomDialog
import com.acacia.simpletodo.utils.cancleAlarm
import com.acacia.simpletodo.utils.dpToPx
import com.acacia.simpletodo.utils.getAlarmManager
import com.acacia.simpletodo.utils.getPendingIntent
import com.acacia.simpletodo.viewmodel.TodoDetailViewModel
import java.util.*
import javax.inject.Inject

class TodoDetailFragment : Fragment(),
    DatePickerDialog.OnDateSelectedListener,
    CustomDialog.OnDialogBtnResult{


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentTodoDetailBinding

    private val viewModel by viewModels<TodoDetailViewModel> { viewModelFactory }

    private val args: TodoDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TodoApplication.instance.appComponent.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().window.statusBarColor = Color.TRANSPARENT

        val h = requireActivity().window.decorView.height
        val w = requireActivity().window.decorView.width

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

        if (resourceId > 0) {

            binding.detailAppbarLayout.layoutParams.height = resources.getDimensionPixelSize(resourceId) + 55.dpToPx(requireContext())

            Log.d("uuu", "h = $h")
            Log.d("uuu", "w = $w")
            Log.d("uuu", "st = ${resources.getDimensionPixelSize(resourceId)}")
            Log.d("uuu", "24 = ${24.dpToPx(requireContext())}")
        }



        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.loadTodo(args.todoId)

        binding.todoDetailRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.mainFm_radio_01 -> { viewModel.setSelectedDay(0) }
                R.id.mainFm_radio_02 -> { viewModel.setSelectedDay(1) }
                R.id.mainFm_radio_03 -> { viewModel.setSelectedDay(2) }
                R.id.mainFm_radio_04 -> { viewModel.setSelectedDay(3) }
                R.id.mainFm_radio_05 -> { viewModel.setSelectedDay(4) }
                R.id.mainFm_radio_06 -> { viewModel.setSelectedDay(5) }
                R.id.mainFm_radio_07 -> { viewModel.setSelectedDay(6) }
            }
        }

        binding.todoDetailSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.isChecked.value = isChecked
        }

        binding.todoDetailNotiLinear.setOnClickListener {
            if (viewModel.isChecked.value!!) {

                val dialog = DatePickerDialog(
                    viewModel.selectedCalendar,
                    this
                )
                dialog.show(activity?.supportFragmentManager!!, dialog.tag)
            }
        }

        binding.todoDetailBtnBack.setOnClickListener {
            if (viewModel.isModifyTodo() == false) {
            }else {
                findNavController().popBackStack()
            }
        }

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer { isUpdated ->
            if (isUpdated) {
                registerAlarm()
                findNavController().popBackStack()
            }
        })

        viewModel.isChecked.observe(viewLifecycleOwner, Observer {
            binding.todoDetailSwitch.isChecked = it
            if (it == false) {
                cancleAlarm(viewModel.todoId.value!!)
            }
        })

    }

    /**
     * 알림 등록
     */
    private fun registerAlarm() {

        if (viewModel.isChecked.value == false) return

        val alarmManager = getAlarmManager()
        val pendingIntent = getPendingIntent(viewModel.todoId.value!!,
                                       viewModel.todoTitle.value ?: "",
                                       viewModel.todoDescription.value ?: "")

        viewModel.selectedCalendar.set(Calendar.SECOND, 10)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, viewModel.selectedCalendar.timeInMillis, pendingIntent)
    }



    /**
     * DateDialog에서 설정한 알림 시간 넘겨 받음
     */
    override fun onResult(cal: Calendar) {
        viewModel.setNotiView(cal)
        Log.d("ddd", "onResult = $cal")
    }

    override fun onClickResult(isSaved: Boolean, type: TodoNewDetailFragment.DialogType) {
    }

    fun onBackPressed(): Boolean {
        if (viewModel.isModifyTodo() == false) {
            // show alert
            return false
        }
        return true
    }


}