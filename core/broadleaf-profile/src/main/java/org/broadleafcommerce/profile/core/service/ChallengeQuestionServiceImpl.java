/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.ChallengeQuestionDao;
import org.broadleafcommerce.profile.core.domain.ChallengeQuestion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Service("blChallengeQuestionService")
public class ChallengeQuestionServiceImpl implements ChallengeQuestionService {

    @Resource(name="blChallengeQuestionDao")
    protected ChallengeQuestionDao challengeQuestionDao;

    @Override
    @Transactional("blTransactionManager")
    public List<ChallengeQuestion> readChallengeQuestions() {
        return challengeQuestionDao.readChallengeQuestions();
    }

    @Override
    @Transactional("blTransactionManager")
    public ChallengeQuestion readChallengeQuestionById(long challengeQuestionId) {
        return challengeQuestionDao.readChallengeQuestionById(challengeQuestionId);
    }
    
}
