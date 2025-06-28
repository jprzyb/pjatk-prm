package pl.pjatk.proj_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.pjatk.proj_2.databinding.ActivityNoteDetailBinding

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)

        binding.btnBack.setOnClickListener {
            finish()
        }

        setContentView(binding.root)

        val note = intent.getSerializableExtra("note") as? Note

        if (note != null) {
            binding.tvLocation.text = note.location
            binding.tvText.text = note.text
            binding.tvPhotoUrl.text = note.photoUrl ?: "Brak zdjęcia"
            binding.tvAudioUrl.text = note.audioUrl ?: "Brak audio"
        }
    }
}
