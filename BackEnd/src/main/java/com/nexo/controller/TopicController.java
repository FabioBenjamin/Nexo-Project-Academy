package com.nexo.controller;

import com.nexo.models.classes.Topic;
import com.nexo.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/topic") // Sujeito a alterações
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/{topicId}/complete")
    public ResponseEntity<List<Topic>> getTopicsByTrail(@PathVariable Long trailId) {
        return ResponseEntity.ok(topicService.getTopicsByTrailId(trailId));
    }

    // Mark the topic as completed
    // Update the user's achievements
    @PostMapping("/{topicId}/completed")
    public ResponseEntity<Void> completeTopic(@RequestParam Long trailId, @PathVariable Long topicId) {
        topicService.completeTopic(trailId, topicId);

        return ResponseEntity.ok().build();
    }
}
