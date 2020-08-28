package com.acacia.simpletodo.todo.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import com.acacia.simpletodo.R
import com.acacia.simpletodo.todo.detail.TodoNewDetailFragment
import kotlinx.android.synthetic.main.dialog_custom.*


class CustomDialog (context: Context,
                    private val type: TodoNewDetailFragment.DialogType,
                    private val title: String,
                    private val cancleBtn: String,
                    private val confirmBtn: String,
                    private val callback: OnDialogBtnResult) : Dialog(context, R.style.CustomDialog) {

    interface OnDialogBtnResult {
        fun onClickResult(isSaved: Boolean, type: TodoNewDetailFragment.DialogType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.5f
        window!!.attributes = lpWindow
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_custom)

        customDialog_tv_title.text = title
        customDialog_btn_cancle.text = cancleBtn
        customDialog_btn_confirm.text = confirmBtn

        customDialog_btn_cancle.setOnClickListener {
            dismiss()
            callback.onClickResult(false, type)
        }
        customDialog_btn_confirm.setOnClickListener {
            dismiss()
            callback.onClickResult(true, type)
        }

    }

}