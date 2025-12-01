package com.example.alarmemotivacional.ui.alarms

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alarmemotivacional.R

class AddAlarmActivity : AppCompatActivity() {

    private var somSelecionado: Uri? = null
    private var videoSelecionado: Uri? = null
    private var onPermissionGranted: (() -> Unit)? = null
    private lateinit var textSoundSelected: TextView
    private lateinit var textVideoSelected: TextView

    companion object {
        private const val REQUEST_PICK_RINGTONE = 1001
        private const val REQUEST_MEDIA_PERMISSION = 1002
        private const val REQUEST_PICK_VIDEO = 1003
        private const val KEY_SELECTED_SOUND_URI = "selected_sound_uri"
        private const val KEY_SELECTED_VIDEO_URI = "selected_video_uri"
    }

    private val mediaPermissions: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveAlarm)

        val btnSelectSound = findViewById<Button>(R.id.btnSelectSound)
        val btnSelectVideo = findViewById<Button>(R.id.btnSelectVideo)
        textSoundSelected = findViewById(R.id.textSoundSelected)
        textVideoSelected = findViewById(R.id.textVideoSelected)

        somSelecionado = savedInstanceState?.getString(KEY_SELECTED_SOUND_URI)?.let { Uri.parse(it) }
        videoSelecionado = savedInstanceState?.getString(KEY_SELECTED_VIDEO_URI)?.let { Uri.parse(it) }
        atualizarLabelSom(somSelecionado)
        atualizarLabelVideo(videoSelecionado)

        // ---------------- SELECTOR DE SOM NATIVO ----------------
        btnSelectSound.setOnClickListener { ensureMediaPermission { abrirSeletorSom() } }
        btnSelectVideo.setOnClickListener { ensureMediaPermission { abrirSeletorVideo() } }

        // ---------------- SALVAR ALARME ----------------
        btnSave.setOnClickListener {

            val hour = timePicker.hour
            val minute = timePicker.minute

            val alarme = AlarmData(
                hour = hour,
                minute = minute,
                isActive = true,
                soundUri = somSelecionado?.toString(),
                videoUri = videoSelecionado?.toString()
            )

            val scheduler = AlarmScheduler(this)
            val alarmeAgendado = scheduler.ligarAlarme(alarme)

            val storage = AlarmStorage(this)
            val alarmeParaSalvar = if (alarmeAgendado) alarme else alarme.copy(isActive = false)
            storage.salvarAlarme(alarmeParaSalvar)

            if (!alarmeAgendado) {
                Toast.makeText(
                    this,
                    R.string.exact_alarm_permission_missing,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Alarme definido para ${alarme.formattedTime}",
                    Toast.LENGTH_SHORT
                ).show()
            }

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
                persistirPermissaoUri(data, uri)
                atualizarLabelSom(uri)
            }
        }

        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            val uri = data?.data

            if (uri != null) {
                videoSelecionado = uri
                persistirPermissaoUri(data, uri)
                atualizarLabelVideo(uri)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_SELECTED_SOUND_URI, somSelecionado?.toString())
        outState.putString(KEY_SELECTED_VIDEO_URI, videoSelecionado?.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                onPermissionGranted?.invoke()
            } else {
                Toast.makeText(
                    this,
                    R.string.media_permission_denied,
                    Toast.LENGTH_SHORT
                ).show()
            }

            onPermissionGranted = null
        }
    }

    private fun abrirSeletorSom() {
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

    private fun abrirSeletorVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivityForResult(intent, REQUEST_PICK_VIDEO)
    }

    private fun ensureMediaPermission(onGranted: () -> Unit) {
        val missingPermissions = mediaPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            onGranted()
        } else {
            onPermissionGranted = onGranted
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                REQUEST_MEDIA_PERMISSION
            )
        }
    }

    private fun atualizarLabelSom(uri: Uri?) {
        val titulo = uri?.let { RingtoneManager.getRingtone(this, it)?.getTitle(this) }

        textSoundSelected.text = when {
            titulo != null -> getString(R.string.sound_selected_label, titulo)
            uri != null -> getString(R.string.sound_unknown_label)
            else -> getString(R.string.sound_default_label)
        }
    }

    private fun atualizarLabelVideo(uri: Uri?) {
        textVideoSelected.text = when {
            uri != null -> getString(R.string.video_selected_label)
            else -> getString(R.string.video_default_label)
        }
    }

    private fun persistirPermissaoUri(data: Intent?, uri: Uri) {
        if (uri.scheme != ContentResolver.SCHEME_CONTENT) return

        val flags = data?.flags ?: 0
        if (flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0) {
            runCatching {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }
}