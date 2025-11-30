package com.example.alarmemotivacional.ui.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Toast.makeText(context, "Sistema reiniciado — restaurando alarmes", Toast.LENGTH_LONG).show()

            // Aqui você vai restaurar alarmes salvos no banco futuramente.
            // No momento, deixaremos apenas o Toast para confirmar que o receiver funciona.
        }
    }
}