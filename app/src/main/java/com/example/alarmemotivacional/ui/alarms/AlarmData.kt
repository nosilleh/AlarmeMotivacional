package com.example.alarmemotivacional.ui.alarms

import org.json.JSONObject

data class AlarmData(
    val id: Long = System.currentTimeMillis(),
    val hour: Int,
    val minute: Int,
    val isActive: Boolean = true,
    val soundUri: String? = null,
    val videoUri: String? = null
) {

    fun toJson(): JSONObject = JSONObject().apply {
        put(KEY_ID, id)
        put(KEY_HOUR, hour)
        put(KEY_MINUTE, minute)
        put(KEY_ACTIVE, isActive)
        put(KEY_SOUND_URI, soundUri)
        put(KEY_VIDEO_URI, videoUri)
    }

    val formattedTime: String
        get() = String.format("%02d:%02d", hour, minute)

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"
        private const val KEY_ACTIVE = "isActive"
        private const val KEY_SOUND_URI = "soundUri"
        private const val KEY_VIDEO_URI = "videoUri"

        fun fromJson(jsonObject: JSONObject): AlarmData {
            return AlarmData(
                id = jsonObject.getLong(KEY_ID),
                hour = jsonObject.getInt(KEY_HOUR),
                minute = jsonObject.getInt(KEY_MINUTE),
                isActive = jsonObject.optBoolean(KEY_ACTIVE, true),
                soundUri = jsonObject.optString(KEY_SOUND_URI).takeIf { it.isNotBlank() },
                videoUri = jsonObject.optString(KEY_VIDEO_URI).takeIf { it.isNotBlank() }
            )
        }
    }
}
