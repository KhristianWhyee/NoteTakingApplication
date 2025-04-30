package com.example.NoteTakingApp.Model; // Keep package name for now, consider lowercase convention later

import jakarta.persistence.*;
import java.util.ArrayList; // Use ArrayList for initialization
import java.util.List;
import java.util.Objects;

@Entity
// Use conventional table name, avoid reserved keyword "User"
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // Consider adding @Email validation and unique constraint
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "occupation", nullable = false, length = 50)
    private String occupation;

    // Correct mappedBy to use the field name in the Note entity ("user")
    // CascadeType.ALL and orphanRemoval=true means deleting a User deletes their Notes. Ensure this is desired.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // Initialize collections

    private List<Note> notes = new ArrayList<>();

    // JPA requires a no-arg constructor
    public User(){}

    public User(String name, String email, String occupation){
        this.name = name;
        this.email = email;
        this.occupation = occupation;
    }

    // --- Standard Getters and Setters ---

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Use ID or unique email for equality
        return Objects.equals(userId, user.userId) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        // Use ID or unique email for hashcode
        return Objects.hash(userId, email);
    }


    @Override
    public String toString() {
        // Exclude collections from default toString
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}