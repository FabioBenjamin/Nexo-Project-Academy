package com.nexo.models.classes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "settings")
public class Settings {

    // Getters and Setters
    @Id // Specifies that this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generates the ID value
    @Column(name = "settings_id") // Maps to the database column name
    private Long id;

    @ManyToOne // N:1 relationship with User
    @JoinColumn(name = "user_id", unique = true, nullable = false) // Maps the Foreign Key column
    private User user;

    @Column(name = "notifications")
    private Boolean notifications = false; // Default value initialized in Java

    @Column(name = "lang", length = 5, nullable = false)
    private String language = "pt-BR"; // Maps Java 'language' to SQL column 'lang'

    @Column(length = 10, nullable = false)
    private String theme = "light";

    @Column(name = "study_weekends")
    private Boolean studyWeekends = true; // Maps camelCase to SQL column 'study_weekends'

    // Default constructor required by JPA
    public Settings() {}

}