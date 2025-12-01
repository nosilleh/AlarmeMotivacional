package com.example.alarmemotivacional.ui.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isBootCompleted = intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED

        if (!isBootCompleted) return

        val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>()
            .setInitialDelay(INITIAL_DELAY_SECONDS, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "reschedule_alarms_after_boot"
        private const val INITIAL_DELAY_SECONDS = 5L
    }
}