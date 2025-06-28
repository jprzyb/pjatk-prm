package pl.pjatk.proj_2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pl.pjatk.proj_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val repo = NoteRepository()
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private val notes = mutableListOf<Note>()

    private val addNoteLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            repo.getNotes(
                onResult = { fetchedNotes ->
                    notes.clear()
                    notes.addAll(fetchedNotes)
                    noteAdapter.notifyDataSetChanged()
                },
                onError = { error ->
                    Toast.makeText(this, "Błąd: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteAdapter = NoteAdapter(notes,
            onNoteRemoved = { noteToRemove ->
                repo.deleteNote(noteToRemove)
                notes.remove(noteToRemove)
                noteAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Usunięto notatkę", Toast.LENGTH_SHORT).show()
            },
            onNoteClicked = { note ->
                val intent = Intent(this, NoteDetailActivity::class.java)
                intent.putExtra("note", note)
                startActivity(intent)
            }
        )

        binding.noteList.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        binding.fabAdd.setOnClickListener {
            addNoteLauncher.launch(Intent(this, AddNoteActivity::class.java))
        }

        repo.getNotes(
            onResult = { fetchedNotes ->
                notes.clear()
                notes.addAll(fetchedNotes)
                noteAdapter.notifyDataSetChanged()
            },
            onError = { error ->
                Toast.makeText(this, "Błąd: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}
