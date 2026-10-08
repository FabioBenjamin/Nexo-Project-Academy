package com.nexo.integration.gemini;

import com.google.genai.Client;
import com.nexo.integration.dto.GeminiTrailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeminiControllerTest {

    private final GeminiClient geminiClient;

    public GeminiControllerTest(GeminiClient geminiClient){ this.geminiClient = geminiClient; }

    @GetMapping("/gemini/test")
    public ResponseEntity<GeminiTrailResponse> genTrail(@RequestBody String request){

        return ResponseEntity.ok(geminiClient.generateTrail(request));
    }
}
