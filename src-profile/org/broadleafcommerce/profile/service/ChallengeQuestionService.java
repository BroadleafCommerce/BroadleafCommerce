package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.ChallengeQuestion;

public interface ChallengeQuestionService {

    public List<ChallengeQuestion> readChallengeQuestions();
    public ChallengeQuestion readChallengeQuestionById(long challengeQuestionId);
}