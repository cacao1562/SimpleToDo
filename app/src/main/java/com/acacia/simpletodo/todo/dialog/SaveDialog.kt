package com.acacia.simpletodo.todo.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.acacia.simpletodo.databinding.DialogSaveBinding
import com.acacia.simpletodo.utils.dpToPx
import com.acacia.simpletodo.utils.getDeviceWidth


class SaveDialog (context: Context, private val callback: OnSaveListener) : Dialog(context) {

    interface OnSaveListener {
        fun onSaved(isSaved: Boolean)
    }

    lateinit var binding: DialogSaveBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.5f
        window!!.attributes = lpWindow
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogSaveBinding.inflate(LayoutInflater.from(context), null, false)

        setContentView(binding.root)

        binding.root.layoutParams.width = (context.getDeviceWidth() * 0.9).toInt()
        binding.root.layoutParams.height = 200.dpToPx(context)

        binding.saveDialogBtnCancle.setOnClickListener {
            dismiss()
            callback.onSaved(false)
        }

        binding.saveDialogBtnSave.setOnClickListener {
            dismiss()
            callback.onSaved(true)
        }
    }

}