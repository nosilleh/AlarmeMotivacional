package com.example.alarmemotivacional.ui.alarms

/**
 * Representa um alarme salvo localmente.
 */
data class AlarmData(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val soundUri: String? = null,
    val active: Boolean = true
)
