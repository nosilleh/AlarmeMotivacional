package com.example.alarmemotivacional.ui.alarms

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.alarmemotivacional.R

class AlarmService : Service() {

    private var player: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (player == null) {
            player = MediaPlayer.create(this, R.raw.alarm_sound).apply {
                isLooping = true
                start()
            }
        }

        val notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle("Alarme tocando")
            .setContentText("Toque para abrir o vídeo motivacional")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
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
}