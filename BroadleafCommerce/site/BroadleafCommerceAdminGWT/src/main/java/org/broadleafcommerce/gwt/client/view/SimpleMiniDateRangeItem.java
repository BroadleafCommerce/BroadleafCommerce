package org.broadleafcommerce.gwt.client.view;

import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.widgets.form.fields.MiniDateRangeItem;

public class SimpleMiniDateRangeItem extends MiniDateRangeItem {

	@Override
	public Criterion getCriterion() {
		Criterion criterion = new Criterion();
		//criterion.addCriteria(getName(), getFromDate())
		//TODO simplify the criteria coming from this date filter editor
		return null;
	}

}
