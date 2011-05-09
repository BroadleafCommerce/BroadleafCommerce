package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation;

import java.util.Map;

import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.grouping.Group;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.grouping.GroupingTranslator;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.statement.FilterType;

import com.smartgwt.client.data.AdvancedCriteria;


public class MVEL_AdvancedCriteriaTranslator {
	
	public static void main(String[] items) {
		MVEL_AdvancedCriteriaTranslator translator = new MVEL_AdvancedCriteriaTranslator();
		try {
			//Map<FilterType, AdvancedCriteria> test = translator.createAdvancedCriteria("one.test && (two.thing || (three.thing && four.thing)) && five.thing");
			Map<FilterType, AdvancedCriteria> test = translator.createAdvancedCriteria("one.test");
		} catch (IncompatibleMVELTranslationException e) {
			e.printStackTrace();
		}
	}
	
	protected GroupingTranslator groupingTranslator = new GroupingTranslator();

	public Map<FilterType, AdvancedCriteria> createAdvancedCriteria(String mvel) throws IncompatibleMVELTranslationException {
		Group group = groupingTranslator.createGroups(mvel);
		return null;
		
	}
	
	
}
