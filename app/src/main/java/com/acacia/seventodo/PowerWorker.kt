package com.acacia.seventodo

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class PowerWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @SuppressLint("InvalidWakeLockTag")
    override fun doWork(): Result {
        val mPower: PowerManager?
        val wakeLock: PowerManager.WakeLock
        mPower = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = mPower.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "WAKELOCK")
        wakeLock.acquire() // WakeLock 깨우기
        wakeLock.release()
        return Result.success()
    }

}