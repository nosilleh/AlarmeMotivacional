package com.example.alarmemotivacional.ui.alarms

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmemotivacional.R

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoView = findViewById<VideoView>(R.id.videoView)

        // Recuperando o vídeo passado pela AddAlarmActivity (em breve implementaremos isso)
        val videoUri = intent.getStringExtra("video_uri")

        if (videoUri != null) {
            videoView.setVideoURI(Uri.parse(videoUri))
            videoView.start()
        }
    }
}