package com.example.alarmemotivacional.ui.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmemotivacional.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.alarmemotivacional.util.MiuiHelper

class AlarmActivity : AppCompatActivity() {

    private lateinit var textEmpty: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlarmAdapter
    private val storage by lazy { AlarmStorage(this) }
    private val scheduler by lazy { AlarmScheduler(this) }
    private val alarmes = mutableListOf<AlarmData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        textEmpty = findViewById(R.id.textEmpty)
        recyclerView = findViewById(R.id.recyclerAlarmes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            startActivity(Intent(this, AddAlarmActivity::class.java))
        }

        pedirPermissaoAlarme()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarme_channel",
                "Alarmes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal usado para alarmes disparados"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun pedirPermissaoAlarme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (MiuiHelper.isMiui() && !MiuiHelper.hasExactAlarmPermission(this)) {
            MiuiHelper.showFixDialog(this)
        }

        atualizarLista()
    }

    private fun atualizarLista() {
        alarmes.clear()
        alarmes.addAll(storage.getAlarmes())

        if (alarmes.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            if (!::adapter.isInitialized) {
                adapter = AlarmAdapter(
                    lista = alarmes,
                    onToggle = ::alternarAlarme,
                    onEdit = ::editarAlarme,
                    onDelete = ::excluirAlarme
                )
                recyclerView.adapter = adapter
            } else {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun alternarAlarme(alarm: AlarmData, ativo: Boolean) {
        val atualizado = alarm.copy(active = ativo)
        storage.salvarOuAtualizar(atualizado)
        val index = alarmes.indexOfFirst { it.id == alarm.id }
        if (index >= 0) {
            alarmes[index] = atualizado
            adapter.notifyItemChanged(index)
        }

        scheduler.desligarAlarme(alarm.id)
        if (ativo) {
            scheduler.ligarAlarme(atualizado)
        }
    }

    private fun editarAlarme(alarm: AlarmData) {
        val intent = Intent(this, AddAlarmActivity::class.java).apply {
            putExtra(AddAlarmActivity.EXTRA_ALARM_ID, alarm.id)
            putExtra(AddAlarmActivity.EXTRA_ALARM_HOUR, alarm.hour)
            putExtra(AddAlarmActivity.EXTRA_ALARM_MINUTE, alarm.minute)
            putExtra(AddAlarmActivity.EXTRA_ALARM_SOUND, alarm.soundUri)
            putExtra(AddAlarmActivity.EXTRA_ALARM_ACTIVE, alarm.active)
        }
        startActivity(intent)
    }

    private fun excluirAlarme(alarm: AlarmData) {
        scheduler.desligarAlarme(alarm.id)
        storage.removerAlarme(alarm.id)
        alarmes.removeAll { it.id == alarm.id }
        adapter.notifyDataSetChanged()

        if (alarmes.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }
}
