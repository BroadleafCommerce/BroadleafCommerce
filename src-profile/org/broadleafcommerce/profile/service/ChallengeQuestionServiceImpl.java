package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.ChallengeQuestionDao;
import org.broadleafcommerce.profile.domain.ChallengeQuestion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("challengeQuestionService")
public class ChallengeQuestionServiceImpl implements ChallengeQuestionService {

    @Resource
    private ChallengeQuestionDao challengeQuestionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ChallengeQuestion> readChallengeQuestions() {
        return challengeQuestionDao.readChallengeQuestions();
    }

    @Override
    public ChallengeQuestion readChallengeQuestionById(long challengeQuestionId) {
        return challengeQuestionDao.readChallengeQuestionById(challengeQuestionId);
    }
    
}
