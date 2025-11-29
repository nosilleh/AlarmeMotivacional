package com.example.alarmemotivacional.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

object MiuiHelper {

    fun isMiui(): Boolean {
        return Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)
    }

    fun hasExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun showFixDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Permissão necessária")
            .setMessage("Seu celular Xiaomi (MIUI) bloqueia alarmes. Para que seus alarmes funcionem, você precisa liberar algumas permissões.")
            .setPositiveButton("Liberar agora") { _, _ ->
                openExactAlarmSettings(context)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun openExactAlarmSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            } else {
                openAppDetails(context)
            }
        } catch (e: Exception) {
            openAppDetails(context)
        }
    }

    fun openAppDetails(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }
}