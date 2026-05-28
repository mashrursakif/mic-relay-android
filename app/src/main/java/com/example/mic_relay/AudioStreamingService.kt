package com.example.mic_relay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AudioStreamingService : Service() {
    private val CHANNEL_ID = "com.micrelay"
    private val NOTIFICTION_ID = 1
//    private val CHANNEL_NAME = "Mic Recording"
    private var wakeLock: PowerManager.WakeLock? = null

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AudioStreamingService = this@AudioStreamingService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MicRelay:WakeLock")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        createNotificationChannel()
//
//        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Streaming Audio")
//            .setContentText("Microphone streaming to PC")
////            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
//            .setOngoing(true)
//            .build()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            startForeground(NOTIFICTION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
//        } else {
//            startForeground(NOTIFICTION_ID, notification)
//        }
//
//        wakeLock?.acquire(10 * 60 * 1000L);

        return START_STICKY
    }

    fun startForegroundWork() {
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Streaming Audio")
            .setContentText("Microphone streaming to PC")
//            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(NOTIFICTION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICTION_ID, notification)
        }

        wakeLock?.acquire(10 * 60 * 1000L);
    }

    fun stopForegroundWork() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        // stop recording/send audio
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Audio Streaming Service",
            NotificationManager.IMPORTANCE_LOW)
        var manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }
}

