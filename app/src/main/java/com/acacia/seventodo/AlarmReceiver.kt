package com.acacia.seventodo

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmReceiver : BaseBroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val NOTIFICATION_ID = "AlarmReceiver_NOTIFICATION_ID"
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val TITLE = "AlarmReceiver_Title"
        const val DESCRIPTION = "AlarmReceiver_Description"
    }


    private var mNotiId = 0
    private var mTitle = ""
    private var mDescription = ""

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent : $intent")
        super.onReceive(context, intent)

        mNotiId = intent.getIntExtra(NOTIFICATION_ID, 0)
        mTitle = intent.getStringExtra(TITLE) ?: ""
        mDescription = intent.getStringExtra(DESCRIPTION) ?: ""

        excuteWorker(context)
        deliverNotification(context)
    }

    private fun excuteWorker(context: Context) {

        val workRequest = OneTimeWorkRequestBuilder<PowerWorker>().build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)
    }

    private fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, MainActivity::class.java)
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            mNotiId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val btnIntent = Intent(context, NotifyActionReceiver::class.java)
        btnIntent.action = "COMPLETE"
        btnIntent.putExtra("ID", mNotiId)
        val btnPendingIntent = PendingIntent.getBroadcast(context, 123, btnIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder =
            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_seven_todo_noti)
                .setColor(context.resources.getColor(R.color.colorNoti, null))
                .setContentTitle(mTitle)
                .setContentText(mDescription)
                .setContentIntent(contentPendingIntent)
                .addAction(android.R.drawable.ic_notification_clear_all, "완료", btnPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(mNotiId, builder.build())
    }

}