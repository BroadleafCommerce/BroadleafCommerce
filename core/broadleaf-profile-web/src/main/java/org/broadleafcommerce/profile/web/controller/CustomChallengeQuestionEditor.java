/*-
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.controller;

import org.broadleafcommerce.profile.core.domain.ChallengeQuestion;
import org.broadleafcommerce.profile.core.service.ChallengeQuestionService;

import java.beans.PropertyEditorSupport;

public class CustomChallengeQuestionEditor extends PropertyEditorSupport {
    
    private ChallengeQuestionService challengeQuestionService;
    
    public CustomChallengeQuestionEditor(ChallengeQuestionService challengeQuestionService) {
        this.challengeQuestionService = challengeQuestionService;
    }

    @Override
    public String getAsText() {
        ChallengeQuestion question = (ChallengeQuestion) getValue();
        if (question == null) {
            return null;
        } else {
            return question.getId().toString();
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(challengeQuestionService.readChallengeQuestionById((Long.parseLong(text))));
    }

}
