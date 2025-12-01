package com.example.alarmemotivacional.ui.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.alarmemotivacional.R
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    fun ligarAlarme(alarme: AlarmData): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                R.string.exact_alarm_permission_missing,
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        val pendingIntent = criarPendingIntent(alarme)

        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarme.hour)
            set(Calendar.MINUTE, alarme.minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendario.timeInMillis,
            pendingIntent
        )

        return true
    }

    fun desligarAlarme(alarme: AlarmData) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = criarPendingIntent(alarme)

        alarmManager.cancel(pendingIntent)
    }

    private fun criarPendingIntent(alarme: AlarmData): PendingIntent {
        val requestCode = buildRequestCode(alarme.id)

        val intent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START_ALARM
            putExtra(AlarmService.EXTRA_REQUEST_CODE, requestCode)
            putExtra(AlarmService.EXTRA_SOUND_URI, alarme.soundUri)
            putExtra(AlarmService.EXTRA_ALARM_ID, alarme.id)
            putExtra(AlarmService.EXTRA_VIDEO_URI, alarme.videoUri)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(context, requestCode, intent, flags)
        } else {
            PendingIntent.getService(context, requestCode, intent, flags)
        }
    }
    companion object {
        fun buildRequestCode(alarmId: Long): Int = (alarmId % Int.MAX_VALUE).toInt()
    }
}
