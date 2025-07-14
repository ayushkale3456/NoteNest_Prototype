package com.dimsum.notenest20.controller;

import com.dimsum.notenest20.model.Project;
import com.dimsum.notenest20.repository.ProjectRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

	private final ProjectRepository repo;

	public ProjectController(ProjectRepository repo) {
		this.repo = repo;
	}

	// Upload link-based project
	@PostMapping("/link")
	public ResponseEntity<Project> uploadLink(@RequestBody Project project) {
		project.setFileType("link");
		Project saved = repo.save(project);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	// Upload file-based project
	@PostMapping("/upload")
	public ResponseEntity<Project> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String title,
			@RequestParam String description, @RequestParam String stream, @RequestParam String year,
			@RequestParam String uploadedBy, @RequestParam String fileType) throws IOException {
		Project p = new Project();
		p.setTitle(title);
		p.setDescription(description);
		p.setStream(stream);
		p.setYear(year);
		p.setUploadedBy(uploadedBy);
		p.setFileType(fileType);
		p.setFileName(file.getOriginalFilename());
		p.setFileData(file.getBytes());
		p.setFileUrl(null);
		Project saved = repo.save(p);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	// Get all projects
	@GetMapping("/all")
	public List<Project> getAll() {
		return repo.findAll();
	}

	// Get projects filtered by stream
	@GetMapping("/stream/{stream}")
	public ResponseEntity<List<Project>> getByStream(@PathVariable String stream) {
	    List<Project> projects = repo.findByStreamIgnoreCase(stream);
	    return ResponseEntity.ok(projects);
	}
	
	// Delete project
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if (!repo.existsById(id))
			return ResponseEntity.notFound().build();
		repo.deleteById(id);
		return ResponseEntity.ok().build();
	}

	// Serve project file
	@GetMapping("/file/{id}")
	public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
		Optional<Project> opt = repo.findById(id);
		if (opt.isEmpty() || !"file".equals(opt.get().getFileType()))
			return ResponseEntity.notFound().build();

		Project p = opt.get();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition.attachment().filename(p.getFileName()).build());

		return new ResponseEntity<>(p.getFileData(), headers, HttpStatus.OK);
	}

	// Update metadata only (title, desc, stream, year)
	@PutMapping("/{id}")
	public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project updated) {
		return repo.findById(id).map(p -> {
			p.setTitle(updated.getTitle());
			p.setDescription(updated.getDescription());
			p.setStream(updated.getStream());
			p.setYear(updated.getYear());
			return ResponseEntity.ok(repo.save(p));
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

}
