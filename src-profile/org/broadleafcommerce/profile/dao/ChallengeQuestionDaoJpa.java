package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.ChallengeQuestion;
import org.springframework.stereotype.Repository;

@Repository("challengeQuestionDao")
public class ChallengeQuestionDaoJpa implements ChallengeQuestionDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<ChallengeQuestion> readChallengeQuestions() {
        Query query = em.createNamedQuery("BC_READ_CHALLENGE_QUESTIONS");
        return query.getResultList();
    }
}
