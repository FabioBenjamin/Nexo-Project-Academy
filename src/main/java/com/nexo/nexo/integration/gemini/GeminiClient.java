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
                        "gemini-2.5-flash",
                        "Responda apenas: OK",
                        null
                );

        return response.text();
    }
}
