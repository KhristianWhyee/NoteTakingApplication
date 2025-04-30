package com.example.NoteTakingApp.Model; // Keep package name for now, consider lowercase convention later

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
// Use conventional lowercase table name
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // Standard Java naming convention for fields
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    // mappedBy should reference the field name in the Note entity
    @ManyToMany(mappedBy = "categories")
    // Initialize collections to avoid potential NullPointerExceptions
    private Set<Note> notes = new HashSet<>();

    public enum CategoryType{
        PERSONAL,
        WORK,
        STUDY
    }

    public Category(){}

    public Category(CategoryType categoryType){
        this.categoryType = categoryType;
    }

    public Long getCategoryId() {
        return categoryId;
    }


    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    // --- hashCode, equals, toString ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        // Use ID for equality check if available, otherwise rely on type.
        // Be cautious with generated IDs before persistence.
        return Objects.equals(categoryId, category.categoryId) ||
                (categoryId == null && category.categoryId == null && categoryType == category.categoryType);
    }

    @Override
    public int hashCode() {
        // Use ID for hashCode if available, otherwise use type.
        return Objects.hash(categoryId, categoryType);
    }

    @Override
    public String toString() {
        // Exclude collections from default toString to avoid LazyInitializationException and performance issues
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryType=" + categoryType +
                '}';
    }
}