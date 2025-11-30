package com.example.alarmemotivacional.ui.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Toast.makeText(context, "Sistema reiniciado — restaurando alarmes", Toast.LENGTH_LONG).show()

            val storage = AlarmStorage(context)
            val scheduler = AlarmScheduler(context)
            storage.getAlarmes().filter { it.active }.forEach { scheduler.ligarAlarme(it) }
        }
    }
}
