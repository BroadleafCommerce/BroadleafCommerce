package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.ChallengeQuestion;

public interface ChallengeQuestionDao {

    public List<ChallengeQuestion> readChallengeQuestions();
    public ChallengeQuestion readChallengeQuestionById(long challengeQuestionId);
  
}
