package com.example.alarmemotivacional.ui.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "⏰ Alarme disparou!", Toast.LENGTH_LONG).show()

        val soundUri = intent?.getStringExtra(AlarmScheduler.EXTRA_ALARM_SOUND_URI)
        val hora = intent?.getStringExtra(AlarmScheduler.EXTRA_ALARM_TIME)
        val videoUri = intent?.getStringExtra(AlarmScheduler.EXTRA_VIDEO_URI)

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_ALARM_SOUND_URI, soundUri)
            putExtra(AlarmScheduler.EXTRA_ALARM_TIME, hora)
            putExtra(AlarmScheduler.EXTRA_VIDEO_URI, videoUri)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        if (!videoUri.isNullOrEmpty()) {
            val videoIntent = Intent(context, VideoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("video_uri", videoUri)
            }
            context.startActivity(videoIntent)
        }
    }
}