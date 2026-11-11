/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.core.catalog.dao.ProductOptionDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

@Service("blProductOptionValidationService")
public class ProductOptionValidationServiceImpl implements ProductOptionValidationService {

    protected static final Integer ADD_TYPE_RANK = ProductOptionValidationStrategyType.ADD_ITEM.getRank();
    protected static final Integer SUBMIT_TYPE_RANK = ProductOptionValidationStrategyType.SUBMIT_ORDER.getRank();

    /**
     * Option types whose value is entered freely rather than chosen from an enumerated list, so there is no allowed
     * value list to check the submitted value against.
     */
    protected static final Set<String> NON_ENUMERATED_OPTION_TYPES = new HashSet<>(Arrays.asList(
            ProductOptionType.TEXT.getType(), ProductOptionType.TEXTAREA.getType(),
            ProductOptionType.INPUT.getType(), ProductOptionType.DATE.getType(),
            ProductOptionType.INTEGER.getType()));

    private static final Log LOG = LogFactory.getLog(ProductOptionValidationServiceImpl.class);
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
            validationString = xssExploitProtectionEnabled
                    ? ESAPI.encoder().decodeForHTML(validationString)
                    : validationString;
            value = siteXssWrapperEnabled ? ESAPI.encoder().decodeForHTML(value) : value;

            if (requiresValidation(productOption, value) && !validateRegex(validationString, value)) {
                String errorMessage = productOption.getErrorMessage();
                if (StringUtils.isEmpty(errorMessage)) {
                    errorMessage = "Value [" + StringUtil.sanitize(value) + "] does not match regex string [" + validationString + "]";
                }

                LOG.error(errorMessage);
                throw new ProductOptionValidationException(
                        errorMessage,
                        productOption.getErrorCode(),
                        attributeName,
                        value,
                        validationString,
                        errorMessage
                );
            }
        }

        return true;
    }

    @Override
    public void validateProductOptionAttributes(Product product, Map<String, String> attributeValues) {
        if (product == null) {
            return;
        }
        Map<String, ProductOption> optionsByName = new HashMap<>();
        for (ProductOptionXref xref : ListUtils.emptyIfNull(product.getProductOptionXrefs())) {
            ProductOption option = xref.getProductOption();
            optionsByName.put(option.getAttributeName(), option);
        }
        for (Map.Entry<String, String> entry : MapUtils.emptyIfNull(attributeValues).entrySet()) {
            ProductOption option = optionsByName.get(entry.getKey());
            if (option == null) {
                //not a product option on this product; order item attributes are a generic key-value store and are
                //also used for custom, non-option attributes (e.g. a gift message), so it is left alone
                continue;
            }
            validateOptionValueIsAllowed(option, entry.getValue());
        }
    }

    /**
     * Rejects a value that is not one of the option's allowed values. Only applies to enumerated options, i.e. those
     * with a non-empty {@link ProductOption#getAllowedValues()}. Non-enumerated option types
     * ({@link #NON_ENUMERATED_OPTION_TYPES}, e.g. a text or date input) are excluded, since their value is entered
     * freely rather than chosen from a list. A missing value is allowed here, since the required check is handled by
     * {@link #validate(ProductOption, String)}.
     */
    protected void validateOptionValueIsAllowed(ProductOption productOption, String attributeValue) {
        if (StringUtils.isEmpty(attributeValue) || isNonEnumeratedOptionType(productOption)) {
            return;
        }
        List<ProductOptionValue> allowedValues = productOption.getAllowedValues();
        if (CollectionUtils.isEmpty(allowedValues)) {
            return;
        }
        for (ProductOptionValue allowedValue : allowedValues) {
            if (attributeValue.equals(allowedValue.getAttributeValue())) {
                return;
            }
        }
        throw new IllegalArgumentException("Value [" + StringUtil.sanitize(attributeValue)
                + "] is not an allowed value for product option [" + productOption.getAttributeName() + "]");
    }

    protected boolean isNonEnumeratedOptionType(ProductOption productOption) {
        return productOption.getType() != null
                && NON_ENUMERATED_OPTION_TYPES.contains(productOption.getType().getType());
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
    public List<Long> findSkuIdsForProductOptionValues(
            Long productId,
            String attributeName,
            String attributeValue,
            List<Long> possibleSkuIds
    ) {
        return productOptionDao.readSkuIdsForProductOptionValues(
                productId, attributeName, attributeValue, possibleSkuIds
        );
    }

}
