/*-
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.processor;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.util.BLCMoneyFormatUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuProductOptionValueXref;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafVariableModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

/**
 * This processor will add the following information to the model, available for consumption by a template:
 * -pricing for a sku based on the product option values selected
 * -the complete set of product options and values for a given product
 *
 * @author jfridye
 *
 */
@Component("blProductOptionsProcessor")
@ConditionalOnTemplating
public class ProductOptionsProcessor extends AbstractBroadleafVariableModifierProcessor {

    private static final Log LOG = LogFactory.getLog(ProductOptionsProcessor.class);
    protected static final Map<Object, String> JSON_CACHE = Collections.synchronizedMap(new LRUMap<Object, String>(500));

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blProductOptionsProcessorExtensionManager")
    protected ProductOptionsProcessorExtensionManager extensionManager;

    @Override
    public String getName() {
        return "product_options";
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    public Map<String, Object> populateModelVariables(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        Long productId = (Long) context.parseExpression(tagAttributes.get("productId"));
        Product product = catalogService.findProductById(productId);
        Map<String, Object> newModelVars = new HashMap<>();
        if (product != null) {
            addAllProductOptionsToModel(newModelVars, product);
            addProductOptionPricingToModel(newModelVars, product, context, tagAttributes);
        }
        return newModelVars;
    }

    protected void addProductOptionPricingToModel(Map<String, Object> newModelVars, Product product, BroadleafTemplateContext context, Map<String, String> tagAttributes) {
        List<Sku> skus = product.getSkus();
        List<ProductOptionPricingDTO> skuPricing = new ArrayList<>();
        for (Sku sku : skus) {
            
            List<Long> productOptionValueIds = new ArrayList<Long>();
            
            Set<SkuProductOptionValueXref> productOptionValueXrefs = SetUtils.emptyIfNull(sku.getProductOptionValueXrefs());
            for (SkuProductOptionValueXref skuProductOptionValueXref : productOptionValueXrefs) {
                ProductOptionValue productOptionValue = skuProductOptionValueXref.getProductOptionValue();
                productOptionValueIds.add(productOptionValue.getId());
            }
            ProductOptionPricingDTO pricingDto = createPricingDto(sku, productOptionValueIds, tagAttributes, context);
            skuPricing.add(pricingDto);
        }
        writeJSONToModel(newModelVars, "skuPricing", skuPricing);
    }

    protected ProductOptionPricingDTO createPricingDto(Sku sku, List<Long> productOptionValueIds, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        Long[] values = new Long[productOptionValueIds.size()];
        productOptionValueIds.toArray(values);

        ProductOptionPricingDTO dto = new ProductOptionPricingDTO();
        Money currentPrice = sku.getPrice();

        // Check for Price Overrides
        ExtensionResultHolder<Money> priceHolder = new ExtensionResultHolder<>();
        priceHolder.setResult(currentPrice);
        if (extensionManager != null) {
            extensionManager.getProxy().modifyPriceForOverrides(sku, priceHolder, context, tagAttributes);
        }

        dto.setPrice(BLCMoneyFormatUtils.formatPrice(priceHolder.getResult()));
        if (sku.getRetailPrice() != null) {
            dto.setRetailPrice(BLCMoneyFormatUtils.formatPrice(sku.getRetailPrice()));
        }
        if (sku.getSalePrice() != null) {
            dto.setSalePrice(BLCMoneyFormatUtils.formatPrice(sku.getSalePrice()));
        }
        dto.setOnSale(sku.isOnSale());
        dto.setSelectedOptions(values);
        return dto;
    }
    
    protected void addAllProductOptionsToModel(Map<String, Object> newModelVars, Product product) {
        List<ProductOptionXref> productOptionXrefs = ListUtils.emptyIfNull(product.getProductOptionXrefs());
        List<ProductOptionDTO> dtos = new ArrayList<>();

        for (ProductOptionXref optionXref : productOptionXrefs) {
            ProductOptionDTO dto = new ProductOptionDTO();
            ProductOption productOption = optionXref.getProductOption();

            dto.setId(productOption.getId());
            dto.setType(productOption.getType().getType());

            Map<Long, String> values = new HashMap<>();
            Map<Long, Double> priceAdjustments = new HashMap<>();

            for (ProductOptionValue value : productOption.getAllowedValues()) {
                values.put(value.getId(), value.getAttributeValue());

                Money priceAdjustment = value.getPriceAdjustment();
                Double priceAdjustmentValue = (priceAdjustment != null) ? priceAdjustment.doubleValue() : null;
                priceAdjustments.put(value.getId(), priceAdjustmentValue);
            }

            dto.setValues(values);
            dto.setPriceAdjustments(priceAdjustments);
            dtos.add(dto);
        }
        writeJSONToModel(newModelVars, "allProductOptions", dtos);
    }

    protected void writeJSONToModel(Map<String, Object> newModelVars, String modelKey, Object o) {
        try {
            String jsonValue = JSON_CACHE.get(o);
            if (jsonValue == null) {
                ObjectMapper mapper = new ObjectMapper();
                Writer strWriter = new StringWriter();
                mapper.writeValue(strWriter, o);
                jsonValue = strWriter.toString();
                JSON_CACHE.put(o, jsonValue);
            }
            newModelVars.put(modelKey, jsonValue);
        } catch (Exception ex) {
            LOG.error("There was a problem writing the product option map to JSON", ex);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected class ProductOptionDTO {

        private Long id;
        private String type;
        private Map<Long, String> values;
        private Map<Long, Double> priceAdjustments;
        private String selectedValue;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<Long, String> getValues() {
            return values;
        }

        public void setValues(Map<Long, String> values) {
            this.values = values;
        }

        public String getSelectedValue() {
            return selectedValue;
        }

        public void setSelectedValue(String selectedValue) {
            this.selectedValue = selectedValue;
        }

        public Map<Long, Double> getPriceAdjustments() {
            return priceAdjustments;
        }

        public void setPriceAdjustments(Map<Long, Double> priceAdjustments) {
            this.priceAdjustments = priceAdjustments;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!getClass().isAssignableFrom(o.getClass())) {
                return false;
            }

            ProductOptionDTO that = (ProductOptionDTO) o;

            if ((id != null) ? !id.equals(that.id) : (that.id != null)) {
                return false;
            }
            if ((selectedValue != null) ? !selectedValue.equals(that.selectedValue) : (that.selectedValue != null)) {
                return false;
            }
            if ((type != null) ? !type.equals(that.type) : (that.type != null)) {
                return false;
            }
            if ((values != null) ? !values.equals(that.values) : (that.values != null)) {
                return false;
            }
            if ((priceAdjustments != null) ? !priceAdjustments.equals(that.priceAdjustments) : (that.priceAdjustments != null)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = (id != null) ? id.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (values != null ? values.hashCode() : 0);
            result = 31 * result + (selectedValue != null ? selectedValue.hashCode() : 0);
            result = 31 * result + (priceAdjustments != null ? priceAdjustments.hashCode() : 0);
            return result;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected class ProductOptionPricingDTO {

        private Long[] skuOptions;
        private String price;
        private String retailPrice;
        private String salePrice;
        private boolean onSale;

        public Long[] getSelectedOptions() {
            return skuOptions;
        }

        public void setSelectedOptions(Long[] skuOptions) {
            this.skuOptions = skuOptions;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getRetailPrice() {
            return retailPrice;
        }

        public void setRetailPrice(String retailPrice) {
            this.retailPrice = retailPrice;
        }

        public String getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(String salePrice) {
            this.salePrice = salePrice;
        }

        public boolean isOnSale() {
            return onSale;
        }

        public void setOnSale(boolean onSale) {
            this.onSale = onSale;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && getClass().isAssignableFrom(o.getClass())) {
                ProductOptionPricingDTO that = (ProductOptionPricingDTO) o;

                return new EqualsBuilder()
                    .append(this.price, that.price)
                    .append(this.skuOptions, that.skuOptions)
                    .append(this.retailPrice, that.retailPrice)
                    .append(this.salePrice, that.salePrice)
                    .append(this.onSale, that.onSale)
                    .build();
            }

            return false;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(skuOptions)
                .append(price)
                .append(retailPrice)
                .append(salePrice)
                .append(onSale)
                .build();
        }
    }
}
