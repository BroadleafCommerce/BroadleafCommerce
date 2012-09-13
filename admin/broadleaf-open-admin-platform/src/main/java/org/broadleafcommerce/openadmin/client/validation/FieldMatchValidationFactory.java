/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.validation;

import java.util.Map;
import java.util.MissingResourceException;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.form.validator.Validator;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Factory;
import org.broadleafcommerce.openadmin.client.reflection.ReflectiveFactory;

/**
 * 
 * @author jfischer
 *
 */
public class FieldMatchValidationFactory implements ValidationFactory {

	private static final long serialVersionUID = 1L;
	
	private Factory factory = (Factory) GWT.create(ReflectiveFactory.class);
	
	public boolean isValidFactory(String validatorClassname, Map<String, String> configurationItems) {
		String fieldType = configurationItems.get("fieldType");
		return validatorClassname.equals(MatchesFieldValidator.class.getName()) && (fieldType == null || !fieldType.equals("password"));
	}

	public Validator createValidator(String validatorClassname, Map<String, String> configurationItems, String fieldName) {
		Object response = factory.newInstance(validatorClassname);
		if (response == null) {
			throw new RuntimeException("Unable to instantiate the item from the Factory using classname: (" + validatorClassname + "). Are you sure this classname is correct?");
		}
		MatchesFieldValidator valid = (MatchesFieldValidator) response;
		if (configurationItems.containsKey("errorMessageKey")) {
			String message = null;
            try {
                message = BLCMain.getMessageManager().getString(configurationItems.get("errorMessageKey"));
            } catch (MissingResourceException e) {
                //do nothing
            }
			if (message != null) {
				valid.setErrorMessage(message);
			}
		} else if (configurationItems.containsKey("errorMessage")) {
			valid.setErrorMessage(configurationItems.get("errorMessage"));
		}
        if (configurationItems.get("otherField") == null) {
            throw new RuntimeException("Usage of MatchesFieldValidator requires a configurationItem be set for 'otherField'");
        }
		valid.setOtherField(configurationItems.get("otherField"));

		return valid;
	}

}
