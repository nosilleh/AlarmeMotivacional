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

        val videoUri = intent.getStringExtra(EXTRA_VIDEO_URI) ?: intent.dataString

        if (videoUri != null) {
            videoView.setVideoURI(Uri.parse(videoUri))
            videoView.start()
        }
    }

    companion object {
        const val EXTRA_VIDEO_URI = "video_uri"
    }
}