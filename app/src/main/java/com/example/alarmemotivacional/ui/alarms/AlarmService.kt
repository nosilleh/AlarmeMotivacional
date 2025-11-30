package com.example.alarmemotivacional.ui.alarms

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.alarmemotivacional.R

class AlarmService : Service() {

    private var player: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_DISMISS -> {
                stopAlarm()
                stopForeground(true)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startAlarm()
                val notification = buildAlarmNotification()
                startForeground(NOTIFICATION_ID, notification)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startAlarm() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.alarm_sound).apply {
                isLooping = true
                start()
            }
        }
    }

    private fun stopAlarm() {
        player?.stop()
        player?.release()
        player = null
    }

    private fun buildAlarmNotification(): Notification {
        val openAppIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(this, AlarmService::class.java).apply { action = ACTION_DISMISS }
        val dismissPendingIntent = PendingIntent.getService(
            this,
            DISMISS_REQUEST_CODE,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            FULL_SCREEN_REQUEST_CODE,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle(getString(R.string.alarm_notification_title))
            .setContentText(getString(R.string.alarm_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openAppPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                getString(R.string.alarm_notification_dismiss),
                dismissPendingIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationHelper.CHANNEL_ID,
                NotificationHelper.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NotificationHelper.CHANNEL_DESC
                enableVibration(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_START_ALARM = "com.example.alarmemotivacional.action.START_ALARM"
        const val ACTION_DISMISS = "com.example.alarmemotivacional.action.DISMISS_ALARM"
        const val EXTRA_REQUEST_CODE = "extra_request_code"

        private const val NOTIFICATION_ID = 1
        private const val DISMISS_REQUEST_CODE = 1001
        private const val FULL_SCREEN_REQUEST_CODE = 1002
    }
}
