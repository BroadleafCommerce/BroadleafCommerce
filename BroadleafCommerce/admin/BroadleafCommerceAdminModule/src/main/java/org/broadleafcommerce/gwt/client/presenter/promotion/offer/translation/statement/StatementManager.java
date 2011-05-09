package org.broadleafcommerce.gwt.client.presenter.promotion.offer.translation.statement;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.presenter.promotion.offer.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.gwt.client.presenter.promotion.offer.translation.grouping.Phrase;

public class StatementManager extends HashMap<String, Integer> {

	private static final long serialVersionUID = 1L;
	public static Map<String, FilterType> mvelKeywordMap = new HashMap<String, FilterType>();
	static {
		mvelKeywordMap.put("order", FilterType.ORDER);
		mvelKeywordMap.put("discreteOrderItem", FilterType.ORDER_ITEM);
		mvelKeywordMap.put("fulfillmentGroup", FilterType.FULFILLMENT_GROUP);
		mvelKeywordMap.put("customer", FilterType.CUSTOMER);
	}
	
	protected String currentKey;

	public StatementManager() {
		for (String keyWord : mvelKeywordMap.keySet()) {
			put(keyWord, 0);
		}
	}
	
	public FilterType checkoutKeyword(Phrase phrase) throws IncompatibleMVELTranslationException {
		String temp = phrase.getPhrase().substring(0, phrase.getPhrase().indexOf("."));
		if (!keySet().contains(temp)) {
			throw new IncompatibleMVELTranslationException("Unable to determine a valid Broadleaf keyword in the MVEL statement (" + phrase.getPhrase() + ")");
		}
		if (temp.equals(currentKey)) {
			return mvelKeywordMap.get(currentKey);
		}
		if (get(temp) > 0) {
			throw new IncompatibleMVELTranslationException("To be compatible with the rules builder, MVEL must be organized into related statements. All order expressions must appear together, all order item expressions must appear together, etc... These groups of related expressions must also be separated by a logical and operator. Intermixing of expression types is not allowed for translation to the rules builder.");
		}
		currentKey = temp;
		put(temp, 1);
		return mvelKeywordMap.get(currentKey);
	}
}
