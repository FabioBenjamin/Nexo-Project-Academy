package com.nexo.integration.gemini;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.integration.dto.GeminiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class GeminiControllerTest {

    private final GeminiClient geminiClient;

    public GeminiControllerTest(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @PostMapping("/gemini/test")
    public ResponseEntity<GeminiResponse> genTrail(@RequestBody StudyTrailRequestDTO dto) {
        String testLearningProfile = "Perfil prático e estruturado: priorize exemplos reais, exercícios de fixação e tópicos objetivos.";
        return ResponseEntity.ok(geminiClient.generateTrail(dto, LocalDate.now(), testLearningProfile));
    }
}