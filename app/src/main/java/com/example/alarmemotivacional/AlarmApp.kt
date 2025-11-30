package com.example.alarmemotivacional

import android.app.Application
import com.example.alarmemotivacional.ui.alarms.NotificationHelper

class AlarmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.criarCanal(this)
    }
}
