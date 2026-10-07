package com.nexo.integration.gemini;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    // creates a Client instance when we need, and automatically inject where is necessary
    @Bean
    public Client geminiApiClient(){
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }
}
