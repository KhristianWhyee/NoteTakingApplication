package com.example.NoteTakingApp.Controller; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.Category; // DTO Recommended
import com.example.NoteTakingApp.Model.Note;    // DTO Recommended
import com.example.NoteTakingApp.Model.User;    // Needed for addNote logic
import com.example.NoteTakingApp.Service.NoteService;
import com.example.NoteTakingApp.Service.UserService;
// Import other exceptions as needed, e.g., BadRequestException

import org.springframework.http.HttpStatus; // For HttpStatus enum
import org.springframework.http.ResponseEntity; // For returning responses
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/notes") // Base path for note-related endpoints
public class NoteController {

    // Use final fields with constructor injection
    private final NoteService noteService;
    private final UserService userService;
    // CategoryService might not be needed here unless note endpoints directly interact with it beyond association
    // private final CategoryService categoryService;

    // Constructor Injection
    public NoteController(NoteService noteService, UserService userService /*, CategoryService categoryService */) {
        this.noteService = noteService;
        this.userService = userService;
        // this.categoryService = categoryService;
    }

    // POST /notes/{userId} - Endpoint design is still a bit unconventional, but keeping logic
    // Consider POST /notes and including userId in the RequestBody DTO
    @PostMapping("/{userId}")
    public ResponseEntity<?> addNote(@PathVariable Long userId, @RequestBody Note note) { // DTO Recommended for Note
        try {
            User user = userService.getUserById(userId); // throws ResourceNotFoundException
            note.setUser(user);
            Note createdNote = noteService.createNote(note); // Assumes user is set
            // DTO Recommended for return
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating note.");
        }
    }

    // PUT /notes/{userId}/{noteId} - Update existing note
    @PutMapping("/{userId}/{noteId}")
    public ResponseEntity<?> updateNote(@PathVariable Long userId, @PathVariable Long noteId, @RequestBody Note noteDetails) { // DTO Recommended for Note
        try {
            Note updatedNote = noteService.updateNote(userId, noteId, noteDetails); // Throws exceptions
            // DTO Recommended for return
            return ResponseEntity.ok(updatedNote);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating note.");
        }
    }

    // DELETE /notes/{userId}/{noteId} - Delete existing note
    @DeleteMapping("/{userId}/categories/{categoryId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long userId, @PathVariable Long noteId) {
        try {
            noteService.deleteNote(userId, noteId); // Throws exceptions
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /notes/{noteId}/categories/{categoryId} - Associate note with category
    @PostMapping("/{noteId}/categories")
    public ResponseEntity<?> addNoteToCategory(@PathVariable Long noteId, @PathVariable Long categoryId) {
        try {
            Set<Category> updatedCategories = noteService.assignToCategory(noteId, categoryId); // Throws exceptions
            // DTO Recommended for Set<Category>
            return ResponseEntity.ok(updatedCategories);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error assigning category to note.");
        }
    }

    // GET /notes - Get all notes (Use with caution - pagination recommended)
    @GetMapping
    public ResponseEntity<List<Note>> findAllNotes() { // DTO Recommended for List<Note>
        List<Note> notes = noteService.findAll();
        return ResponseEntity.ok(notes);
    }

    // GET /notes/{noteId} - Get a specific note by ID
    @GetMapping("/{noteId}")
    public ResponseEntity<?> findNoteById(@PathVariable Long noteId) { // DTO Recommended for Note
        // Using Optional return from service
        Optional<Note> noteOptional = noteService.findNoteById(noteId);
        // You can map the Optional to ResponseEntity directly
        return noteOptional.<ResponseEntity<?>>map(ResponseEntity::ok) // If present, return 200 OK with note
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Note not found with id: " + noteId)); // If empty, return 404
    }
}