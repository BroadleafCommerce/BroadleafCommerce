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
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.LRUMap;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * This processor will add the following information to the model, available for consumption by a template:
 * -pricing for a sku based on the product option values selected
 * -the complete set of product options and values for a given product
 *  
 * @author jfridye
 *
 */
public class ProductOptionsProcessor extends AbstractModelVariableModifierProcessor {
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    private static final Log LOG = LogFactory.getLog(ProductOptionsProcessor.class);
    protected static final Map<Object, String> JSON_CACHE = Collections.synchronizedMap(new LRUMap<Object, String>(100, 500));

    public ProductOptionsProcessor() {
        super("product_options");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue("productId"));
        Long productId = (Long) expression.execute(arguments.getConfiguration(), arguments);
        Product product = catalogService.findProductById(productId);
        if (product != null) {
            addAllProductOptionsToModel(arguments, product);
        }
    }
    
    private void addAllProductOptionsToModel(Arguments arguments, Product product) {
        List<ProductOptionXref> productOptionXrefs = product.getProductOptionXrefs();
        List<ProductOptionDTO> dtos = new ArrayList<ProductOptionDTO>();
        for (ProductOptionXref optionXref : productOptionXrefs) {
            ProductOption option = optionXref.getProductOption();
            ProductOptionDTO dto = new ProductOptionDTO();
            dto.setId(option.getId());
            dto.setType(option.getType().getType());
            dto.setRequired(option.getRequired());
            Map<Long, String> values = new HashMap<Long, String>();
            for (ProductOptionValue value : option.getAllowedValues()) {
                values.put(value.getId(), value.getAttributeValue());
            }
            dto.setValues(values);
            dtos.add(dto);
        }
        writeJSONToModel(arguments, "allProductOptions", dtos);
    }
    
    private void writeJSONToModel(Arguments arguments, String modelKey, Object o) {
        try {
            if (!JSON_CACHE.containsKey(o)) {
                ObjectMapper mapper = new ObjectMapper();
                Writer strWriter = new StringWriter();
                mapper.writeValue(strWriter, o);
                JSON_CACHE.put(o, strWriter.toString());
            }
            addToModel(arguments, modelKey, JSON_CACHE.get(o));
        } catch (Exception ex) {
            LOG.error("There was a problem writing the product option map to JSON", ex);
        }
    }
    
    private class ProductOptionDTO {
        private Long id;
        private String type;
        private Map<Long, String> values;
        private String selectedValue;
        private Boolean required;
        @SuppressWarnings("unused")
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        @SuppressWarnings("unused")
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        @SuppressWarnings("unused")
        public Map<Long, String> getValues() {
            return values;
        }
        public void setValues(Map<Long, String> values) {
            this.values = values;
        }
        @SuppressWarnings("unused")
        public String getSelectedValue() {
            return selectedValue;
        }
        @SuppressWarnings("unused")
        public void setSelectedValue(String selectedValue) {
            this.selectedValue = selectedValue;
        }
        @SuppressWarnings("unused")
        public Boolean getRequired() {
            return required;
        }
        @SuppressWarnings("unused")
        public void setRequired(Boolean required) {
            this.required = required;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (!getClass().isAssignableFrom(o.getClass())) return false;

            ProductOptionDTO that = (ProductOptionDTO) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (selectedValue != null ? !selectedValue.equals(that.selectedValue) : that.selectedValue != null)
                return false;
            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            if (values != null ? !values.equals(that.values) : that.values != null) return false;
            if (required != null ? required != that.required : that.required != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (values != null ? values.hashCode() : 0);
            result = 31 * result + (selectedValue != null ? selectedValue.hashCode() : 0);
            return result;
        }
    }

}
