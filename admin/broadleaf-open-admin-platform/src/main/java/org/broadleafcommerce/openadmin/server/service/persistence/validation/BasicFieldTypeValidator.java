/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;


/**
 * Validates that values are actually of their required types before trying to populate it. Integers should be integers,
 * dates should parse correctly, etc.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blBasicFieldTypeValidator")
public class BasicFieldTypeValidator implements PopulateValueRequestValidator {

    @Override
    public PropertyValidationResult validate(PopulateValueRequest populateValueRequest, Serializable instance) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        Locale locale = brc.getJavaLocale();
        switch(populateValueRequest.getMetadata().getFieldType()) {
            case INTEGER:
                try {
                    if (int.class.isAssignableFrom(populateValueRequest.getReturnType()) || Integer.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Integer.parseInt(populateValueRequest.getRequestedValue());
                    } else if (byte.class.isAssignableFrom(populateValueRequest.getReturnType()) || Byte.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Byte.parseByte(populateValueRequest.getRequestedValue());
                    } else if (short.class.isAssignableFrom(populateValueRequest.getReturnType()) || Short.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Short.parseShort(populateValueRequest.getRequestedValue());
                    } else if (long.class.isAssignableFrom(populateValueRequest.getReturnType()) || Long.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Long.parseLong(populateValueRequest.getRequestedValue());
                    }
                } catch (NumberFormatException e) {
                    return new PropertyValidationResult(false, "Field must be an valid number");
                }
                break;
            case DECIMAL:
                try {
                    if (BigDecimal.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        DecimalFormat format = populateValueRequest.getDataFormatProvider().getDecimalFormatter();
                        format.setParseBigDecimal(true);
                        format.parse(populateValueRequest.getRequestedValue());
                        format.setParseBigDecimal(false);
                    } else {
                        populateValueRequest.getDataFormatProvider().getDecimalFormatter().parse(populateValueRequest.getRequestedValue());
                    }
                } catch (ParseException e) {
                    return new PropertyValidationResult(false, "Field must be a valid decimal");
                }
                break;
            case MONEY:
                try {
                    if (BigDecimal.class.isAssignableFrom(populateValueRequest.getReturnType()) || Money.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        DecimalFormat format = populateValueRequest.getDataFormatProvider().getDecimalFormatter();
                        format.setParseBigDecimal(true);
                        format.parse(populateValueRequest.getRequestedValue());
                        format.setParseBigDecimal(false);
                    } else if (Double.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getDataFormatProvider().getDecimalFormatter().parse(populateValueRequest.getRequestedValue());
                    }
                } catch (ParseException e) {
                    return new PropertyValidationResult(false, "Field must be a valid number");
                }
                break;
            case DATE:
                try {
                    populateValueRequest.getDataFormatProvider().getSimpleDateFormatter().parse(populateValueRequest.getRequestedValue());
                } catch (ParseException e) {
                    return new PropertyValidationResult(false, "Field must be a date of the format: " + populateValueRequest.getDataFormatProvider().getSimpleDateFormatter().toPattern());
                }
                break;
            case FOREIGN_KEY:
            case ADDITIONAL_FOREIGN_KEY:
                if (Collection.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                    Collection collection;
                    try {
                        collection = (Collection) populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName());
                    } catch (FieldNotAvailableException e) {
                        return new PropertyValidationResult(false, "External entity cannot be added to the specified collection at " + populateValueRequest.getProperty().getName());
                    } catch (IllegalAccessException e) {
                        return new PropertyValidationResult(false, "External entity cannot be added to the specified collection at " + populateValueRequest.getProperty().getName());
                    }
                } else if (Map.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                    return new PropertyValidationResult(false, "External entity cannot be added to a map at " + populateValueRequest.getProperty().getName());
                }
            case ID:
                if (populateValueRequest.getSetId()) {
                    switch (populateValueRequest.getMetadata().getSecondaryType()) {
                        case INTEGER:
                            Long.valueOf(populateValueRequest.getRequestedValue());
                            break;
                        default:
                            //do nothing
                    }
                }
            default:
                return new PropertyValidationResult(true);
        }
        return new PropertyValidationResult(true);
    }
    
}
