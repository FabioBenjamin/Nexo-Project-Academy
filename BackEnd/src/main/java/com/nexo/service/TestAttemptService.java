package com.nexo.service;

import com.nexo.dto.AnswerRequest;
import com.nexo.dto.QuestionExplanationResponse;
import com.nexo.dto.TestAttemptResultRequest;
import com.nexo.dto.TestAttemptResultResponse;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.exceptions.StudyTestUnavailableException;
import com.nexo.models.classes.Alternative;
import com.nexo.models.classes.Question;
import com.nexo.models.classes.StudyTest;
import com.nexo.models.classes.TestAttempt;
import com.nexo.models.enums.StudyTestStatus;
import com.nexo.repository.AlternativeRepository;
import com.nexo.repository.QuestionRepository;
import com.nexo.repository.StudyTestRepository;
import com.nexo.repository.TestAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestAttemptService {

    private final TestAttemptRepository testAttemptRepository;
    private final StudyTestRepository studyTestRepository;
    private final QuestionRepository questionRepository;
    private final AlternativeRepository alternativeRepository;
    private final StudyTestService studyTestService;

    public TestAttemptService(
            TestAttemptRepository testAttemptRepository,
            StudyTestRepository studyTestRepository,
            QuestionRepository questionRepository,
            AlternativeRepository alternativeRepository,
            StudyTestService studyTestService
    ){
        this.testAttemptRepository = testAttemptRepository;
        this.studyTestRepository = studyTestRepository;
        this.questionRepository = questionRepository;
        this.alternativeRepository = alternativeRepository;
        this.studyTestService = studyTestService;
    }

    @Transactional
    public TestAttemptResultResponse checkTestAttemptResult(
            Long studyTestId,
            Long userId,
            TestAttemptResultRequest resultRequest){

        // check if user has access to study test with id X
        // otherwise raise a new exception
        // There is no need to tell user that the resource exists and, he doesn't have access
        // That's why return an exception with status code 404(not found) instead of 403(Forbidden)

        StudyTest studyTest = studyTestRepository.findByIdAndStudyTrailUserId(studyTestId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Study test not found"));

        // Completed study Test must not receive new test attempts
        if(studyTest.getStatus() == StudyTestStatus.COMPLETED){
            throw new StudyTestUnavailableException("Study test has already been completed");
        }

        // extract question IDs
        List<Long> questionIds = resultRequest.answers()
                .stream()
                .map(AnswerRequest::questionId)
                .distinct()
                .toList();

        if(questionRepository.countQuestionsBelongingToStudyTest(studyTestId, questionIds) !=
            questionIds.size()){
            throw new IllegalArgumentException("Invalid questions");
        }

        int score = 0;
        List<QuestionExplanationResponse> questionExplanationResponses = new ArrayList<>();

        for(AnswerRequest answer: resultRequest.answers()){

            Alternative alternative = alternativeRepository.findByIdAndQuestionId(
                    answer.alternativeId(), answer.questionId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Alternative"));

            if(alternative.isCorrect()){
                score += 1;
                questionExplanationResponses.add(new QuestionExplanationResponse(
                        answer.questionId(),
                        alternative.getExplanation(),
                        true
                    )
                );
            } else {
                questionExplanationResponses.add(new QuestionExplanationResponse(
                        answer.questionId(),
                        alternative.getExplanation(),
                        false
                    )
                );
            }
        }

        TestAttempt testAttempt = new TestAttempt();
        testAttempt.setStudyTest(studyTest);
        testAttempt.setScore(score);
        testAttempt.setCreatedAt(LocalDate.now());

        testAttemptRepository.save(testAttempt);

        boolean approved = studyTestService.completeStudyTest(studyTest, score);

        return new TestAttemptResultResponse(
                questionExplanationResponses,
                score,
                approved
        );

    }
}
