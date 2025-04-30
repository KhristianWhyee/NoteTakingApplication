package com.example.NoteTakingApp.Repository; // Keep package name for now, consider lowercase convention later

import com.example.NoteTakingApp.Model.User; // Ensure correct import path
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Technically optional
public interface UserRepository extends JpaRepository<User, Long> {
    // Add custom query methods here if needed (e.g., findByEmail)
    // Optional<User> findByEmail(String email);
}