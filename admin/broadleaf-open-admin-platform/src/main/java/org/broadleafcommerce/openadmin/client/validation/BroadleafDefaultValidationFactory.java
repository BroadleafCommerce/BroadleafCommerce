/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.validation;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Factory;
import org.broadleafcommerce.openadmin.client.reflection.ReflectiveFactory;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.form.validator.Validator;

import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
@Deprecated
public class BroadleafDefaultValidationFactory implements ValidationFactory {

    private static final long serialVersionUID = 1L;
    
    private final Factory factory = (Factory) GWT.create(ReflectiveFactory.class);
    
    @Override
    public boolean isValidFactory(String validatorClassname, Map<String, String> configurationItems) {
        return true;
    }

    @Override
    public Validator createValidator(String validatorClassname, Map<String, String> configurationItems, String fieldName) {
        Object response = factory.newInstance(validatorClassname);
        if (response == null) {
            throw new RuntimeException("Unable to instantiate the item from the Factory using classname: (" + validatorClassname + "). Are you sure this classname is correct?");
        }
        Validator valid = (Validator) response;
        if (configurationItems.containsKey("regularExpression")) {
            ((RegExpValidator) valid).setExpression(configurationItems.get("regularExpression"));
        }
        if (configurationItems.containsKey("errorMessageKey")) {
            String message = BLCMain.getMessageManager().getString(configurationItems.get("errorMessageKey"));
            if (message != null) {
                valid.setErrorMessage(message);
            }
        } else if (configurationItems.containsKey("errorMessage")) {
            valid.setErrorMessage(configurationItems.get("errorMessage"));
        }

        if (valid instanceof IntegerRangeValidator) {
            IntegerRangeValidator integerRangeValidator = (IntegerRangeValidator) valid;
            if (configurationItems.containsKey("min")) {
                integerRangeValidator.setMin(Integer.parseInt(configurationItems.get("min")));
            }
            if (configurationItems.containsKey("max")) {
                integerRangeValidator.setMin(Integer.parseInt(configurationItems.get("max")));
            }
        }

        return valid;
    }

}
