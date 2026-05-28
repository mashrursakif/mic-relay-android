package com.example.mic_relay.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MicRelayPrefs", Context.MODE_PRIVATE)

    fun saveConnectionDetails(ip: String, port: Int) {
        prefs.edit()
            .putString("LAST_IP", ip)
            .putInt("LAST_PORT", port)
            .apply()
    }

    fun getLastIP(): String? {
        return prefs.getString("LAST_IP", "")
    }

    fun getLastPort(): Int? {
        return prefs.getInt("LAST_PORT", 5000)
    }
}