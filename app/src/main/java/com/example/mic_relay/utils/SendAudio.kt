package com.example.mic_relay.utils

import android.util.Log
import java.net.DatagramPacket
//import java.io.OutputStream
import java.net.DatagramSocket
import java.net.InetAddress

class SendAudio {
    // TCP Setup
//    lateinit var outputStream: OutputStream;

     var socket: DatagramSocket? = null;
     var address: InetAddress? = null;
     var port: Int = 0;
//     var outputStream: OutputStream? = null;

    var onConnectionClosed: (() -> Unit)? = null

    fun setupSocket(ip: String, portValue: Int) {
        try {
//            socket = Socket(ip, port)
            socket = DatagramSocket()
            address = InetAddress.getByName(ip)
            port = portValue;
//            outputStream = socket?.getOutputStream()
        } catch (e: Exception) {
            Log.e("SOCKET", "Connect failed: ${e.message}", e)
        }
    }

    fun sendData(data: ByteArray) {
        try {
            Log.e("TAG", "Sending data $data.size")
//            outputStream?.write(data)
            val packet = DatagramPacket(data, data.size, address, port);
            socket?.send(packet)
        } catch (e: Exception) {
            Log.e("TAG", "Send Data Failed");
            onConnectionClosed?.invoke()
        }
    }

    fun closeSocket() {
        Log.e("TAG", "Closing Socket");

//        try {
//            outputStream?.flush()
//        } catch (e: Exception) {
//
//        }
//
//        try {
//            outputStream?.close()
//        } catch (e: Exception) {
//
//        }
//
//        try {
//            socket?.close()
//        } catch  (e: Exception) {
//
//        }
//
//        outputStream = null
        socket?.close();
        socket = null
    }
}