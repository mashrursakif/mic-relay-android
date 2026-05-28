package com.example.mic_relay

import RecordMic
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mic_relay.ui.theme.MicrelayTheme
import com.example.mic_relay.utils.PreferencesHelper
import com.example.mic_relay.utils.SendAudio
import com.example.mic_relay.utils.StreamController

class MainActivity : ComponentActivity() {
    lateinit var ipInput: EditText
    lateinit var portInput: EditText
    lateinit var recordButton: Button

    lateinit var ip: String;
    var port: Int? = null;

//    val controller = StreamController(this)
    private lateinit var controller: StreamController

    private lateinit var prefsHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, // Scrim color
                Color.TRANSPARENT  // Detect dark/light mode automatically
            )
        )

        setContentView(R.layout.activity_main)

        val rootLayout = findViewById<android.view.View>(R.id.main_container)

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, windowInsets ->
            // Retrieve the physical size of status bars, nav bars, and display cutouts (notches)
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // Apply these pixel measurements directly as padding to the root layout
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)

            // Return the unconsumed insets to pass down to child views if necessary
            windowInsets
        }

        controller = StreamController(this)
        controller.bindStreamingService()

        prefsHelper = PreferencesHelper(this)

        ipInput = findViewById<EditText>(R.id.ip_addr_input)
        portInput = findViewById<EditText>(R.id.port_input)
        recordButton = findViewById<Button>(R.id.record_button)

        val savedIP = prefsHelper.getLastIP()
        val savedPort = prefsHelper.getLastPort()

        if (!savedIP.isNullOrEmpty()) ipInput.setText(savedIP)
        portInput.setText(savedPort.toString())

        recordButton.setOnClickListener {
            if (!controller.isRecording) {
                ip = ipInput.text.toString().trim()
                port = portInput.text.toString().toIntOrNull()

                if (ip.isNotEmpty() && port != null) {
                    prefsHelper.saveConnectionDetails(ip, port!!)

                    checkMicPermissionAndStart();
//                    updateState(true)
                }
            } else {
                controller.stop();
//                updateState(false)
            }
        }

        controller.onRecordingStateChanged = { recording ->
            Log.e("TAG", "recording: $recording")
            runOnUiThread {
                updateState(recording)
            }
        }

//        setContent {
//            MicrelayTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
    }




    private val requestMicPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

            if (isGranted) {
                checkMicPermissionAndStart()
            } else {
                //
            }
        }


    fun checkMicPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (port != null) {
                controller.start(ip, port!!)
            }
        } else {
            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun updateState(value: Boolean) {
        Log.e("TAG", "update state -> recording: $value")
//        controller.onRecordingStateChanged?.invoke(value)

        if (value) {
            recordButton.text = "STOP";
        } else {
            recordButton.text = "START";
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MicrelayTheme {
        Greeting("Android")
    }
}