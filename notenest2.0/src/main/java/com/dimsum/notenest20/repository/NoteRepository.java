package com.dimsum.notenest20.repository;

import com.dimsum.notenest20.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
	List<Note> findByStreamAndYear(String stream, String year);

	List<Note> findByStream(String stream);
	
	List<Note> findByUniversity(String university);
}
