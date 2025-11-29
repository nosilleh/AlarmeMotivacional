package com.example.alarmemotivacional.ui.alarms

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmemotivacional.R

class AddAlarmActivity : AppCompatActivity() {

    private var somSelecionado: Uri? = null

    companion object {
        private const val REQUEST_PICK_RINGTONE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveAlarm)

        val btnSelectSound = findViewById<Button>(R.id.btnSelectSound)
        val textSoundSelected = findViewById<TextView>(R.id.textSoundSelected)

        // ---------------- SELECTOR DE SOM NATIVO ----------------
        btnSelectSound.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)

            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_ALARM
            )
            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                "Selecione o som do alarme"
            )
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)

            startActivityForResult(intent, REQUEST_PICK_RINGTONE)
        }

        // ---------------- SALVAR ALARME ----------------
        btnSave.setOnClickListener {

            val hour = timePicker.hour
            val minute = timePicker.minute

            val horaFormatada = String.format("%02d:%02d", hour, minute)

            val storage = AlarmStorage(this)
            storage.salvarAlarme(horaFormatada)

            // Liga o alarme automaticamente
            val scheduler = AlarmScheduler(this)
            scheduler.ligarAlarme(hour, minute)

            Toast.makeText(
                this,
                "Alarme definido para $horaFormatada",
                Toast.LENGTH_SHORT
            ).show()

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    // ---------------- RESULTADO DO PICKER DE SOM ----------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_RINGTONE && resultCode == Activity.RESULT_OK) {

            val uri = data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

            if (uri != null) {
                somSelecionado = uri

                val textSound = findViewById<TextView>(R.id.textSoundSelected)
                val title = RingtoneManager.getRingtone(this, uri).getTitle(this)

                textSound.text = "Som selecionado: $title"
            }
        }
    }
}