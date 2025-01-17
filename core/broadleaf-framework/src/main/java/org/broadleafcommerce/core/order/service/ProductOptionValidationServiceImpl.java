/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.core.catalog.dao.ProductOptionDao;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.type.MessageType;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

@Service("blProductOptionValidationService")
public class ProductOptionValidationServiceImpl implements ProductOptionValidationService  {

    private static final Log LOG = LogFactory.getLog(ProductOptionValidationServiceImpl.class);
    protected static final Integer ADD_TYPE_RANK = ProductOptionValidationStrategyType.ADD_ITEM.getRank();
    protected static final Integer SUBMIT_TYPE_RANK = ProductOptionValidationStrategyType.SUBMIT_ORDER.getRank();

    @Resource
    protected ProductOptionDao productOptionDao;

    @Autowired
    protected Environment environment;

    @Value("${exploitProtection.xssEnabled:false}")
    protected boolean xssExploitProtectionEnabled;

    @Value("${blc.site.enable.xssWrapper:false}")
    protected boolean siteXssWrapperEnabled;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.order.service.ProductOptionValidationService#validate(org.broadleafcommerce.core.catalog.domain.ProductOption, java.lang.String)
     */
    @Override
    public Boolean validate(ProductOption productOption, String value) {
        String attributeName = productOption.getAttributeName();

        if (isRequiredAttributeNotProvided(productOption, value)) {
            String message = "Required attribute, " + StringUtil.sanitize(attributeName) + ", not provided";

            LOG.error(message);
            throw new RequiredAttributeNotProvidedException(message, attributeName);
        } else {
            String validationString = productOption.getValidationString();
            validationString = xssExploitProtectionEnabled ? ESAPI.encoder().decodeForHTML(validationString) : validationString;
            value = siteXssWrapperEnabled ? ESAPI.encoder().decodeForHTML(value) : value;

            if (requiresValidation(productOption, value) && !validateRegex(validationString, value)) {
                String errorMessage = productOption.getErrorMessage();
                if (StringUtils.isEmpty(errorMessage)) {
                    errorMessage = "Value [" + StringUtil.sanitize(value) + "] does not match regex string [" + validationString + "]";
                }

                LOG.error(errorMessage);
                throw new ProductOptionValidationException(errorMessage, productOption.getErrorCode(),
                                                           attributeName, value, validationString,
                                                           errorMessage);
            }
        }

        return true;
    }

    protected boolean isRequiredAttributeNotProvided(ProductOption productOption, String attributeValue) {
        return productOption.getRequired() && StringUtils.isEmpty(attributeValue);
    }

    protected boolean requiresValidation(ProductOption productOption, String value) {
        ProductOptionValidationType validationType = productOption.getProductOptionValidationType();
        boolean typeRequiresValidation = validationType == ProductOptionValidationType.REGEX;
        boolean validationStringExists = StringUtils.isNotEmpty(productOption.getValidationString());
        boolean isRequired = productOption.getRequired();
        boolean hasValue = StringUtils.isNotEmpty(value);

        return (isRequired || hasValue) && typeRequiresValidation && validationStringExists;
    }

    protected Boolean validateRegex(String regex, String value) {
        return value != null && Pattern.matches(regex, value);
    }

    @Override
    public boolean hasProductOptionValidationStrategy(ProductOption productOption) {
        return productOption.getProductOptionValidationStrategyType() != null;
    }

    @Override
    public boolean isSubmitType(ProductOption productOption) {
        boolean hasStrategy = hasProductOptionValidationStrategy(productOption);

        return hasStrategy && productOption.getProductOptionValidationStrategyType().getRank().equals(SUBMIT_TYPE_RANK);
    }

    @Override
    public boolean isAddOrNoneType(ProductOption productOption) {
        boolean hasStrategy = hasProductOptionValidationStrategy(productOption);

        return hasStrategy && productOption.getProductOptionValidationStrategyType().getRank() <= ADD_TYPE_RANK;
    }

    @Override
    public void validateWithoutException(ProductOption productOption, String attributeValue, ActivityMessages messages) {
        try {
            validate(productOption, attributeValue);
        } catch (ProductOptionValidationException | RequiredAttributeNotProvidedException e) {
            ActivityMessageDTO msg = new ActivityMessageDTO(MessageType.PRODUCT_OPTION.getType(), 1, e.getMessage());

            if (e instanceof ProductOptionValidationException) {
                msg.setErrorCode(productOption.getErrorCode());
            } else {
                msg.setErrorCode(RequiredAttributeNotProvidedException.ERROR_CODE);
            }

            messages.getActivityMessages().add(msg);
        }
    }

    @Override
    public List<Long> findSkuIdsForProductOptionValues(Long productId, String attributeName, String attributeValue, List<Long> possibleSkuIds) {
        return productOptionDao.readSkuIdsForProductOptionValues(productId, attributeName, attributeValue, possibleSkuIds);
    }
}
