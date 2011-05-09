package org.broadleafcommerce.gwt.client.presenter.promotion.offer.translation.grouping;

import java.util.HashMap;
import java.util.Map;

public class Phrase {
	
	protected static Map<String, OperatorType> types = new HashMap<String, OperatorType>();
	static {
		types.put("&&", OperatorType.AND);
		types.put("&", OperatorType.AND);
		types.put("||", OperatorType.OR);
		types.put("|", OperatorType.OR);
	}
	
	protected String phrase = "";
	protected OperatorType operatorType;
	
	public Phrase(Phrase startPhrase) {
		if (startPhrase != null) {
			operatorType = startPhrase.getOperatorType();
		}
	}
	
	public String getPhrase() {
		return phrase;
	}
	
	public OperatorType getOperatorType() {
		return operatorType;
	}
	
	public void setContent(String phrase) {
		String test = new String(phrase);
		test.trim();
		OperatorType temp = determineOperatorType(test);
		if (temp == null) {
			this.phrase = test;
		} else {
			operatorType = temp;
		}
	}
	
	public Boolean isComplete() {
		return !phrase.equals("") && operatorType != null;
	}
	
	protected OperatorType determineOperatorType(String phrase) {
		for (String type : types.keySet()) {
			if (type.equals(phrase)) {
				return types.get(type);
			}
		}
		return null;
	}
}
