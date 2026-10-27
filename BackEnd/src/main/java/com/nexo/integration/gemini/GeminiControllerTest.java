package com.nexo.integration.gemini;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.integration.dto.GeminiTrailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class GeminiControllerTest {

    private final GeminiClient geminiClient;

    public GeminiControllerTest(GeminiClient geminiClient){ this.geminiClient = geminiClient; }

    @GetMapping("/gemini/test")
    public ResponseEntity<GeminiTrailResponse> genTrail(@RequestBody StudyTrailRequestDTO dto){

        return ResponseEntity.ok(geminiClient.generateTrail(dto, LocalDate.now()));
    }
}
