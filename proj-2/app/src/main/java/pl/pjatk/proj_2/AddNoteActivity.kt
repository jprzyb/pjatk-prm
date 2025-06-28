package pl.pjatk.proj_2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.pjatk.proj_2.databinding.ActivityAddNoteBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private val repo = NoteRepository()
    private lateinit var audioHelper: AudioHelper
    private lateinit var locationHelper: LocationHelper

    private var photoBytes: ByteArray? = null
    private var audioFile: File? = null
    private var locationName: String = ""

    private lateinit var binding: ActivityAddNoteBinding

    private val CAMERA_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_CODE = 101
    private val AUDIO_PERMISSION_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioHelper = AudioHelper()
        locationHelper = LocationHelper(this)

        var locationText: TextView = findViewById(R.id.tvLocation)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        } else {
            locationHelper.getCityName { city ->
                runOnUiThread {
                    locationText.text = city
                    locationName = city
                }
            }
        }

        binding.btnPhoto.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        binding.btnAudio.setOnClickListener {
            checkAudioPermissionAndStartRecording()
        }

        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                val noteText = binding.etNote.text.toString()
                var photoUrl: String? = "photo_${UUID.randomUUID()}.jpg"
                var audioUrl: String? = audioFile?.name

                if (photoBytes != null) {
                    val photoFile = File(filesDir, photoUrl)
                    photoFile.writeBytes(photoBytes!!)
                    uploadFileSuspend(photoFile)
                }
                else photoUrl = null

                if (audioFile != null) {
                    val audioFileName = audioUrl
                    val audioLocalFile = File(filesDir, audioFileName)
                    uploadFileSuspend(audioFile!!)
                }
                else audioUrl = null

                val note = Note(
                    text = noteText,
                    location = locationName,
                    photoUrl = photoUrl,
                    audioUrl = audioUrl
                )

                saveNoteSuspend(note)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }


    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private suspend fun saveNoteSuspend(note: Note): Unit =
        suspendCancellableCoroutine { cont ->
            repo.saveNote(
                note,
                onSuccess = { cont.resume(Unit, null) },
                onError = { _ -> cont.resume(Unit, null) }
            )
        }

    private suspend fun uploadFileSuspend(file: File): String? =
        suspendCancellableCoroutine { cont ->
            repo.uploadFile(
                file,
                onResult = { cont.resume(it, null) },
                onError = { cont.resume(null, null) }
            )
        }


    private fun checkAudioPermissionAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                AUDIO_PERMISSION_CODE
            )
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        val audioPath = File(filesDir, "audio_${UUID.randomUUID()}.3gp")
        audioHelper.startRecording(audioPath)
        binding.btnStopAudio.setOnClickListener {
            audioHelper.stopRecording()
            audioFile = audioPath
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Odmowa dostępu do aparatu", Toast.LENGTH_SHORT).show()
                }
            }
            AUDIO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    Toast.makeText(this, "Odmowa dostępu do mikrofonu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                photoBytes = stream.toByteArray()
            }
        }
    }
}
