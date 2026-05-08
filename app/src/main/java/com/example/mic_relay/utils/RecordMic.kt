import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresPermission

class RecordMic {
//    var isRecording = false;
//    var onRecordingStateChanged: ((Boolean) -> Unit)? = null

    var recorder: AudioRecord? = null;
    val minBufferSize = 1024
    val format = AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(48000).setChannelMask(
        AudioFormat.CHANNEL_IN_MONO).build()

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    fun start(sendAudio: (ByteArray) -> Unit) {
        recorder = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(format)
            .setBufferSizeInBytes(2 * minBufferSize).build()

        recorder?.startRecording()
//        isRecording = true;
//        onRecordingStateChanged?.invoke(isRecording)

        val buffer = ByteArray(minBufferSize);

//        while(isRecording) {
        while (recorder != null) {
            val read = recorder?.read(buffer, 0, buffer.size);
            if (read != null && read > 0) {
                sendAudio(buffer.copyOf(read))
            }
        }
//        }
    }

    fun stop() {
        Log.e("TAG", "Stopping Mic")
//        isRecording = false;
//        onRecordingStateChanged?.invoke(isRecording)
        recorder?.stop();
        recorder?.release();
        recorder = null;
    }
}