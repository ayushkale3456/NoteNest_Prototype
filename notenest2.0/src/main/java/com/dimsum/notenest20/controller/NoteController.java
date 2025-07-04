package com.dimsum.notenest20.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dimsum.notenest20.model.Note;
import com.dimsum.notenest20.repository.NoteRepository;



@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

	private NoteRepository noteRepository;

	@Autowired
	public NoteController(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@PostMapping("/upload")
	public ResponseEntity<?> uploadNote(@RequestBody Note note) {
		noteRepository.save(note);
		return ResponseEntity.ok("Note uploaded successfully");
	}
	
	@PutMapping("/edit/{id}")
	public ResponseEntity<?> editNote(@PathVariable Long id, @RequestBody Note updatedNote) {
	  return noteRepository.findById(id)
	    .map(note -> {
	      note.setTitle(updatedNote.getTitle());
	      note.setStream(updatedNote.getStream());
	      note.setYear(updatedNote.getYear());
	      note.setFileLink(updatedNote.getFileLink());
	      note.setUploadedBy(updatedNote.getUploadedBy());
	      noteRepository.save(note);
	      return ResponseEntity.ok("Note updated successfully");
	    }).orElse(ResponseEntity.status(404).body("Note not found"));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteNote(@PathVariable Long id) {
	  if (!noteRepository.existsById(id)) {
	    return ResponseEntity.status(404).body("Note not found");
	  }
	  noteRepository.deleteById(id);
	  return ResponseEntity.ok("Note deleted successfully");
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllNotes() {
		return ResponseEntity.ok(noteRepository.findAll());
	}

	@GetMapping
	public ResponseEntity<?> getNotesByStreamAndYear(@RequestParam String stream,
			@RequestParam(required = false) String year) {
		if (year != null) {
			return ResponseEntity.ok(noteRepository.findByStreamAndYear(stream, year));
		} else {
			return ResponseEntity.ok(noteRepository.findByStream(stream));
		}
	}
}
