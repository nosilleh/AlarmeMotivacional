package com.example.alarmemotivacional

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmemotivacional.ui.alarms.AlarmActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abre a tela de alarmes imediatamente
        startActivity(Intent(this, AlarmActivity::class.java))

        // Finaliza esta Activity para não voltar nela
        finish()
    }
}