/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.codehaus.jackson.map.ObjectMapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

public class ProductOptionValueProcessor extends AbstractAttrProcessor  {

    private static final Log LOG = LogFactory.getLog(ProductOptionValueProcessor.class);
    
    public ProductOptionValueProcessor() {
        super("product_option_value");
    }
    
    @Override
    protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

        ProductOptionValue productOptionValue = (ProductOptionValue) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
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
            element.setAttribute("data-product-option-value", strWriter.toString());
            element.removeAttribute(attributeName);
            return ProcessorResult.OK;
        } catch (Exception ex) {
            LOG.error("There was a problem writing the product option value to JSON", ex);
        }
        
        return null;
        
    }

    @Override
    public int getPrecedence() {
        return 10000;
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
