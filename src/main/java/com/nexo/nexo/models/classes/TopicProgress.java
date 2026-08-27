package com.nexo.nexo.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="topicprogress")
public class TopicProgress {


    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "topicprogress_id")
    private Long id;

    @ManyToOne // N:1
    @JoinColumn(name = "topic_id") // Find FK
    private Topic topic;

    @Enumerated(EnumType.STRING)
    private TopicProgressStatus status;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE", insertable = false)
    private LocalDate completedAt;


    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public TopicProgressStatus getStatus() {
        return status;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void setStatus(TopicProgressStatus status) {
        this.status = status;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }
}
