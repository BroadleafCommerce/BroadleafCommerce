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

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
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
    protected static final Map<Object, String> JSON_CACHE = Collections.synchronizedMap(new LRUMap<Object, String>(500));

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
            addProductOptionPricingToModel(arguments, product);
        }
    }
    
    private void addProductOptionPricingToModel(Arguments arguments, Product product) {
        List<Sku> skus = product.getSkus();
        List<ProductOptionPricingDTO> skuPricing = new ArrayList<ProductOptionPricingDTO>();
        for (Sku sku : skus) {
            
            List<Long> productOptionValueIds = new ArrayList<Long>();
            
            List<ProductOptionValue> productOptionValues = sku.getProductOptionValues();
            for (ProductOptionValue productOptionValue : productOptionValues) {
                productOptionValueIds.add(productOptionValue.getId());
            }
            
            Long[] values = new Long[productOptionValueIds.size()];
            productOptionValueIds.toArray(values);
            
            ProductOptionPricingDTO dto = new ProductOptionPricingDTO();
            Money currentPrice;
            if (sku.isOnSale()) {
                currentPrice = sku.getSalePrice();
            } else {
                currentPrice = sku.getRetailPrice();
            }
            dto.setPrice(formatPrice(currentPrice));
            dto.setSelectedOptions(values);
            skuPricing.add(dto);
        }
        writeJSONToModel(arguments, "skuPricing", skuPricing);
    }
    
    private void addAllProductOptionsToModel(Arguments arguments, Product product) {
        List<ProductOption> productOptions = product.getProductOptions();
        List<ProductOptionDTO> dtos = new ArrayList<ProductOptionDTO>();
        for (ProductOption option : productOptions) {
            ProductOptionDTO dto = new ProductOptionDTO();
            dto.setId(option.getId());
            dto.setType(option.getType().getType());
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

    private String formatPrice(Money price){
        if (price == null){
            return null;
        }
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc.getJavaLocale() != null) {
            return BroadleafCurrencyUtils.getNumberFormatFromCache(brc.getJavaLocale(), price.getCurrency()).format
                                (price.getAmount());
        } else {
            // Setup your BLC_CURRENCY and BLC_LOCALE to display a diff default.
            return "$ " + price.getAmount().toString();
        }
    }
    
    private class ProductOptionDTO {
        private Long id;
        private String type;
        private Map<Long, String> values;
        private String selectedValue;
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
    
    private class ProductOptionPricingDTO {
        private Long[] skuOptions;
        private String price;
        @SuppressWarnings("unused")
        public Long[] getSelectedOptions() {
            return skuOptions;
        }
        public void setSelectedOptions(Long[] skuOptions) {
            this.skuOptions = skuOptions;
        }
        @SuppressWarnings("unused")
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (!getClass().isAssignableFrom(o.getClass())) return false;

            ProductOptionPricingDTO that = (ProductOptionPricingDTO) o;

            if (price != null ? !price.equals(that.price) : that.price != null) return false;
            if (!Arrays.equals(skuOptions, that.skuOptions)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = skuOptions != null ? Arrays.hashCode(skuOptions) : 0;
            result = 31 * result + (price != null ? price.hashCode() : 0);
            return result;
        }
    }

}
