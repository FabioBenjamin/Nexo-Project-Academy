package com.nexo.controller;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.dto.StudyTrailResponse;
import com.nexo.service.StudyTrailService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/trail/{id}")
    public ResponseEntity<StudyTrailResponse> getStudyTrail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long studyTrailId
    ){
        Long user_id = Long.valueOf(jwt.getSubject());

        return ResponseEntity
                .ok(studyTrailService.getStudyTrail())
    }
}
