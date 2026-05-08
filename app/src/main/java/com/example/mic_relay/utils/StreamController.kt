package com.example.mic_relay.utils

import RecordMic
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat


class StreamController(private val context: Context) {
    val recordMic = RecordMic()
    val sendAudio = SendAudio()

    var onRecordingStateChanged: ((Boolean) -> Unit)? = null

    fun start(ip: String, port: Int) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Thread {
                val outputStream = sendAudio.setupTCP(ip, port)

                sendAudio.onConnectionClosed = {
                    stop()
                }

                recordMic.start { data ->
                    sendAudio.sendData(outputStream, data)
                }

                recordMic.onRecordingStateChanged = { recording ->
                    if (!recording) {
                        stop()
                    }
                }
            }.start()
            onRecordingStateChanged?.invoke(true);
        } else {
            Log.e("TAG", "Microphone Permission not Granted")
        }
    }

    fun stop() {
        Log.e("TAG", "Stopping Controller")

        recordMic.stop();
        sendAudio.closeTCP();
        onRecordingStateChanged?.invoke(false);
    }
}