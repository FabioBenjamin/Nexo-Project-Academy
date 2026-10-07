package com.nexo.nexo.service;

import com.nexo.nexo.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

    // final creates constant var. So there will be only one topicRepository instance
    // that will be created once and cannot be modified
    // dependencies
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository){
        this.topicRepository = topicRepository;
    }

}
