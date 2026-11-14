package com.nexo.models.classes;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "testattempt")
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testattempt_id")
    private Long id;

    @Column
    private int score;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "studytest_id")
    private StudyTest studyTest;

    public Long getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public StudyTest getStudyTest() {
        return studyTest;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStudyTest(StudyTest studyTest) {
        this.studyTest = studyTest;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
