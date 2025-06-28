package pl.kubaprzyb.proj2api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes/")
@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteRepository noteRepository;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping
    public List<Note> getNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Note> getNote(@PathVariable String id) {
        Optional<Note> note = noteRepository.findById(id);
        return note.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("{id}")
    public ResponseEntity<Note> updateNote(@PathVariable String id, @RequestBody Note updatedNote) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    existingNote.setText(updatedNote.getText());
                    existingNote.setLocation(updatedNote.getLocation());
                    existingNote.setPhotoUrl(updatedNote.getPhotoUrl());
                    existingNote.setAudioUrl(updatedNote.getAudioUrl());
                    noteRepository.save(existingNote);
                    return ResponseEntity.ok(existingNote);
                })
                .orElseGet(() -> {
                    updatedNote.setId(id);
                    noteRepository.save(updatedNote);
                    return ResponseEntity.ok(updatedNote);
                });
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note newNote) {
        newNote.setId(null);
        Note savedNote = noteRepository.save(newNote);
        return ResponseEntity.ok(savedNote);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
