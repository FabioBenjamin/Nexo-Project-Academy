package com.nexo.models.classes;
import com.nexo.models.enums.StudyTestStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "studytest")
public class StudyTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studytest_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "studytrail_id")
    private StudyTrail studyTrail;

    @Enumerated(EnumType.STRING)
    // tells Hibernate that the field is an enum named in databank instead just a simple varchar
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private StudyTestStatus status;

    @Column(name = "passing_score", nullable = false)
    private int passingScore;

    // references the attribute name in Question class
    // mappedBy allows bidirectional navigation
    @OneToMany(mappedBy = "studyTest", cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "studyTest", cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<TestAttempt> testAttempts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public StudyTrail getStudyTrail() {
        return studyTrail;
    }

    public StudyTestStatus getStatus() {
        return status;
    }

    public void setStudyTrail(StudyTrail studyTrail) {
        this.studyTrail = studyTrail;
    }

    public void setStatus(StudyTestStatus status) {
        this.status = status;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<TestAttempt> getTestAttempts(){ return testAttempts; }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }
}
