package com.nexo.models.classes;
import jakarta.persistence.*;

@Entity
@Table(name = "alternative")
public class Alternative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alternative_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_correct", nullable = false)
    private Boolean correct;

    public Long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isCorrect() {
        return correct;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}
