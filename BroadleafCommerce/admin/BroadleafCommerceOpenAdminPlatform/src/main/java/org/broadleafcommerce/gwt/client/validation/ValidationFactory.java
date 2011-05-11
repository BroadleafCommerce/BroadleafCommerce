package org.broadleafcommerce.gwt.client.validation;

import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.smartgwt.client.widgets.form.validator.Validator;

public interface ValidationFactory {

	public boolean isValidFactory(String validatorClassname, Map<String, String> configurationItems);
	
	public Validator createValidator(String validatorClassname, Map<String, String> configurationItems, List<ConstantsWithLookup> constants, String fieldName);
	
}
