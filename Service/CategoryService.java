package com.example.NoteTakingApp.Service; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.Category;
import com.example.NoteTakingApp.Model.Note;
import com.example.NoteTakingApp.Repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Needed for lazy loading access

import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    // NotesRepository likely not needed here unless you modify notes *from* category service

    // Use final fields with constructor injection
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // --- CRUD and Business Logic Methods ---

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Example: Get Category by ID (useful for controllers)
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }


    // Renamed method, accepts ID, handles potential lazy loading
    @Transactional(readOnly = true) // Keep session open to access notes if LAZY fetched
    public Set<Note> getNotesByCategoryId(Long categoryId) {
        Category category = getCategoryById(categoryId); // Reuse get method
        // Access notes within the transaction
        // Force initialization if necessary, or DTO mapping handles it
        // Hibernate.initialize(category.getNotes()); // One way to force load
        return category.getNotes();
    }

    // Handles invalid category name, removes redundant setter
    public Category addCategory(String categoryName) {
        Category.CategoryType categoryType;
        try {
            // Consider case-insensitivity
            categoryType = Category.CategoryType.valueOf(categoryName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category type: " + categoryName);
        }

        Category newCategory = new Category(categoryType);
        // Redundant call removed: newCategory.setCategoryType(categoryType);
        return categoryRepository.save(newCategory);
    }

    // Added delete method - Requires careful consideration of notes associated
    public void deleteCategoryById(Long categoryId) {
        Category category = getCategoryById(categoryId); // Check existence first

        // **Important Logic Needed:** What happens to Notes associated with this Category?
        // Option 1: Disallow deletion if notes are associated (check category.getNotes().isEmpty())
        // Option 2: Remove the category from all associated notes (requires iterating notes and saving them)
        // Option 3: Rely on database constraints (if any)

        // Simple deletion (assumes notes handle the removal or it's okay to leave dangling refs - usually bad)
        // if (!category.getNotes().isEmpty()) {
        //     throw new BadRequestException("Cannot delete category with associated notes.");
        // }
        categoryRepository.delete(category);
    }
}