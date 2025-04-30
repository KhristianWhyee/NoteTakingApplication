package com.example.NoteTakingApp.Service; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.Category;
import com.example.NoteTakingApp.Model.Note;
import com.example.NoteTakingApp.Repository.CategoryRepository;
import com.example.NoteTakingApp.Repository.NotesRepository;
import com.example.NoteTakingApp.Repository.UserRepository;
// Import custom exceptions

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Often useful for update/delete spanning checks

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class NoteService {

    private final NotesRepository notesRepository;
    private final UserRepository userRepository; // Needed for associating user/checking ownership
    private final CategoryRepository categoryRepository; // Needed for assigning category

    // Use final fields with constructor injection
    public NoteService(NotesRepository notesRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.notesRepository = notesRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Note> findAll() {
        // Consider pagination for large datasets
        return notesRepository.findAll();
    }

    // Renamed findNoteById to make it clear it throws exception or returns entity
    public Note getNoteById(Long noteId) {
        return notesRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));
    }

    // Method to find by ID returning Optional (useful for controllers that prefer checking)
    public Optional<Note> findNoteById(Long noteId) {
        return notesRepository.findById(noteId);
    }


    // No user passed here, assume user comes pre-associated with the note object
    // Controller should ensure the user is set correctly before calling this
    public Note createNote(Note note) {
        // Add validation if needed (e.g., ensure user is not null)
        if (note.getUser() == null || note.getUser().getUserId() == null) {
            // This might indicate needing the user ID passed separately
            // Or rely on validation annotations on the DTO/Entity
            throw new IllegalArgumentException("Note must be associated with a user.");
        }
        // Ensure the user actually exists (optional, depends on controller logic)
        // userRepository.findById(note.getUser().getUserId())
        //      .orElseThrow(() -> new ResourceNotFoundException("User specified for note does not exist: " + note.getUser().getUserId()));

        return notesRepository.save(note);
    }

    // Updated with ownership check and correct naming
    @Transactional // Good practice for operations involving multiple checks/saves
    public Note updateNote(Long userId, Long noteId, Note noteDetails) {
        // Fetch the existing note
        Note noteToUpdate = getNoteById(noteId);

        // Check ownership
        if (!noteToUpdate.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("User " + userId + " cannot update note " + noteId);
        }

        // Update fields from noteDetails (use DTO ideally)
        // Use corrected getter/setter name
        noteToUpdate.setContent(noteDetails.getContent());
        noteToUpdate.setDate(noteDetails.getDate());
        // Updating categories/user this way might need care, consider dedicated endpoints
        noteToUpdate.setCategories(noteDetails.getCategories());

        return notesRepository.save(noteToUpdate);
    }

    @Transactional // Good practice for delete involving checks
    public void deleteNote(Long userId, Long noteId) {
        // Fetch the existing note to check ownership
        Note noteToDelete = getNoteById(noteId);

        // Check ownership
        if (!noteToDelete.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("User " + userId + " cannot delete note " + noteId);
        }

        notesRepository.delete(noteToDelete);
    }

    @Transactional
    public Set<Category> assignToCategory(Long noteId, Long categoryId) {
        Note note = getNoteById(noteId);
        // Fetch category using CategoryService or repository
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        note.getCategories().add(category);
        // Saving the note updates the relationship in the join table
        Note savedNote = notesRepository.save(note);
        return savedNote.getCategories(); // Return the updated set
    }

    // Optional: Add method to remove category from note
    @Transactional
    public Set<Category> removeCategoryFromNote(Long noteId, Long categoryId) {
        Note note = getNoteById(noteId);
        Category categoryToRemove = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        note.getCategories().remove(categoryToRemove);
        Note savedNote = notesRepository.save(note);
        return savedNote.getCategories();
    }
}