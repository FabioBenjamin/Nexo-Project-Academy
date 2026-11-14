package com.nexo.service;

import com.nexo.dto.AlternativeResponse;
import com.nexo.dto.QuestionResponse;
import com.nexo.dto.StudyTestResponse;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.exceptions.StudyTestUnavailableException;
import com.nexo.dto.StudyTestResponse;
import com.nexo.integration.dto.GeminiAlternativeResponse;
import com.nexo.integration.dto.GeminiQuestionResponse;
import com.nexo.integration.dto.GeminiTestResponse;
import com.nexo.models.classes.Alternative;
import com.nexo.models.classes.Question;
import com.nexo.models.classes.StudyTest;
import com.nexo.models.classes.StudyTrail;
import com.nexo.models.enums.StudyTestStatus;
import com.nexo.repository.StudyTestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyTestService {

    private final StudyTestRepository studyTestRepository;

    public StudyTestService(
            StudyTestRepository studyTestRepository
    ){
        this.studyTestRepository = studyTestRepository;
    }

    public void saveStudyTest(GeminiTestResponse testResponse, StudyTrail studyTrail){

        StudyTest studyTest = new StudyTest();
        studyTest.setStatus(StudyTestStatus.BLOCKED);
        studyTest.setStudyTrail(studyTrail);
        studyTest.setPassingScore(7);

        studyTrail.setStudyTest(studyTest);

        for(GeminiQuestionResponse questionResponse : testResponse.questions()){

            Question question = new Question();

            question.setStatement(questionResponse.statement());
            question.setStudyTest(studyTest);

            for(GeminiAlternativeResponse alternativeResponse : questionResponse.alternatives()){

                Alternative alternative = new Alternative();

                alternative.setDescription(alternativeResponse.description());
                alternative.setExplanation(alternativeResponse.explanation());
                alternative.setCorrect(alternativeResponse.isCorrect());
                alternative.setQuestion(question);

                question.getAlternatives().add(alternative);
            }

            studyTest.getQuestions().add(question);

        }

        studyTestRepository.save(studyTest);
    }

    public StudyTestResponse getStudyTest(Long studyTestId, Long userId){

        StudyTest studyTest = studyTestRepository.findByIdAndStudyTrailUserId(studyTestId, userId)
                .orElseThrow(() -> new EntityNotFoundException("StudyTest not found"));

        // Verify if StudyTest is available
        if(studyTest.getStatus() == StudyTestStatus.BLOCKED){
            throw new StudyTestUnavailableException("Study Test is not available");
        }

        if(studyTest.getStatus() == StudyTestStatus.COMPLETED){
            throw new StudyTestUnavailableException("Study Test has already been completed");
        }

        return mapToStudyTestResponseDTO(studyTest);

    }

    private StudyTestResponse mapToStudyTestResponseDTO(StudyTest studyTest){

        List<Question> questions = studyTest.getQuestions();

        List<QuestionResponse> questionResponses = questions
                .stream()
                .map(this :: mapToQuestionResponseDTO)
                .toList();

        return new StudyTestResponse(
                questionResponses
        );
    }

    private QuestionResponse mapToQuestionResponseDTO(Question question){

        List<AlternativeResponse> alternativeResponses = question.getAlternatives()
                .stream()
                .map(this:: mapToAlternativeResponseDTO)
                .toList();

        return new QuestionResponse(
                question.getId(),
                question.getStatement(),
                alternativeResponses
        );
    }

    private AlternativeResponse mapToAlternativeResponseDTO(Alternative alternative){

        return new AlternativeResponse(
                alternative.getId(),
                alternative.getDescription()
        );
    }
}
