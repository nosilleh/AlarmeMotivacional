package com.example.alarmemotivacional.ui.alarms

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.absoluteValue

class AlarmStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarme_prefs", Context.MODE_PRIVATE)

    private val keyAlarmes = "alarmes_json"

    fun salvarOuAtualizar(alarm: AlarmData) {
        val lista = getAlarmes().toMutableList()
        val index = lista.indexOfFirst { it.id == alarm.id }
        if (index >= 0) {
            lista[index] = alarm
        } else {
            lista.add(alarm)
        }
        persistir(lista)
    }

    fun removerAlarme(id: Int) {
        val listaFiltrada = getAlarmes().filter { it.id != id }
        persistir(listaFiltrada)
    }

    fun getAlarmes(): List<AlarmData> {
        val json = prefs.getString(keyAlarmes, "[]") ?: "[]"
        val array = JSONArray(json)
        val lista = mutableListOf<AlarmData>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            lista.add(
                AlarmData(
                    id = obj.getInt("id"),
                    hour = obj.getInt("hour"),
                    minute = obj.getInt("minute"),
                    soundUri = obj.optString("soundUri").takeIf { it.isNotEmpty() },
                    active = obj.optBoolean("active", true)
                )
            )
        }
        return lista.sortedWith(compareBy({ it.hour }, { it.minute }))
    }

    fun getAlarme(id: Int): AlarmData? = getAlarmes().firstOrNull { it.id == id }

    private fun persistir(lista: List<AlarmData>) {
        val array = JSONArray()
        lista.sortedWith(compareBy({ it.hour }, { it.minute })).forEach { alarm ->
            val obj = JSONObject().apply {
                put("id", alarm.id)
                put("hour", alarm.hour)
                put("minute", alarm.minute)
                put("soundUri", alarm.soundUri ?: "")
                put("active", alarm.active)
            }
            array.put(obj)
        }
        prefs.edit().putString(keyAlarmes, array.toString()).apply()
    }

    companion object {
        fun gerarId(): Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt().absoluteValue
    }
}
