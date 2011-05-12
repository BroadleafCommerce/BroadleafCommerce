package org.broadleafcommerce.gwt.client.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.broadleafcommerce.gwt.client.BLCMain;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.form.validator.Validator;

public class ValidationFactoryManager extends ArrayList<ValidationFactory> {
	
	private static final long serialVersionUID = 1L;
	
	public static final Map<String, Map<String, String>> EMAIL_CONFIG = new HashMap<String, Map<String, String>>();
	static {
		Map<String, String> items = new HashMap<String, String>();
		items.put("regularExpression", "^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
		items.put("errorMessage", "Invalid email address");
		EMAIL_CONFIG.put(RegExpValidator.class.getName(), items);
	}
	
	public static final Map<String, Map<String, String>> US_CURRENCY_CONFIG = new HashMap<String, Map<String, String>>();
	static {
		Map<String, String> items = new HashMap<String, String>();
		items.put("regularExpression", "^\\d+(\\.\\d{1,2})?$");
		items.put("errorMessage", "Invalid currency amount");
		US_CURRENCY_CONFIG.put(RegExpValidator.class.getName(), items);
	}
	
	private static ValidationFactoryManager manager = null;
	
	public static ValidationFactoryManager getInstance() {
		if (manager == null) {
			ValidationFactoryManager.manager = new ValidationFactoryManager();
			ValidationFactoryManager.manager.getConstants().add(BLCMain.OPENADMINMESSAGES);
			ValidationFactoryManager.manager.add(new PasswordMatchValidationFactory());
		}
		return ValidationFactoryManager.manager;
	}
	
	protected ValidationFactory defaultFactory = new BroadleafDefaultValidationFactory();
	protected List<ConstantsWithLookup> constants = new ArrayList<ConstantsWithLookup>();

	public Validator[] createValidators(Map<String, Map<String, String>> validatorConfiguration, String fieldName) {
		List<Validator> validators = new ArrayList<Validator>();
		for (String validationClassname : validatorConfiguration.keySet()) {
			boolean factoryFound = false;
			for (ValidationFactory factory : this) {
				if (factory.isValidFactory(validationClassname, validatorConfiguration.get(validationClassname))) {
					Validator validator = factory.createValidator(validationClassname, validatorConfiguration.get(validationClassname), constants, fieldName);
					validators.add(validator);
					factoryFound = true;
				}
			}
			if (!factoryFound) {
				//unable to find a validator factory registered - use the default factory
				Validator validator = defaultFactory.createValidator(validationClassname, validatorConfiguration.get(validationClassname), constants, fieldName);
				validators.add(validator);
			}
		}
		
		return validators.toArray(new Validator[]{});
	}

	public ValidationFactory getDefaultFactory() {
		return defaultFactory;
	}

	public void setDefaultFactory(ValidationFactory defaultFactory) {
		this.defaultFactory = defaultFactory;
	}
	
	public String getConstantValue(String key) {
		String response = null;
		for (ConstantsWithLookup constant : constants) {
			try {
				response = constant.getString(key);
				break;
			} catch (MissingResourceException e) {
				//do nothing
			}
		}
		return response;
	}

	public List<ConstantsWithLookup> getConstants() {
		return constants;
	}

	public void setConstants(List<ConstantsWithLookup> constants) {
		this.constants = constants;
	}
	
}
