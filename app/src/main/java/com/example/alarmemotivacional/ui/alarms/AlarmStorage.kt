package com.example.alarmemotivacional.ui.alarms

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

class AlarmStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarme_prefs", Context.MODE_PRIVATE)

    private val KEY_ALARMES = "alarmes_json"

    fun salvarAlarme(alarme: AlarmData) {
        val lista = getAlarmes().toMutableList()
        lista.add(alarme)
        salvarLista(lista)
    }

    fun atualizarAlarme(alarme: AlarmData) {
        val listaAtualizada = getAlarmes().map { if (it.id == alarme.id) alarme else it }
        salvarLista(listaAtualizada)
    }

    fun removerAlarme(id: Long) {
        val listaAtualizada = getAlarmes().filterNot { it.id == id }
        salvarLista(listaAtualizada)
    }

    fun getAlarmes(): List<AlarmData> {
        val jsonString = prefs.getString(KEY_ALARMES, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val lista = mutableListOf<AlarmData>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            lista.add(AlarmData.fromJson(jsonObject))
        }

        return lista
    }

    private fun salvarLista(lista: List<AlarmData>) {
        val jsonArray = JSONArray()
        lista.forEach { jsonArray.put(it.toJson()) }
        prefs.edit().putString(KEY_ALARMES, jsonArray.toString()).apply()
    }
}