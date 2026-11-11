package com.nexo.service;

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
}
