package pl.pjatk.proj_2

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
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

        if (note?.photoUrl != null) {
            val imageView = findViewById<ImageView>(R.id.tvPhoto)

            Picasso.get()
                .load("http://10.0.2.2:8080/api/files/download/${note.photoUrl}")
                .resize(400, 400)
                .centerCrop()
                .into(imageView)
        }


        if (note != null) {
            binding.tvLocation.text = note.location
            binding.tvText.text = note.text
            binding.tvPhotoUrl.text = note.photoUrl ?: "Brak zdjęcia"
            binding.tvAudioUrl.text = note.audioUrl ?: "Brak audio"
        }
    }
}
