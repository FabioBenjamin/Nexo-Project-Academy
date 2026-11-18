package com.nexo.models.classes;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topic")
public class Topic {

    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "topic_id")
    private Long id;

    @ManyToOne // N:1
    @JoinColumn(name = "studytrail_id") // Find FK
    private StudyTrail studyTrail;

    @ManyToOne(fetch = FetchType.LAZY) // N:1 -> in this case: auto relationship
    @JoinColumn(name = "parent_id")
    private Topic parentTopic;

    @OneToMany(mappedBy = "parentTopic", cascade = CascadeType.PERSIST)
    @OrderBy("orderIndex ASC") // the item name(orderIndex) is the attribute name of Java class
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Topic> subTopics = new ArrayList<>();

    @OneToOne(mappedBy = "topic", cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TopicProgress progress;

    @Column(length = 80, nullable = false)
    private String title;

    @Column(length = 200)
    private String description;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    public Long getId() {
        return id;
    }

    public StudyTrail getStudyTrail() {
        return studyTrail;
    }

    public Topic getParentTopic() {
        return parentTopic;
    }

    public String getTitle() {
        return title;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setStudyTrail(StudyTrail studyTrail) {
        this.studyTrail = studyTrail;
    }

    public void setParentTopic(Topic parentTopic) {
        this.parentTopic = parentTopic;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public TopicProgress getProgress() {
        return progress;
    }

    public List<Topic> getSubTopics() {
        return subTopics;
    }

    public void setProgress(TopicProgress progress) {
        this.progress = progress;

        if(progress != null){
            progress.setTopic(this);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
