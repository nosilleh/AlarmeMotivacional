package com.example.alarmemotivacional.ui.alarms

import android.content.Context
import android.content.SharedPreferences

class AlarmStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarme_prefs", Context.MODE_PRIVATE)

    private val KEY_ALARMES = "alarmes_salvos"

    fun salvarAlarme(hora: String) {
        val lista = getAlarmes().toMutableList()
        lista.add(hora)
        prefs.edit().putStringSet(KEY_ALARMES, lista.toSet()).apply()
    }

    fun getAlarmes(): List<String> {
        return prefs.getStringSet(KEY_ALARMES, emptySet())!!.toList()
    }
}