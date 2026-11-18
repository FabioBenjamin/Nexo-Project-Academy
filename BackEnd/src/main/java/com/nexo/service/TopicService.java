package com.nexo.service;

import com.nexo.models.classes.Topic;
import com.nexo.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService {

    // final creates constant var. So there will be only one topicRepository instance
    // that will be created once and cannot be modified
    // dependencies
    private final TopicRepository topicRepository;

    // Construtor
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }
}