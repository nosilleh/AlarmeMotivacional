package com.example.alarmemotivacional.ui.alarms

import android.content.Context
import android.content.SharedPreferences

data class AlarmData(
    val hora: String,
    val soundUri: String?
)

class AlarmStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarme_prefs", Context.MODE_PRIVATE)

    private val KEY_ALARMES = "alarmes_salvos"

    fun salvarAlarme(hora: String, soundUri: String?) {
        val lista = getAlarmes().toMutableList()
        lista.add(AlarmData(hora, soundUri))
        prefs.edit().putStringSet(KEY_ALARMES, lista.map { serialize(it) }.toSet()).apply()
    }

    fun getAlarmes(): List<AlarmData> {
        return prefs.getStringSet(KEY_ALARMES, emptySet())!!
            .mapNotNull { deserialize(it) }
    }

    private fun serialize(alarmData: AlarmData): String {
        val uriPart = alarmData.soundUri ?: ""
        return "${alarmData.hora}|$uriPart"
    }

    private fun deserialize(raw: String): AlarmData? {
        if (raw.isEmpty()) return null

        val parts = raw.split("|", limit = 2)
        val hora = parts.getOrNull(0) ?: return null
        val soundUri = parts.getOrNull(1)?.takeIf { it.isNotEmpty() }
        return AlarmData(hora, soundUri)
    }
}