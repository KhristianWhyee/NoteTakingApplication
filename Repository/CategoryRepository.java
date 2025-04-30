package com.example.NoteTakingApp.Repository; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.Category; // Ensure correct import path
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Technically optional if component scanning finds it, but good practice
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Add custom query methods here if needed
}