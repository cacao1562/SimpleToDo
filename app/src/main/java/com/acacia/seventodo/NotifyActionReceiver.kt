package com.acacia.seventodo

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.acacia.seventodo.database.TodoDAO
import com.acacia.seventodo.database.TodoEntity
import com.acacia.seventodo.utils.getStringDate
import kotlinx.coroutines.*
import javax.inject.Inject


class NotifyActionReceiver : BaseBroadcastReceiver() {

    @Inject
    lateinit var todoDao: TodoDAO

    private val notiBarID = 9999

    override fun onReceive(context: Context, intent: Intent) {

        super.onReceive(context, intent)
        TodoApplication.instance.appComponent.inject(this)
        Log.d("kkk", "NotifyActionReceiver onReceive" )

        when(intent.action) {
            "COMPLETE" -> {
                val id = intent.getIntExtra("ID", 0)

                runBlocking {
                    todoDao.updateCompleted(id, true)
                }
                val s = Context.NOTIFICATION_SERVICE
                val nm = context.getSystemService(s) as NotificationManager
                nm.cancel(id)
                Log.d("kkk", "NotifyActionReceiver COMPLETE id = $id" )
            }
            "REFRESH" -> {
                Log.d("kkk", "NotifyActionReceiver REFRESH " )
                GlobalScope.launch {
                    val todayList = todoDao.getTaskByDate(getStringDate(0))
                    deliverNotification(context, todayList ?: emptyList())
                }

            }
            "CLOSE" -> {
                val id = intent.getIntExtra("ID", 0)
                val s = Context.NOTIFICATION_SERVICE
                val nm = context.getSystemService(s) as NotificationManager
                nm.cancel(id)
                Log.d("kkk", "NotifyActionReceiver CLOSE id = $id" )
            }
        }
    }


    private fun deliverNotification(context: Context, todolist: List<TodoEntity>) {
        val contentIntent = Intent(context, MainActivity::class.java)
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            123,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val refreshIntent = Intent(context, NotifyActionReceiver::class.java)
        refreshIntent.action = "REFRESH"
        val refreshPendingIntent = PendingIntent.getBroadcast(context, 123, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val closeIntent = Intent(context, NotifyActionReceiver::class.java)
        closeIntent.action = "CLOSE"
        closeIntent.putExtra("ID", notiBarID)
        val closePendingIntent = PendingIntent.getBroadcast(context, 123, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val remoteViews = RemoteViews(context.packageName, R.layout.item_noti_bar)
        remoteViews.setTextViewText(R.id.notiBar_tv_today, "오늘\n할일\n" + "${todolist.size} 개")

        remoteViews.setTextViewText(R.id.notiBar_tv_todo01, todolist.getOrNull(0)?.title)
        remoteViews.setTextViewText(R.id.notiBar_tv_todo02, todolist.getOrNull(1)?.title)
        remoteViews.setTextViewText(R.id.notiBar_tv_todo03, todolist.getOrNull(2)?.title)
        remoteViews.setTextViewText(R.id.notiBar_tv_todo04, todolist.getOrNull(3)?.title)
        remoteViews.setTextViewText(R.id.notiBar_tv_todo05, todolist.getOrNull(4)?.title)
        remoteViews.setTextViewText(R.id.notiBar_tv_todo06, todolist.getOrNull(5)?.title)


        remoteViews.setOnClickPendingIntent(R.id.notiBar_btn_refresh, refreshPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.notiBar_btn_close, closePendingIntent)

        val builder =
            NotificationCompat.Builder(context, AlarmReceiver.PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_seven_todo_noti)
//                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setColor(context.resources.getColor(R.color.colorNoti, null))
                .setCustomContentView(remoteViews)
//                .setCustomBigContentView(remoteViews)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOngoing(true)

        notificationManager.notify(notiBarID, builder.build())
    }

}