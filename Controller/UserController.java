package com.example.NoteTakingApp.Controller;

import com.example.NoteTakingApp.Model.Note;
import com.example.NoteTakingApp.Model.User;
import com.example.NoteTakingApp.Service.NoteService;
import com.example.NoteTakingApp.Service.UserService;
// Import custom exceptions if you have them and use them in specific catches
// import com.example.NoteTakingApp.Exception.ResourceNotFoundException; // Example if you created this

// Import Logger and LoggerFactory
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    // Define a logger instance for this class
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final NoteService noteService;

    // Constructor Injection
    public UserController(UserService userService, NoteService noteService){
        this.userService = userService;
        this.noteService = noteService;
    }

    // POST /users - Create User
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user){
        try {
            User createdUser = userService.createUser(user);
            log.info("Successfully created user with id: {}", createdUser.getUserId()); // Optional: Log success
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } // Add catch for potential specific exceptions first if desired
        catch (Exception e) {
            // Log the exception with stack trace at ERROR level
            log.error("Error creating user: {}", e.getMessage(), e); // Log message and the full exception
            // Return the generic error response to the client
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user.");
        }
    }

    // GET /users/{userId} - Find User by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> findUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) { // Catch specific exception if service throws it
            log.warn("User not found for id: {}", userId); // Log handled exceptions at WARN or INFO
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) { // Catch unexpected exceptions
            log.error("Error finding user by id: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user.");
        }
    }

    // GET /users - Find all users
    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        try {
            List<User> users = userService.getAllUser();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error finding all users", e);
            // Consider what to return here - maybe an empty list with status 500?
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or an error message DTO
        }
    }

    // PUT /users/{userId} - Update User
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User userDetails){
        try {
            User updatedUser = userService.updateUser(userId, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) { // Catch specific exception if service throws it
            log.warn("User not found for update with id: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } // Add catch for potential validation/bad request errors if needed
        catch (Exception e) {
            log.error("Error updating user with id: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user.");
        }
    }

    // DELETE /users/{userId} - Delete User
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) { // Catch specific exception if service throws it
            log.warn("User not found for deletion with id: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting user with id: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- User's Notes Endpoints ---

    // GET /users/{userId}/notes - Corrected logic
    @GetMapping("/{userId}/notes")
    public ResponseEntity<?> findUserNotes(@PathVariable Long userId) {
        try {
            List<Note> notes = userService.findNotesByUserId(userId);
            return ResponseEntity.ok(notes);
        } catch (ResourceNotFoundException e) { // User not found
            log.warn("User not found when finding notes for id: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error finding notes for user id: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user notes.");
        }
    }

    // DELETE /users/{userId}/notes - Corrected mapping and logic
    @DeleteMapping("/{userId}/notes")
    public ResponseEntity<Void> deleteUserNotes(@PathVariable Long userId) {
        try {
            userService.deleteAllNotesForUser(userId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) { // User not found
            log.warn("User not found when deleting notes for id: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting notes for user id: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}