package com.acacia.simpletodo.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.acacia.simpletodo.R
import com.acacia.simpletodo.TodoApplication

object ToastHelper {

    private var mTaost: Toast? = null

    fun showToast(msg: String) {

        mTaost?.let {
            it.cancel()
        }
        val li =
            TodoApplication.instance.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.layout_custom_toast, null)
        val textview = view.findViewById<TextView>(R.id.layout_custom_toast_textView)
        textview.text = msg
        val toast = Toast(TodoApplication.instance.applicationContext)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()

        mTaost = toast
    }

}