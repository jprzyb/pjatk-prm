package pl.pjatk.proj_2

import android.media.MediaRecorder
import java.io.File

class AudioHelper {
    private var recorder: MediaRecorder? = null
    var outputFile: File? = null

    fun startRecording(path: File) {
        outputFile = path
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile!!.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}
