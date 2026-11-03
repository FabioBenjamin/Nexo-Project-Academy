package com.nexo.models.classes;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "studytest_id")
    private StudyTest studyTest;

    @OneToMany(mappedBy = "question")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Alternative> alternatives = new ArrayList<>();

    @Column(nullable = false)
    private String statement;


    public Long getId() {
        return id;
    }

    public StudyTest getStudyTest() {
        return studyTest;
    }

    public String getStatement() {
        return statement;
    }

    public void setStudyTest(StudyTest studyTest) {
        this.studyTest = studyTest;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }
}
