package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.statement;

import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.grouping.Phrase;

import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.types.OperatorId;

public class StatementTranslator {

	public AdvancedCriteria createCriteria(Phrase phrase) {
		String temp = phrase.getPhrase().substring(phrase.getPhrase().indexOf(".") + 1, phrase.getPhrase().length());
		return null;
	}
	
	protected Expression createExpression(String truncatedPhrase) throws IncompatibleMVELTranslationException {
		String[] components = truncatedPhrase.split(" ");
		if (components.length != 3) {
			throw new IncompatibleMVELTranslationException("The expression does not appear to have the three basic components [field, operator, value] to qualify for an expression (" + truncatedPhrase + ")");
		}
		Expression expression = new Expression();
		expression.setField(components[0]);
		
		OperatorId operator = getOperator(components[1]);
		return null;
	}
	
	protected OperatorId getOperator(String operator) {
		return null;
	}
	
}
