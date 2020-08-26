package com.acacia.simpletodo.todo.detail

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
import com.acacia.simpletodo.databinding.FragmentTodoDetailBinding
import com.acacia.simpletodo.databinding.FragmentTodoNewDetailBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.todo.datedialog.DatePickerDialog
import com.acacia.simpletodo.todo.dialog.SaveDialog
import com.acacia.simpletodo.utils.dpToPx
import com.acacia.simpletodo.utils.getTodoDay
import com.acacia.simpletodo.viewmodel.TodoDetailViewModel
import java.util.*
import javax.inject.Inject
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

class TodoNewDetailFragment: Fragment(),
    DatePickerDialog.OnDateSelectedListener,
    SaveDialog.OnSaveListener {

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

        viewModel.selectedDay.observe(viewLifecycleOwner, Observer {
            dayAdapter.notifyItemChanged(viewModel.beforeSelectedDay)
            dayAdapter.notifyItemChanged(it)
            viewModel.beforeSelectedDay = it
            binding.todoDetailRvDay.smoothScrollToPosition(it)
        })
    }

    /**
     * DateDialog에서 설정한 알림 시간 넘겨 받음
     */
    override fun onResult(cal: Calendar) {
        viewModel.setNotiView(cal)
        Log.d("ddd", "onResult = $cal")
    }

    override fun onSaved(isSaved: Boolean) {
        if (isSaved) {
            viewModel.updateTodo()
        }else {
            findNavController().popBackStack()
        }
    }

    fun onBackPressed(): Boolean {
        if (viewModel.isModifyTodo() == false) {
            // show alert
            SaveDialog(requireContext(), this).show()
            return false
        }
        return true
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