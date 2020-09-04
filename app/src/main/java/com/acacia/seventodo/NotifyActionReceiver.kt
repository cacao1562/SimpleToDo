package com.acacia.seventodo

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.acacia.seventodo.database.TodoDAO
import kotlinx.coroutines.*
import javax.inject.Inject


class NotifyActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var todoDao: TodoDAO

    override fun onReceive(context: Context?, intent: Intent?) {

        TodoApplication.instance.appComponent.inject(this)
        Log.d("kkk", "NotifyActionReceiver onReceive" )

        when(intent?.action) {
            "COMPLETE" -> {
                val id = intent.getIntExtra("ID", 0)

                runBlocking {
                    todoDao.updateCompleted(id, true)
                }
                val s = Context.NOTIFICATION_SERVICE
                val nm = context?.getSystemService(s) as NotificationManager
                nm.cancel(id)
                Log.d("kkk", "NotifyActionReceiver COMPLETE id = $id" )
            }
        }
    }

}