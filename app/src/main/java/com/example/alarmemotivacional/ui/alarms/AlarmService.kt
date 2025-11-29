package com.example.alarmemotivacional.ui.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
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

        if (player == null) {
            val soundUri = intent?.getStringExtra(AlarmScheduler.EXTRA_ALARM_SOUND_URI)
            val parsedUri = soundUri?.let { Uri.parse(it) }

            player = if (parsedUri != null) {
                MediaPlayer.create(this, parsedUri)
            } else {
                MediaPlayer.create(this, R.raw.alarm_sound)
            }?.apply {
                isLooping = true
                start()
            }
        }

        val videoIntent = Intent(this, VideoActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent?.getStringExtra(AlarmScheduler.EXTRA_VIDEO_URI)?.let { putExtra("video_uri", it) }
        }

        val videoPendingIntent = PendingIntent.getActivity(
            this,
            0,
            videoIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle("Alarme tocando")
            .setContentText("Toque para abrir o vídeo motivacional")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentIntent(videoPendingIntent)
            .addAction(
                R.mipmap.ic_launcher,
                "Ver vídeo",
                videoPendingIntent
            )
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        player?.stop()
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarme Motivacional",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}