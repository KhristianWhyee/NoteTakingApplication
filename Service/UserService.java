package com.example.NoteTakingApp.Service; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.Note;
import com.example.NoteTakingApp.Model.User;
import com.example.NoteTakingApp.Repository.UserRepository;
// Import custom exceptions

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For lazy loading access

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    // NotesRepository might not be needed if all note access is via User.notes
    // private final NotesRepository notesRepository;

    // Constructor injection
    public UserService(UserRepository userRepository /*, NotesRepository notesRepository */) {
        this.userRepository = userRepository;
        // this.notesRepository = notesRepository;
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    // Throws exception if not found
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public User createUser(User user) {
        // Add validation? Check if email already exists?
        // Example check:
        // if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        //    throw new BadRequestException("Email already exists: " + user.getEmail());
        // }
        return userRepository.save(user);
    }

    // Updated to include occupation, throws exception
    @Transactional // Good practice for read-then-write operations
    public User updateUser(Long userId, User userDetails) { // Parameter order corrected
        User userToUpdate = getUserById(userId); // Checks existence

        userToUpdate.setName(userDetails.getName());
        userToUpdate.setEmail(userDetails.getEmail());
        // Update occupation as well (based on previous discussion)
        userToUpdate.setOccupation(userDetails.getOccupation());

        return userRepository.save(userToUpdate);
    }

    // Throws exception if not found
    @Transactional // Deletion might involve cascades or checks
    public void deleteUserById(Long userId) {
        // Check if user exists first
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        // Deletion will cascade to Notes if configured via CascadeType.ALL/orphanRemoval=true
        userRepository.deleteById(userId);
    }

    // Handles potential lazy loading exception, throws if user not found
    @Transactional(readOnly = true) // Keeps session open for accessing notes
    public List<Note> findNotesByUserId(Long userId) {
        User user = getUserById(userId); // Checks existence
        // Access notes within the transaction
        // Initialize or map to DTOs if needed before returning
        // Hibernate.initialize(user.getNotes()); // Force load if needed
        return user.getNotes();
    }

    // Example: Method to delete all notes for a user (if needed by controller)
    @Transactional
    public void deleteAllNotesForUser(Long userId) {
        User user = getUserById(userId);
        // Because of orphanRemoval=true, clearing the list and saving the user should trigger note deletion
        user.getNotes().clear();
        userRepository.save(user);
        // Alternatively, if notes repo available: notesRepository.deleteAll(user.getNotes()); but orphanRemoval handles it.
    }
}