package pl.pjatk.proj_2

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onNoteRemoved: (Note) -> Unit,
    private val onNoteClicked: (Note) -> Unit   // <-- dodany callback kliknięcia
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(android.R.id.text1)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onNoteClicked(notes[position])
                }
            }

            view.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val note = notes[position]
                    AlertDialog.Builder(view.context)
                        .setTitle("Usuń notatkę")
                        .setMessage("Czy na pewno chcesz usunąć tę notatkę?")
                        .setPositiveButton("Tak") { _, _ ->
                            onNoteRemoved(note)
                            removeNoteAt(position)
                        }
                        .setNegativeButton("Anuluj", null)
                        .show()
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.text.text = "${note.location}: ${note.text}"
    }

    override fun getItemCount() = notes.size

    fun removeNoteAt(position: Int) {
        if (position >= 0 && position < notes.size) {
            notes.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
