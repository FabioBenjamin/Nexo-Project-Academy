package com.nexo.nexo.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "studytrail")
public class StudyTrail {

    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "studytrail_id")
    private Long id;

    @ManyToOne // N:1
    @JoinColumn(name = "user_id") // Find FK
    private User user;

    @Column(nullable = false, length = 80)
    private String title;

    @Column
    private String description;

    @Column(name = "start_date", columnDefinition = "DATE DEFAULT CURRENT_DATE", insertable = false)
    private LocalDate startDate;

    @Column(name="target_date", nullable = false)
    private LocalDate tagetDate;

    @Enumerated(EnumType.STRING)
    private StudyTrailStatus status;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getTagetDate() {
        return tagetDate;
    }

    public StudyTrailStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTagetDate(LocalDate tagetDate) {
        this.tagetDate = tagetDate;
    }

    public void setStatus(StudyTrailStatus status) {
        this.status = status;
    }
}
