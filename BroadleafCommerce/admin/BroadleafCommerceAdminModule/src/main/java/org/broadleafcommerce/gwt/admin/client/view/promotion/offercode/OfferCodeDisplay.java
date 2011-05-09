package org.broadleafcommerce.gwt.admin.client.view.promotion.offercode;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

public interface OfferCodeDisplay extends DynamicEditDisplay {

	public DynamicFormDisplay getDynamicFormDisplay();

	public DynamicEntityListDisplay getListDisplay();
	
}