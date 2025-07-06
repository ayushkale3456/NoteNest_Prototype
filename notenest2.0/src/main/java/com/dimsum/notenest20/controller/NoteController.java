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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteRepository noteRepository;

    @Value("${upload.dir:uploads}") // Default folder if not configured
    private String uploadDir;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // -------------------- Upload PDF --------------------
    @PostMapping("/upload")
    public ResponseEntity<?> uploadNote(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("stream") String stream,
            @RequestParam("year") String year,
            @RequestParam("uploadedBy") String uploadedBy) {

        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String filename = UUID.randomUUID() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save note with file link
            Note note = new Note();
            note.setTitle(title);
            note.setStream(stream);
            note.setYear(year);
            note.setUploadedBy(uploadedBy);
            note.setFileLink("/api/notes/files/" + filename);

            Note saved = noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    // -------------------- Serve PDF --------------------
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -------------------- Get All Notes --------------------
    @GetMapping("/all")
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    // -------------------- Delete Note --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        if (!noteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Note note = noteRepository.findById(id).get();

        // Delete associated file
        if (note.getFileLink() != null) {
            String filename = Paths.get(note.getFileLink()).getFileName().toString();
            Path filePath = Paths.get(uploadDir).resolve(filename);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }

        noteRepository.deleteById(id);
        return ResponseEntity.ok("Note deleted successfully.");
    }

    // -------------------- Update Note (metadata only) --------------------
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
