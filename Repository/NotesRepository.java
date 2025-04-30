package com.example.NoteTakingApp.Repository; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.Note; // Ensure correct import path
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Technically optional
public interface NotesRepository extends JpaRepository<Note, Long> {
    // Add custom query methods here if needed
    // Example: List<Note> findByUserId(Long userId); // If you want notes directly by user ID
}