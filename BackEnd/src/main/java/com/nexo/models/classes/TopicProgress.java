package com.nexo.models.classes;

import com.nexo.models.enums.TopicProgressStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name="topicprogress")
public class TopicProgress {


    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "progress_id")
    private Long id;

    @OneToOne // 1:1
    @JoinColumn(name = "topic_id", unique = true) // Find FK
    private Topic topic;

    @Enumerated(EnumType.STRING)
    // tells Hibernate that the field is a enum named in databank instead just a simple varchar
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TopicProgressStatus status;

    @Column(nullable = false, name = "scheduled_date")
    private LocalDate targetDate;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @Column(name = "xp_awarded", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean xpAwarded;

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

    public Boolean isXpAwarded(){ return xpAwarded; }

    public void setTopic(Topic topic) { this.topic = topic; }

    public void setStatus(TopicProgressStatus status) {
        this.status = status;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }

    public void setXpAwarded(Boolean xpAwarded) {this.xpAwarded = xpAwarded; }
}
