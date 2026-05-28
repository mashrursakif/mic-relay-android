package com.example.mic_relay.utils

import RecordMic
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mic_relay.AudioStreamingService


class StreamController(private val context: Context) {
    var isRecording = false;
    val recordMic = RecordMic()
    val sendAudio = SendAudio()

    var onRecordingStateChanged: ((Boolean) -> Unit)? = null

    var audioService: AudioStreamingService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioStreamingService.LocalBinder
            audioService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            isBound = false
        }
    }

    fun bindStreamingService() {
        val intent = Intent(context, AudioStreamingService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindStreamingService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    fun start(ip: String, port: Int) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isBound) {
                audioService?.startForegroundWork()
            }

            Thread {
                sendAudio.setupSocket(ip, port)

                sendAudio.onConnectionClosed = {
                    stop()
                }

                recordMic.start { data ->
                    sendAudio.sendData(data)
                }

//                recordMic.onRecordingStateChanged = { recording ->
//                    if (!recording) {
//                        stop()
//                    }
//                }
            }.start()
            isRecording = true;
            onRecordingStateChanged?.invoke(true);
        } else {
            Log.e("TAG", "Microphone Permission not Granted")
        }
    }

    fun stop() {
        Log.e("TAG", "Stopping Controller")

        recordMic.stop();
        sendAudio.closeSocket();

        if (isBound) {
            audioService?.stopForegroundWork()
        }

        isRecording = false;
        onRecordingStateChanged?.invoke(false);
    }
}