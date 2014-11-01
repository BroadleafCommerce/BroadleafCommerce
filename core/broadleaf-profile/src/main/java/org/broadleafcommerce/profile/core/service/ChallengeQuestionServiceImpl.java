/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.ChallengeQuestionDao;
import org.broadleafcommerce.profile.core.domain.ChallengeQuestion;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("blChallengeQuestionService")
public class ChallengeQuestionServiceImpl implements ChallengeQuestionService {

    @Resource(name="blChallengeQuestionDao")
    protected ChallengeQuestionDao challengeQuestionDao;

    public List<ChallengeQuestion> readChallengeQuestions() {
        return challengeQuestionDao.readChallengeQuestions();
    }

    public ChallengeQuestion readChallengeQuestionById(long challengeQuestionId) {
        return challengeQuestionDao.readChallengeQuestionById(challengeQuestionId);
    }

}
