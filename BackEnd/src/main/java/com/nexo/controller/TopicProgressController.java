package com.nexo.controller;

import com.nexo.dto.UpdateTopicProgressRequest;
import com.nexo.models.classes.Topic;
import com.nexo.service.TopicProgressService;
import com.nexo.service.TopicService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicProgressController {

    private final TopicProgressService topicProgressService;

    public TopicProgressController(
            TopicProgressService topicProgressService)
    {
        this.topicProgressService = topicProgressService;
    }


    @PatchMapping("/{id}/progress")
    public ResponseEntity<Void> updateTopicProgress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long topicId,
            @RequestBody UpdateTopicProgressRequest request
            ){

        Long userId = Long.valueOf(jwt.getSubject());

        topicProgressService.updateTopicProgress(userId, topicId, request);

        return ResponseEntity.noContent().build();
    }
}
