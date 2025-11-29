package com.example.alarmemotivacional.ui.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    fun ligarAlarme(hora: Int, minuto: Int, somUri: Uri?) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val horaFormatada = String.format("%02d:%02d", hora, minuto)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_TIME, horaFormatada)
            putExtra(EXTRA_ALARM_SOUND_URI, somUri?.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hora * 100 + minuto,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hora * 100 + minuto,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val EXTRA_ALARM_SOUND_URI = "extra_alarm_sound_uri"
        const val EXTRA_ALARM_TIME = "extra_alarm_time"
        const val EXTRA_VIDEO_URI = "extra_video_uri"
    }
}