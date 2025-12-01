package com.example.alarmemotivacional.ui.alarms

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmemotivacional.R

class AlarmRingingActivity : AppCompatActivity() {

    private var videoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_ringing)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        videoUri = intent.getStringExtra(EXTRA_VIDEO_URI) ?: intent.dataString

        val title = findViewById<TextView>(R.id.textAlarmTitle)
        val subtitle = findViewById<TextView>(R.id.textAlarmSubtitle)

        title.setOnClickListener { handleUserInteraction() }
        subtitle.setOnClickListener { handleUserInteraction() }
        findViewById<TextView>(R.id.textAlarmAction).setOnClickListener { handleUserInteraction() }
        findViewById<TextView>(R.id.textAlarmHint).setOnClickListener { handleUserInteraction() }
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.ringingRoot)
            .setOnClickListener { handleUserInteraction() }
    }

    private fun handleUserInteraction() {
        val dismissIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_DISMISS
        }
        startService(dismissIntent)

        videoUri?.let {
            val uri = runCatching { Uri.parse(it) }.getOrNull()
            if (uri != null) {
                val videoIntent = Intent(this, VideoActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = uri
                    putExtra(VideoActivity.EXTRA_VIDEO_URI, it)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(videoIntent)
            }
        }

        finish()
    }

    companion object {
        const val EXTRA_VIDEO_URI = "extra_video_uri"
    }
}
