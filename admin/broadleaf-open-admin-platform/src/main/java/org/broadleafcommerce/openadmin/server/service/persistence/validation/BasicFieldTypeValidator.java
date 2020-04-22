/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
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
        DecimalFormat format = populateValueRequest.getDataFormatProvider().getDecimalFormatter();
        ParsePosition pp;
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
                    } else {
                        Integer.parseInt(populateValueRequest.getRequestedValue());
                    }
                } catch (NumberFormatException e) {
                    return new PropertyValidationResult(false, "Field must be an valid number");
                }
                break;
            case DECIMAL:
                pp = new ParsePosition(0);
                if (BigDecimal.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                    format.setParseBigDecimal(true);
                    format.parse(populateValueRequest.getRequestedValue(), pp);
                    format.setParseBigDecimal(false);
                } else {
                    format.parse(populateValueRequest.getRequestedValue(), pp);
                }
                if (pp.getIndex() != populateValueRequest.getRequestedValue().length()) {
                    return new PropertyValidationResult(false, "Field must be a valid decimal");
                }
                break;
            case MONEY:
                pp = new ParsePosition(0);
                try {
                    if (Double.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        format.parse(populateValueRequest.getRequestedValue(), pp);
                    } else {
                        format.setParseBigDecimal(true);
                        format.parse(populateValueRequest.getRequestedValue(), pp);
                        format.setParseBigDecimal(false);
                    }
                    if (pp.getIndex() != populateValueRequest.getRequestedValue().length()) {
                        return new PropertyValidationResult(false, "Field must be a valid number");
                    }
                } catch (NumberFormatException e) {
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

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }
}
