package org.broadleafcommerce.gwt.client.validation;

import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.broadleafcommerce.gwt.client.reflection.Factory;
import org.broadleafcommerce.gwt.client.reflection.ReflectiveFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.form.validator.Validator;

public class BroadleafDefaultValidationFactory implements ValidationFactory {

	private Factory factory = (Factory) GWT.create(ReflectiveFactory.class);
	
	public boolean isValidFactory(String validatorClassname, Map<String, String> configurationItems) {
		return true;
	}

	public Validator createValidator(String validatorClassname, Map<String, String> configurationItems, List<ConstantsWithLookup> constants, String fieldName) {
		Object response = factory.newInstance(validatorClassname);
		if (response == null) {
			throw new RuntimeException("Unable to instantiate the item from the Factory using classname: (" + validatorClassname + "). Are you sure this classname is correct?");
		}
		Validator valid = (Validator) response;
		if (configurationItems.containsKey("regularExpression")) {
			((RegExpValidator) valid).setExpression(configurationItems.get("regularExpression"));
		}
		if (configurationItems.containsKey("errorMessageKey")) {
			String message = null;
			for (ConstantsWithLookup constant : constants) {
				try {
					message = constant.getString(configurationItems.get("errorMessageKey"));
					if (message != null) {
						break;
					}
				} catch (MissingResourceException e) {
					//do nothing
				}
			}
			if (message != null) {
				((RegExpValidator) valid).setErrorMessage(message);
			}
		} else if (configurationItems.containsKey("errorMessage")) {
			((RegExpValidator) valid).setErrorMessage(configurationItems.get("errorMessage"));
		}
		return valid;
	}

}
