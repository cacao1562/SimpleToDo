package com.acacia.simpletodo.todo.detail

import android.app.AlarmManager
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.acacia.simpletodo.TodoApplication
import com.acacia.simpletodo.databinding.FragmentTodoNewDetailBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.todo.datedialog.DatePickerDialog
import com.acacia.simpletodo.todo.dialog.CustomDialog
import com.acacia.simpletodo.viewmodel.TodoDetailViewModel
import java.util.*
import javax.inject.Inject
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.acacia.simpletodo.R
import com.acacia.simpletodo.utils.*

class TodoNewDetailFragment: Fragment(),
    DatePickerDialog.OnDateSelectedListener,
    CustomDialog.OnDialogBtnResult {

    enum class DialogType {
        DialogType_Save,
        DialogType_Delete
    }

    private val appComponent: TodoComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as TodoApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TodoDetailViewModel> { viewModelFactory }

    private val args: TodoDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentTodoNewDetailBinding

    private lateinit var dayAdapter: TodoDayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoNewDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().window.statusBarColor = Color.TRANSPARENT

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            binding.detailAppbarLayout.layoutParams.height = resources.getDimensionPixelSize(resourceId) + 55.dpToPx(requireContext())
        }

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        val vm = binding.viewmodel
        if (vm != null) {
            dayAdapter = TodoDayAdapter(vm, getTodoDay())
            binding.todoDetailRvDay.setHasFixedSize(true)
            binding.todoDetailRvDay.addItemDecoration(SlideItemDecoration(20.dpToPx(requireContext()), 6))
            binding.todoDetailRvDay.adapter = dayAdapter
        }

        viewModel.loadTodo(args.todoId)

        initBinds()
        initObserves()

    }

    private fun initBinds() {
        binding.todoDetailBtnBack.setOnClickListener {
            if (viewModel.isModifyTodo() == false) {
                showDialog(
                    DialogType.DialogType_Save,
                    resources.getString(R.string.dialog_save_title),
                    resources.getString(R.string.dialog_save_cancle),
                    resources.getString(R.string.dialog_save_confirm)
                )
            }else {
                findNavController().popBackStack()
            }
        }

        binding.todoDetailLinearNoti.setOnClickListener {
            if (viewModel.isChecked.value!!) {

                val dialog = DatePickerDialog(
                    viewModel.selectedCalendar,
                    this
                )
                dialog.show(activity?.supportFragmentManager!!, dialog.tag)
            }
        }

        binding.todoDetailBtnDelete.setOnClickListener {
            showDialog(
                DialogType.DialogType_Delete,
                resources.getString(R.string.dialog_delete_title),
                resources.getString(R.string.dialog_delete_cancle),
                resources.getString(R.string.dialog_delete_confirm)
            )
        }
    }

    private fun initObserves() {
        viewModel.selectedDayIndex.observe(viewLifecycleOwner, Observer {
            dayAdapter.notifyItemChanged(viewModel.beforeSelectedDay)
            dayAdapter.notifyItemChanged(it)
            viewModel.beforeSelectedDay = it
            binding.todoDetailRvDay.smoothScrollToPosition(it)
        })

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer { isUpdated ->
            if (isUpdated) {
                registerAlarm()
                findNavController().popBackStack()
            }
        })

        viewModel.isChecked.observe(viewLifecycleOwner, Observer {
            binding.todoDetailSwitchNoti.isChecked = it
            if (it == false) {
                cancleAlarm(viewModel.todoId.value!!)
            }
        })
        viewModel.errorTitle.observe(viewLifecycleOwner, Observer {
            binding.todoDetailTextlayoutTitle.error = it
        })

        viewModel.todoTitle.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) binding.todoDetailTextlayoutTitle.error = null
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

    /**
     * 변경사항 있을때 나타나는 팝업
     * 저장 안함, 저장 버튼 false, true로 리턴
     */
    override fun onClickResult(isSaved: Boolean, type: DialogType) {
        when(type) {
            DialogType.DialogType_Save -> {
                if (isSaved) {
                    viewModel.updateTodo()
                }else {
                    findNavController().popBackStack()
                }
            }
            DialogType.DialogType_Delete -> {
                if (isSaved) {
                    viewModel.deleteTodo {
                        findNavController().popBackStack()
                    }
                }
            }
        }

    }

    fun onBackPressed(): Boolean {
        if (viewModel.isModifyTodo() == false) {
            // show alert
            showDialog(
                DialogType.DialogType_Save,
                resources.getString(R.string.dialog_save_title),
                resources.getString(R.string.dialog_save_cancle),
                resources.getString(R.string.dialog_save_confirm)
            )
            return false
        }
        return true
    }


    private fun showDialog(type: DialogType, title: String, cancle: String, confirm: String) {
        CustomDialog(requireContext(),
            type,
            title,
            cancle,
            confirm,
            this).show()
    }

}

class SlideItemDecoration(private val spaceLeft: Int, private val lastPos: Int ): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        if (position == 0) {
            outRect.left = spaceLeft * 2
            return
        }
        if (position == lastPos) {
            outRect.left = spaceLeft
            outRect.right = spaceLeft * 2
            return
        }
        outRect.left = spaceLeft
    }
}