package com.example.alarmemotivacional.ui.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    fun ligarAlarme(hora: Int, minuto: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = criarPendingIntent(hora, minuto)

        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)

            // se horário já passou hoje → agenda para amanhã
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendario.timeInMillis,
            pendingIntent
        )
    }

    fun desligarAlarme(hora: Int, minuto: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = criarPendingIntent(hora, minuto)

        alarmManager.cancel(pendingIntent)
    }

    private fun criarPendingIntent(hora: Int, minuto: Int): PendingIntent {
        val intent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START_ALARM
            putExtra(AlarmService.EXTRA_REQUEST_CODE, hora * 100 + minuto)
        }

        val requestCode = hora * 100 + minuto
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(context, requestCode, intent, flags)
        } else {
            PendingIntent.getService(context, requestCode, intent, flags)
        }
    }
}
