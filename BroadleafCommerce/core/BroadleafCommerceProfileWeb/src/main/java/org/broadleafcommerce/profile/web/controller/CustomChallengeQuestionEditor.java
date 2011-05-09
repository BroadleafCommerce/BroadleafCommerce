package org.broadleafcommerce.profile.web.controller;

import java.beans.PropertyEditorSupport;

import org.broadleafcommerce.profile.domain.ChallengeQuestion;
import org.broadleafcommerce.profile.service.ChallengeQuestionService;

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
