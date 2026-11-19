package com.example.system

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ApexOperationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        ApexNotificationScheduler.syncScheduledRemindersIfArmed(applicationContext)
        return Result.success()
    }
}
