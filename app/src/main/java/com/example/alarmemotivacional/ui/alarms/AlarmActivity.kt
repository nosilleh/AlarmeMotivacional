package com.example.alarmemotivacional.ui.alarms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmemotivacional.R
import com.example.alarmemotivacional.util.MiuiHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlarmActivity : AppCompatActivity() {

    private lateinit var textEmpty: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlarmAdapter
    private val alarmes: MutableList<AlarmData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        solicitarPermissaoNotificacao()

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !MiuiHelper.hasExactAlarmPermission(this)) {
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
        val scheduler = AlarmScheduler(this)
        val storage = AlarmStorage(this)

        val alarmeAtualizado = if (alarme.isActive) {
            val agendado = scheduler.ligarAlarme(alarme)
            if (agendado) alarme else alarme.copy(isActive = false)
        } else {
            scheduler.desligarAlarme(alarme)
            alarme
        }

        if (!alarme.isActive) {
            Toast.makeText(
                this,
                R.string.alarm_disabled_feedback,
                Toast.LENGTH_SHORT
            ).show()
        }

        storage.atualizarAlarme(alarmeAtualizado)
        atualizarLista()
    }

    private fun onRemoveAlarm(alarme: AlarmData) {
        val storage = AlarmStorage(this)
        storage.removerAlarme(alarme.id)

        val scheduler = AlarmScheduler(this)
        scheduler.desligarAlarme(alarme)

        atualizarLista()
    }

    private fun solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permissao = Manifest.permission.POST_NOTIFICATIONS
        val possuiPermissao = ContextCompat.checkSelfPermission(this, permissao) ==
            PackageManager.PERMISSION_GRANTED

        if (!possuiPermissao) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permissao),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            val concedida = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED

            if (!concedida) {
                Toast.makeText(
                    this,
                    R.string.notification_permission_denied,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 2001
    }
}