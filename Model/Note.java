package com.example.NoteTakingApp.Model; // Keep package name for now, consider lowercase convention later

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
// Use conventional lowercase table name
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long noteId;

    @Column(name = "content", nullable = false, length = 200) // length is okay for String
    private String content; // Renamed field to match getter/setter

    // Removed inappropriate 'length' attribute for LocalDate
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // FetchType.LAZY is default for ManyToOne, often desirable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Conventional FK name
    // Field name 'user' (lowercase) is correct for mappedBy in User entity
    private User user;

    @ManyToMany(fetch = FetchType.LAZY) // LAZY is often preferred for collections
    @JoinTable(
            name = "note_category", // Join table name convention
            joinColumns = @JoinColumn(name = "note_id"), // FK name convention
            inverseJoinColumns = @JoinColumn(name = "category_id")) // FK name convention
    private Set<Category> categories = new HashSet<>(); // Initialize collection

    // JPA requires a no-arg constructor
    public Note(){}

    // Constructor with corrected field names
    public Note(String content, LocalDate date, User user, Set<Category> categories){
        this.content = content;
        this.date = date;
        this.user = user;
        this.categories = categories; // Be careful passing managed entities here
    }

    // --- Getters and Setters with correct naming ---

    public Long getNoteId() { // Renamed getId() for clarity
        return noteId;
    }

    // No setter for Id

    // Corrected getter/setter names for content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    // --- hashCode, equals, toString ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        // Use ID for equality check if available
        return Objects.equals(noteId, note.noteId);
    }

    @Override
    public int hashCode() {
        // Use ID for hashCode if available
        return Objects.hash(noteId);
    }


    @Override
    public String toString() {
        // Exclude related entities or only include their IDs in toString
        return "Note{" +
                "noteId=" + noteId +
                ", content='" + content + '\'' + // Use correct field name
                ", date=" + date + // Removed unnecessary quotes for date
                ", userId=" + (user != null ? user.getUserId() : null) + // Safely access user ID
                // Optionally include category IDs if needed, carefully
                // ", categoryIds=" + categories.stream().map(Category::getCategoryId).collect(Collectors.toSet()) +
                '}';
    }
}