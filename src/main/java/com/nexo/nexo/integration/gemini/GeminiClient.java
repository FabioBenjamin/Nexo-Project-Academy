package com.nexo.nexo.integration.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiClient {

    private final Client client;

    public GeminiClient(Client client){
        this.client = client;
    }

    public String test(){
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.5-flash-lite",
                        "Responda o que é uma API",
                        null
                );

        return response.text();
    }
}
