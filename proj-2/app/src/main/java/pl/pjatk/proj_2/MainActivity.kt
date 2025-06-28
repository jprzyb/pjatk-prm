package pl.pjatk.proj_2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pl.pjatk.proj_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val repo = NoteRepository()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.noteList.adapter = NoteAdapter(emptyList())
        binding.noteList.layoutManager = LinearLayoutManager(this)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        repo.getNotes(
            onResult = { notes ->
                binding.noteList.adapter = NoteAdapter(notes)
            },
            onError = { error ->
                Toast.makeText(this, "Error while initializing: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}
