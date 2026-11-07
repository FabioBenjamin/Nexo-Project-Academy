package com.nexo.models.classes;

import com.nexo.models.enums.StudyTrailStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // references the attribute name in Topic class
    // mappedBy allows bidirectional navigation
    @OneToMany(mappedBy = "studyTrail", cascade = CascadeType.PERSIST)
    @OrderBy("orderIndex ASC") // the item name is the attribute name of Java class
    @OnDelete(action = OnDeleteAction.CASCADE) // delegate the delegate responsibility to data bank
    private List<Topic> topics = new ArrayList<>();

    @OneToOne(mappedBy = "studyTrail", cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StudyTest studyTest;

    @Column(nullable = false, length = 80)
    private String title;

    @Column
    private String description;

    @Column(name = "start_date", columnDefinition = "DATE DEFAULT CURRENT_DATE", insertable = false)
    private LocalDate startDate;

    @Column(name="target_date", nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    // tells Hibernate that the field is enum named in databank instead just a simple varchar
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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

    public LocalDate getTargetDate() {
        return targetDate;
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

    public void setTargetDate(LocalDate tagetDate) {
        this.targetDate = tagetDate;
    }

    public void setStatus(StudyTrailStatus status) {
        this.status = status;
    }

    public void setUser(User user) { this.user = user; }

    public List<Topic> getTopics() { return topics; }

    public StudyTest getStudyTest() {
        return studyTest;
    }

    public void setStudyTest(StudyTest studyTest) {
        this.studyTest = studyTest;
    }
}