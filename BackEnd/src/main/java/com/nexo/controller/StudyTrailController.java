package com.nexo.controller;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.dto.StudyTrailResponse;
import com.nexo.dto.StudyTrailSummaryResponse;
import com.nexo.dto.UpdateStudyTrailRequest;
import com.nexo.service.StudyTrailService;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
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

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studyTrailService.createStudyTrail(dto, userId));
    }

    @GetMapping("/trail/{id}")
    public ResponseEntity<StudyTrailResponse> getStudyTrail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long studyTrailId
    ){
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity
                .ok(studyTrailService.getStudyTrail(studyTrailId, userId));
    }

    @GetMapping("/trails")
    public ResponseEntity<Page<StudyTrailSummaryResponse>> getUserTrails(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page){

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity
                .ok(studyTrailService.getUserTrails(userId, page, 10));
    }

    @DeleteMapping("/trails/{id}")
    public ResponseEntity<Void> deleteStudyTrail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long studyTrailId
    ){
        Long userId = Long.valueOf(jwt.getSubject());

        studyTrailService.deleteStudyTrail(studyTrailId, userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/trails/{id}")
    public ResponseEntity<Void> updateStudyTrail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long studyTrailId,
            @RequestBody UpdateStudyTrailRequest request
            ){

        Long userId = Long.valueOf(jwt.getSubject());

        studyTrailService.updateStudyTrail(studyTrailId, userId, request);

        return ResponseEntity.noContent().build();
    }
}
