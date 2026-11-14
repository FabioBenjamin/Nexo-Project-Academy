package com.nexo.controller;

import com.nexo.dto.TestAttemptResultRequest;
import com.nexo.dto.TestAttemptResultResponse;
import com.nexo.service.TestAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tests")
public class TestAttemptController {

    private final TestAttemptService testAttemptService;

    public TestAttemptController(
            TestAttemptService testAttemptService
    ){
        this.testAttemptService = testAttemptService;
    }

    @PostMapping("/{id}/attempts")
    public ResponseEntity<TestAttemptResultResponse> checkTestAttemptResult(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long studyTestId,
            @RequestBody TestAttemptResultRequest resultRequest
            ){

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity
                .ok(testAttemptService.checkTestAttemptResult(
                        studyTestId,
                        userId,
                        resultRequest
                ));
    }
}
