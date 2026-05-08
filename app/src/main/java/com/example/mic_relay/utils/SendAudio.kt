package com.example.mic_relay.utils

import RecordMic
import android.util.Log
import java.io.OutputStream
import java.net.Socket

class SendAudio {
    // TCP Setup
//    lateinit var outputStream: OutputStream;

     var socket: Socket? = null;
     var outputStream: OutputStream? = null;

    var onConnectionClosed: (() -> Unit)? = null

    fun setupTCP(ip: String, port: Int): OutputStream? {
        try {
            socket = Socket(ip, port)
            outputStream = socket?.getOutputStream()
        } catch (e: Exception) {
            Log.e("TcpClient", "Connect failed: ${e.message}", e)
        }
        return outputStream;
    }

    fun sendData(outputStream: OutputStream?, data: ByteArray) {
        try {
            outputStream?.write(data)
        } catch (e: Exception) {
            Log.e("TAG", "TCP Stream Write Failed");
            onConnectionClosed?.invoke()
        }
    }

    fun closeTCP() {
        Log.e("TAG", "Closing TCP");

        try {
            outputStream?.flush()
        } catch (e: Exception) {

        }

        try {
            outputStream?.close()
        } catch (e: Exception) {

        }

        try {
            socket?.close()
        } catch  (e: Exception) {

        }

        outputStream = null
        socket = null
    }
}