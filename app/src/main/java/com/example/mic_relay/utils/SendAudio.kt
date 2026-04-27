package com.example.mic_relay.utils

import RecordMic
import android.util.Log
import java.io.OutputStream
import java.net.Socket

class SendAudio {
    // TCP Setup
//    lateinit var outputStream: OutputStream;

    val recordMic = RecordMic()

     var socket: Socket? = null;
     var outputStream: OutputStream? = null;

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
            closeTCP();
            recordMic.stop();
        }
    }

    fun closeTCP() {
        Log.e("TAG", "Closing TCP");

        try {
            outputStream?.flush()
        } catch (e: Exception) {
            Log.e("TAG", "ERR Flush");

        }

        try {
            outputStream?.close()
        } catch (e: Exception) {
            Log.e("TAG", "ERR stream close");

        }

        try {
            socket?.close()
        } catch  (e: Exception) {
            Log.e("TAG", "ERR socket close");

        }

        Log.e("TAG", "setting var to null")

        outputStream = null
        socket = null
    }
}