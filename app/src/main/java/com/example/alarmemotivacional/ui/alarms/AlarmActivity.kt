package com.example.alarmemotivacional.ui.alarms

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
    private val alarmes: MutableList<AlarmData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        textEmpty = findViewById(R.id.textEmpty)
        recyclerView = findViewById(R.id.recyclerAlarmes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AlarmAdapter(alarmes, ::onToggleAlarm, ::onRemoveAlarm)
        recyclerView.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            startActivity(Intent(this, AddAlarmActivity::class.java))
        }

        pedirPermissaoAlarme()

    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
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
        val storage = AlarmStorage(this)
        val lista = storage.getAlarmes()

        alarmes.clear()
        alarmes.addAll(lista)
        adapter.notifyDataSetChanged()

        val isEmpty = lista.isEmpty()
        textEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun onToggleAlarm(alarme: AlarmData) {
        val storage = AlarmStorage(this)
        storage.atualizarAlarme(alarme)

        val scheduler = AlarmScheduler(this)
        if (alarme.isActive) {
            scheduler.ligarAlarme(alarme)
        } else {
            scheduler.desligarAlarme(alarme)
        }

        atualizarLista()
    }

    private fun onRemoveAlarm(alarme: AlarmData) {
        val storage = AlarmStorage(this)
        storage.removerAlarme(alarme.id)

        val scheduler = AlarmScheduler(this)
        scheduler.desligarAlarme(alarme)

        atualizarLista()
    }
}