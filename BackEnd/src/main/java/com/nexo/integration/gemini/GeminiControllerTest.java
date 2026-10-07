package com.nexo.integration.gemini;

import com.google.genai.Client;
import com.nexo.nexo.integration.dto.GeminiTrailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeminiControllerTest {

    private final GeminiClient geminiClient;

    public GeminiControllerTest(GeminiClient geminiClient){ this.geminiClient = geminiClient; }

    @GetMapping("/gemini/test")
    public ResponseEntity<GeminiTrailResponse> genTrail(){
        String prompt = """
                     Crie uma trilha de estudos para aprender
                     programação competitiva.
                
                     O estudante é iniciante e possui 5 horas
                     disponíveis por semana.
                
                     Organize os assuntos em uma ordem progressiva.
                """;

        return ResponseEntity.ok(geminiClient.generateTrail(prompt));
    }
}
