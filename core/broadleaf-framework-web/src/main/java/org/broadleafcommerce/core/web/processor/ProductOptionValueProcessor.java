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

package org.broadleafcommerce.core.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

@Component("blProductOptionValueProcessor")
public class ProductOptionValueProcessor extends AbstractAttributeTagProcessor {

    private static final Log LOG = LogFactory.getLog(ProductOptionValueProcessor.class);
    
    public ProductOptionValueProcessor() {
        super(TemplateMode.HTML, "blc", null, false, "product_option_value", true, 10000, true);
    }


    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        ProductOptionValue productOptionValue = (ProductOptionValue)StandardExpressions.getExpressionParser(context.getConfiguration())
                .parseExpression(context, attributeValue)
                .execute(context);
        ProductOptionValueDTO dto = new ProductOptionValueDTO();
        dto.setOptionId(productOptionValue.getProductOption().getId());
        dto.setValueId(productOptionValue.getId());
        dto.setValueName(productOptionValue.getAttributeValue());
        if (productOptionValue.getPriceAdjustment() != null) {
            dto.setPriceAdjustment(productOptionValue.getPriceAdjustment().getAmount());
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Writer strWriter = new StringWriter();
            mapper.writeValue(strWriter, dto);
            structureHandler.setAttribute("data-product-option-value", strWriter.toString(), AttributeValueQuotes.DOUBLE);
        } catch (Exception ex) {
            LOG.error("There was a problem writing the product option value to JSON", ex);
        }
    }

    private class ProductOptionValueDTO {
        private Long optionId;
        private Long valueId;
        private String valueName;
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
        public BigDecimal getPriceAdjustment() {
            return priceAdjustment;
        }
        public void setPriceAdjustment(BigDecimal priceAdjustment) {
            this.priceAdjustment = priceAdjustment;
        }
    }

}
