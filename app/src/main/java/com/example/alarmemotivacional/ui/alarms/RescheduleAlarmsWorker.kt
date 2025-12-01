package com.example.alarmemotivacional.ui.alarms

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class RescheduleAlarmsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val storage = AlarmStorage(applicationContext)
        val scheduler = AlarmScheduler(applicationContext)

        storage.getAlarmes()
            .filter { it.isActive }
            .forEach { scheduler.ligarAlarme(it) }

        return Result.success()
    }
}
