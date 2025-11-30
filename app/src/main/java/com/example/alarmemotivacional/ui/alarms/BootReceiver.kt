package com.example.alarmemotivacional.ui.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.alarmemotivacional.BuildConfig

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    context,
                    "Sistema reiniciado — restaurando alarmes",
                    Toast.LENGTH_LONG
                ).show()
            }

            val storage = AlarmStorage(context)
            val scheduler = AlarmScheduler(context)

            storage.getAlarmes()
                .filter { it.ativo }
                .forEach { alarme ->
                    val (hora, minuto) = parseHora(alarme.hora)
                    if (hora != null && minuto != null) {
                        scheduler.ligarAlarme(hora, minuto)
                        Log.d("BootReceiver", "Alarme reativado: ${alarme.hora}")
                    } else {
                        Log.w("BootReceiver", "Horário inválido ao restaurar: ${alarme.hora}")
                    }
                }
        }
    }

    private fun parseHora(hora: String): Pair<Int?, Int?> {
        val partes = hora.split(":")
        if (partes.size != 2) return null to null

        val horaInt = partes[0].toIntOrNull()
        val minutoInt = partes[1].toIntOrNull()

        return horaInt to minutoInt
    }
}
