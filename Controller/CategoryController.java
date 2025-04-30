package com.example.NoteTakingApp.Controller; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.Category; // DTO Recommended
import com.example.NoteTakingApp.Model.Note; // DTO Recommended
import com.example.NoteTakingApp.Service.CategoryService;
// Import custom exceptions

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    // NoteService likely not needed here unless modifying notes directly

    // Constructor Injection
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /categories - Find all
    @GetMapping
    public ResponseEntity<List<Category>> findAllCategories() {
        // DTO Recommended for List<Category>
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // GET /categories/{id} - Find one by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            // DTO Recommended for Category
            return ResponseEntity.ok(category);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // GET /categories/{id}/notes - Find notes for a category
    @GetMapping("/{id}/notes")
    public ResponseEntity<?> findAllNotesInCategory(@PathVariable Long id) {
        try {
            // Call the updated service method
            Set<Note> notes = categoryService.getNotesByCategoryId(id);
            // DTO Recommended for Set<Note>
            return ResponseEntity.ok(notes);
        } catch (ResourceNotFoundException e) { // Catches category not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        // Add catch for potential LazyInitializationException if service isn't Transactional
        catch (Exception e) { // Generic catch for other issues like lazy loading
            // Log exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving notes for category.");
        }
    }

    // POST /categories/{categoryName} - Still less standard, but matches service
    // Consider POST /categories with RequestBody for more standard REST
    @PostMapping
    public ResponseEntity<?> addCategory(@PathVariable String categoryName) {
        try {
            Category createdCategory = categoryService.addCategory(categoryName);
            // DTO Recommended
            // Return 201 Created with the created category (or its location URI)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Log exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating category.");
        }
    }

    // DELETE /categories/{id} - More standard endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (BadRequestException e) { // Catch potential error if category is in use
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return 400 maybe? Or 409 Conflict
        }
        catch (Exception e) {
            // Log exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}