/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafAttributeModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafAttributeModifier;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component("blProductOptionValueProcessor")
@ConditionalOnTemplating
public class ProductOptionValueProcessor extends AbstractBroadleafAttributeModifierProcessor {

    private static final Log LOG = LogFactory.getLog(ProductOptionValueProcessor.class);

    @Override
    public String getName() {
        return "product_option_value";
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    public BroadleafAttributeModifier getModifiedAttributes(String tagName, Map<String, String> tagAttributes, String attributeName, String attributeValue, BroadleafTemplateContext context) {
        Map<String, String> newAttributes = new HashMap<>();
        ProductOptionValue productOptionValue = (ProductOptionValue) context.parseExpression(attributeValue);
        ProductOptionValueDTO dto = new ProductOptionValueDTO();
        dto.setOptionId(productOptionValue.getProductOption().getId());
        dto.setValueId(productOptionValue.getId());
        dto.setValueName(productOptionValue.getAttributeValue());
        dto.setRawValue(productOptionValue.getRawAttributeValue());
        if (productOptionValue.getPriceAdjustment() != null) {
            dto.setPriceAdjustment(productOptionValue.getPriceAdjustment().getAmount());
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Writer strWriter = new StringWriter();
            mapper.writeValue(strWriter, dto);
            newAttributes.put("data-product-option-value", strWriter.toString());
        } catch (Exception ex) {
            LOG.error("There was a problem writing the product option value to JSON", ex);
        }
        return new BroadleafAttributeModifier(newAttributes);
    }

    @Override
    public boolean useSingleQuotes() {
        return true;
    }

    private class ProductOptionValueDTO {

        private Long optionId;
        private Long valueId;
        private String valueName;
        private String rawValue;
        private BigDecimal priceAdjustment;

        @SuppressWarnings("unused")
        public Long getOptionId() {
            return optionId;
        }

        public void setOptionId(Long optionId) {
            this.optionId = optionId;
        }

        @SuppressWarnings("unused")
        public Long getValueId() {
            return valueId;
        }

        public void setValueId(Long valueId) {
            this.valueId = valueId;
        }

        @SuppressWarnings("unused")
        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        @SuppressWarnings("unused")
        public String getRawValue() {
            return rawValue;
        }
        public void setRawValue(String rawValue) {
            this.rawValue = rawValue;
        }
        @SuppressWarnings("unused")
        public BigDecimal getPriceAdjustment() {
            return priceAdjustment;
        }

        public void setPriceAdjustment(BigDecimal priceAdjustment) {
            this.priceAdjustment = priceAdjustment;
        }
    }
}
