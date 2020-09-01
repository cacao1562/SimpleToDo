package com.acacia.simpletodo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val NOTIFICATION_ID = "AlarmReceiver_NOTIFICATION_ID"
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val TITLE = "AlarmReceiver_Title"
        const val DESCRIPTION = "AlarmReceiver_Description"
    }

    lateinit var notificationManager: NotificationManager

    private var mNotiId = 0
    private var mTitle = ""
    private var mDescription = ""

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent : $intent")
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotiId = intent.getIntExtra(NOTIFICATION_ID, 0)
        mTitle = intent.getStringExtra(TITLE) ?: ""
        mDescription = intent.getStringExtra(DESCRIPTION) ?: ""

        createNotificationChannel()
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
//                .setSmallIcon(R.drawable.ic_alarm)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .setContentTitle(mTitle)
                .setContentText(mDescription)
                .setContentIntent(contentPendingIntent)
                .addAction(android.R.drawable.ic_notification_clear_all, "완료", btnPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(mNotiId, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Stand up notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "AlarmManager Tests"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}