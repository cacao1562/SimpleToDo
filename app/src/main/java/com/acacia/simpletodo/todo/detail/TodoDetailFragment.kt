package com.acacia.simpletodo.todo.detail

import android.app.AlarmManager
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
import com.acacia.simpletodo.utils.getAlarmManager
import com.acacia.simpletodo.utils.getPendingIntent
import com.acacia.simpletodo.viewmodel.TodoDetailViewModel
import javax.inject.Inject
import kotlin.math.abs

class TodoDetailFragment : Fragment(),
    DatePickerDialog.OnDateSelectedListener {

    private val appComponent: TodoComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as TodoApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentTodoDetailBinding

    private val viewModel by viewModels<TodoDetailViewModel> { viewModelFactory }

    private val args: TodoDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
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

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.loadTodo(args.todoId)

        binding.todoDetailRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.todoDetail_radio_01 -> { viewModel.setSelectedDay(0) }
                R.id.todoDetail_radio_02 -> { viewModel.setSelectedDay(1) }
                R.id.todoDetail_radio_03 -> { viewModel.setSelectedDay(2) }
                R.id.todoDetail_radio_04 -> { viewModel.setSelectedDay(3) }
                R.id.todoDetail_radio_05 -> { viewModel.setSelectedDay(4) }
                R.id.todoDetail_radio_06 -> { viewModel.setSelectedDay(5) }
                R.id.todoDetail_radio_07 -> { viewModel.setSelectedDay(6) }
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

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer { isUpdated ->
            if (isUpdated) {
                setAlarm()
                findNavController().popBackStack()
            }
        })

    }

    /**
     * 알림 등록
     */
    private fun setAlarm() {

        if (viewModel.isChecked.value == false) return

        val alarmManager = getAlarmManager()
        val pendingIntent = getPendingIntent(viewModel.taskId,
                                       viewModel.title.value ?: "",
                                       viewModel.description.value ?: "")
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, viewModel.selectedCalendar.timeInMillis, pendingIntent)
    }

    /**
     * DateDialog에서 설정한 알림 시간 넘겨 받음
     */
    override fun onResult(cal: java.util.Calendar) {
        viewModel.setNotiView(cal)
        Log.d("ddd", "onResult = $cal")
    }
}