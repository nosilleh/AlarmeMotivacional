package com.example.alarmemotivacional.ui.alarms

import android.content.Context
import android.content.SharedPreferences

class AlarmStorage(context: Context) {

    data class AlarmData(
        val hora: String,
        val ativo: Boolean
    )

    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarme_prefs", Context.MODE_PRIVATE)

    private val KEY_ALARMES = "alarmes_salvos"

    fun salvarAlarme(hora: String, ativo: Boolean = true) {
        val lista = getAlarmes().toMutableList()
        lista.add(AlarmData(hora, ativo))
        prefs.edit().putStringSet(KEY_ALARMES, lista.map { serialize(it) }.toSet()).apply()
    }

    fun getAlarmes(): List<AlarmData> {
        return prefs.getStringSet(KEY_ALARMES, emptySet())
            ?.mapNotNull { deserialize(it) }
            ?.sortedBy { it.hora }
            ?: emptyList()
    }

    private fun serialize(alarm: AlarmData): String {
        return "${alarm.hora}|${alarm.ativo}"
    }

    private fun deserialize(raw: String): AlarmData? {
        val partes = raw.split("|")
        if (partes.isEmpty()) return null

        val hora = partes[0]
        val ativo = partes.getOrNull(1)?.toBooleanStrictOrNull() ?: true

        return AlarmData(hora, ativo)
    }
}
