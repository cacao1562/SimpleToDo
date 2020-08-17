package com.acacia.simpletodo.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.acacia.simpletodo.AlarmReceiver

fun Fragment.getAlarmManager(): AlarmManager {
    return requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

fun Fragment.getPendingIntent(notiId: Int, title: String, description: String = ""): PendingIntent {
    val intent = Intent(requireContext(), AlarmReceiver::class.java)
    intent.putExtra(AlarmReceiver.NOTIFICATION_ID, notiId)
    intent.putExtra(AlarmReceiver.TITLE, title)
    intent.putExtra(AlarmReceiver.DESCRIPTION, description)
    return PendingIntent.getBroadcast(
        requireContext(), notiId, intent,
        PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.getDeviceWidth(): Int {
    return resources.displayMetrics.widthPixels
}

fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()