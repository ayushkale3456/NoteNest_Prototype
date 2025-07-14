//package com.dimsum.notenest20.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.dimsum.notenest20.model.Note;
//import com.dimsum.notenest20.repository.NoteRepository;
//
//
//
//@RestController
//@RequestMapping("/api/notes")
//@CrossOrigin(origins = "*")
//public class NoteController {
//
//	private NoteRepository noteRepository;
//
//	@Autowired
//	public NoteController(NoteRepository noteRepository) {
//		this.noteRepository = noteRepository;
//	}
//
//	@PostMapping("/upload")
//	public ResponseEntity<?> uploadNote(@RequestBody Note note) {
//		noteRepository.save(note);
//		return ResponseEntity.ok("Note uploaded successfully");
//	}
//	
//	@PutMapping("/edit/{id}")
//	public ResponseEntity<?> editNote(@PathVariable Long id, @RequestBody Note updatedNote) {
//	  return noteRepository.findById(id)
//	    .map(note -> {
//	      note.setTitle(updatedNote.getTitle());
//	      note.setStream(updatedNote.getStream());
//	      note.setYear(updatedNote.getYear());
//	      note.setFileLink(updatedNote.getFileLink());
//	      note.setUploadedBy(updatedNote.getUploadedBy());
//	      noteRepository.save(note);
//	      return ResponseEntity.ok("Note updated successfully");
//	    }).orElse(ResponseEntity.status(404).body("Note not found"));
//	}
//
//	@DeleteMapping("/delete/{id}")
//	public ResponseEntity<?> deleteNote(@PathVariable Long id) {
//	  if (!noteRepository.existsById(id)) {
//	    return ResponseEntity.status(404).body("Note not found");
//	  }
//	  noteRepository.deleteById(id);
//	  return ResponseEntity.ok("Note deleted successfully");
//	}
//
//	@GetMapping("/all")
//	public ResponseEntity<?> getAllNotes() {
//		return ResponseEntity.ok(noteRepository.findAll());
//	}
//
//	@GetMapping
//	public ResponseEntity<?> getNotesByStreamAndYear(@RequestParam String stream,
//			@RequestParam(required = false) String year) {
//		if (year != null) {
//			return ResponseEntity.ok(noteRepository.findByStreamAndYear(stream, year));
//		} else {
//			return ResponseEntity.ok(noteRepository.findByStream(stream));
//		}
//	}
//}

// Updated code for pdf

package com.dimsum.notenest20.controller;

import com.dimsum.notenest20.model.Note;
import com.dimsum.notenest20.repository.NoteRepository;

import java.io.*;
import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

	private final NoteRepository noteRepository;

	public NoteController(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	// Upload PDF directly to DB
	@PostMapping(value = "/upload")
	public ResponseEntity<?> uploadNote(@RequestParam("file") MultipartFile file, @RequestParam("title") String title,
			@RequestParam("stream") String stream, @RequestParam("year") String year,
			@RequestParam("uploadedBy") String uploadedBy) {

		try {
			Note note = new Note();
			note.setTitle(title);
			note.setStream(stream);
			note.setYear(year);
			note.setUploadedBy(uploadedBy);
			note.setFileName(file.getOriginalFilename());
			note.setFileData(file.getBytes());

			Note saved = noteRepository.save(note);
			return ResponseEntity.status(HttpStatus.CREATED).body("Note uploaded successfully with ID: " + saved.getId());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save file.");
		}
	}

	// Serve PDF from DB
	@GetMapping("/file/{id}")
	public ResponseEntity<byte[]> downloadNote(@PathVariable Long id) {
		return noteRepository.findById(id).map(note -> {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDisposition(ContentDisposition.inline().filename(note.getFileName()).build());
			return new ResponseEntity<>(note.getFileData(), headers, HttpStatus.OK);
		}).orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/stream/{stream}")
	public List<Note> getNotesByStream(@PathVariable String stream) {
	    return noteRepository.findByStream(stream);
	}


	@GetMapping("/all")
	public List<Note> getAllNotes() {
		return noteRepository.findAll();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteNote(@PathVariable Long id) {
		if (!noteRepository.existsById(id))
			return ResponseEntity.notFound().build();
		noteRepository.deleteById(id);
		return ResponseEntity.ok("Note deleted.");
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Note updatedNote) {
		return noteRepository.findById(id).map(note -> {
			note.setTitle(updatedNote.getTitle());
			note.setStream(updatedNote.getStream());
			note.setYear(updatedNote.getYear());
			note.setUploadedBy(updatedNote.getUploadedBy());
			return ResponseEntity.ok(noteRepository.save(note));
		}).orElse(ResponseEntity.notFound().build());
	}
}
