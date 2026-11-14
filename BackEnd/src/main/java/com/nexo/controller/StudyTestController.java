package com.nexo.controller;

import com.nexo.dto.StudyTestResponse;
import com.nexo.models.classes.StudyTest;
import com.nexo.service.StudyTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
public class StudyTestController {

    private StudyTestService studyTestService;

    public StudyTestController(
            StudyTestService studyTestService
    ){
        this.studyTestService = studyTestService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyTestResponse> getStudyTest(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long studyTestId
            ){

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity
                .ok(studyTestService.getStudyTest(studyTestId, userId));
    }
}
