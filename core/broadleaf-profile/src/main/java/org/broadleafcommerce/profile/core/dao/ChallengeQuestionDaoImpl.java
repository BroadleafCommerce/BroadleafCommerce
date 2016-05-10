/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.profile.core.domain.ChallengeQuestion;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blChallengeQuestionDao")
public class ChallengeQuestionDaoImpl implements ChallengeQuestionDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<ChallengeQuestion> readChallengeQuestions() {
        Query query = em.createNamedQuery("BC_READ_CHALLENGE_QUESTIONS");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public ChallengeQuestion readChallengeQuestionById(long challengeQuestionId) {
        Query query = em.createNamedQuery("BC_READ_CHALLENGE_QUESTION_BY_ID");
        query.setParameter("question_id", challengeQuestionId);
        List<ChallengeQuestion> challengeQuestions = query.getResultList();
        return challengeQuestions == null || challengeQuestions.isEmpty() ? null : challengeQuestions.get(0);
    }
    
    @Transactional("blTransactionManager")
    public ChallengeQuestion saveChallengeQuestion(ChallengeQuestion q) {
        return em.merge(q);
    }

}
