package com.example.alarmemotivacional.ui.alarms

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmemotivacional.util.MiuiHelper
import com.example.alarmemotivacional.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlarmActivity : AppCompatActivity() {

    private lateinit var textEmpty: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlarmAdapter

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

        if (lista.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            adapter = AlarmAdapter(lista)
            recyclerView.adapter = adapter
        }
    }
}