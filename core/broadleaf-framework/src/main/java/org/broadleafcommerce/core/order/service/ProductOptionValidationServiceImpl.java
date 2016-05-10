/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service("blProductOptionValidationService")
public class ProductOptionValidationServiceImpl implements ProductOptionValidationService  {

    private static final Log LOG = LogFactory.getLog(ProductOptionValidationServiceImpl.class);


    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.order.service.ProductOptionValidationService#validate(org.broadleafcommerce.core.catalog.domain.ProductOption, java.lang.String)
     */
    @Override
    public Boolean validate(ProductOption productOption, String value) {
        if (requiresValidation(productOption) && !validateRegex(productOption.getValidationString(), value)) {
            LOG.error(productOption.getErrorMessage() + ". Value [" + value + "] does not match regex string ["
                    + productOption.getValidationString() + "]");
            String exceptionMessage = productOption.getAttributeName() + " " + productOption.getErrorMessage()
                    + ". Value [" + value + "] does not match regex string ["
                    + productOption.getValidationString() + "]";
            throw new ProductOptionValidationException(exceptionMessage, productOption.getErrorCode(),
                    productOption.getAttributeName(), value, productOption.getValidationString(),
                    productOption.getErrorMessage());
        }
        return true;
    }

    protected Boolean requiresValidation(ProductOption productOption) {
        ProductOptionValidationType validationType = productOption.getProductOptionValidationType();

        Boolean typeRequiresValidation = validationType == null || validationType == ProductOptionValidationType.REGEX;
        Boolean validationStringExists = productOption.getValidationString() != null;

        return typeRequiresValidation && validationStringExists;
    }

    protected Boolean validateRegex(String regex, String value) {
        if (value == null) {
            return false;
        }
        return Pattern.matches(regex, value);
    }
    

}
