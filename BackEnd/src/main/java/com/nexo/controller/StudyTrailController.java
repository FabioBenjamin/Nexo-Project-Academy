package com.nexo.controller;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.service.StudyTrailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudyTrailController {

    private final StudyTrailService studyTrailService;

    public StudyTrailController(StudyTrailService studyTrailService){
        this.studyTrailService = studyTrailService;
    }

    @PostMapping("/trail")
    public ResponseEntity<Long> createStudyTrail(
            @RequestBody StudyTrailRequestDTO dto,
            // Spring receives access token, JwtDecoder validate, AuthenticationPrincipal receives the
            // principal Object which is a Jwt
            @AuthenticationPrincipal Jwt jwt){

        Long user_id = Long.valueOf(jwt.getSubject());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studyTrailService.createStudyTrail(dto, user_id));
    }
}
