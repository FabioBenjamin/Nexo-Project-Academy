package com.nexo.integration.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.integration.dto.GeminiResponse;
import com.nexo.integration.dto.GeminiTrailResponse;
import com.nexo.integration.schemas.GeminiResponseSchema;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Service
public class GeminiClient {

    private final Client client;
    private final ObjectMapper objectMapper; // used to convert json into Java object

    public GeminiClient(Client client, ObjectMapper objectMapper){
        this.client = client;
        this.objectMapper = objectMapper;
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

    public GeminiResponse generateTrail(StudyTrailRequestDTO dto, LocalDate startDate){
        // define the configs
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")  //return a json
                .responseSchema(GeminiResponseSchema.RESPONSE_SCHEMA) // follow this schema
                .build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.5-flash-lite",
                        GeminiPrompt.trailPrompt(dto, startDate) + GeminiPrompt.TEST_PROMPT,
                        config
                );

        try{
            // convert Json response into a Java class
            return objectMapper.readValue(
                    response.text(),
                    GeminiResponse.class
            );
        } catch (DatabindException e){
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
}
