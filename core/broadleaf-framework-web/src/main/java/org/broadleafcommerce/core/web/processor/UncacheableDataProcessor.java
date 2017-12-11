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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.inventory.service.InventoryServiceExtensionHandler;
import org.broadleafcommerce.core.inventory.service.InventoryServiceExtensionManager;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.SkuAccessor;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.processor.extension.UncacheableDataProcessorExtensionManager;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

/**
 * This processor outputs a SCRIPT tag with JSON data that can be used to update a mostly cached page followed by
 * a call to a javascript function.  The function name is "updateUncacheableData()" by default.
 * 
 * Broadleaf provides this example but most clients with typical customizations will need to create a similar processor
 * to meet their dynamic data caching needs.
 * 
 * The Broadleaf processor works with the sample javacript function in HeatClinic found in heatClinic-UncacheableData.js work 
 * together to update the "In Cart", "Out of Stock", "Welcome {name}", and "Cart Qty" messages.   By doing this, the 
 * category and product pages in HeatClinic can be aggressively cached using the {@link BroadleafCacheProcessor}. 
 * 
 * Example usage on cached pages with dynamic data.   This would generally go after the footer for the page.
 * <pre>
 *  {@code
 *      <blc:uncacheableData />  
 *  }
 * </pre>
 * @author bpolster
 */
@Component("blUncacheableDataProcessor")
@ConditionalOnTemplating
public class UncacheableDataProcessor extends AbstractBroadleafTagReplacementProcessor {
    
    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Resource(name = "blInventoryService")
    protected InventoryService inventoryService;

    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService eps;

    @Resource(name = "blUncacheableDataProcessorExtensionManager")
    protected UncacheableDataProcessorExtensionManager extensionManager;

    @Resource(name = "blInventoryServiceExtensionManager"   )
    protected InventoryServiceExtensionManager inventoryServiceExtensionManager;

    private String defaultCallbackFunction = "updateUncacheableData(params);\n";

    @Override
    public String getName() {
        return "uncacheabledata"; 
    }

    @Override
    public int getPrecedence() {
        return 100;
    }
    
    @Override
    public BroadleafTemplateModel getReplacementModel(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        StringBuffer sb = new StringBuffer();
        sb.append("<SCRIPT>\n");
        sb.append("  var params = \n  ");
        sb.append(buildContentMap(context)).append(";\n  ");
        sb.append(getUncacheableDataFunction(context, tagAttributes));
        sb.append("</SCRIPT>");
                
        // Add contentNode to the document
        BroadleafTemplateModel model = context.createModel();
        BroadleafTemplateElement script = context.createTextElement(sb.toString());
        model.addElement(script);
        return model;
    }
    
    protected String buildContentMap(BroadleafTemplateContext context) {
        Map<String, Object> attrMap = new HashMap<>();
        addCartData(attrMap);
        addCustomerData(attrMap);
        addProductInventoryData(attrMap, context);

        try {
            attrMap.put("csrfToken", eps.getCSRFToken());
            attrMap.put("csrfTokenParameter", eps.getCsrfTokenParameter());
        } catch (ServiceException e) {
            throw new RuntimeException("Could not get a CSRF token for this session", e);
        }
        return StringUtil.getMapAsJson(attrMap);
    }

    protected void addProductInventoryData(Map<String, Object> attrMap, BroadleafTemplateContext context) {
        List<Long> outOfStockProducts = new ArrayList<>();
        List<Long> outOfStockSkus = new ArrayList<>();

        Set<Product> allProducts = new HashSet<>();
        Set<Sku> allSkus = new HashSet<>();
        Set<Product> products = (Set<Product>) context.getVariable("blcAllDisplayedProducts");
        Set<Sku> skus = (Set<Sku>) context.getVariable("blcAllDisplayedSkus");
        if (!CollectionUtils.isEmpty(products)) {
            allProducts.addAll(products);
        }
        if (!CollectionUtils.isEmpty(skus)) {
            allSkus.addAll(skus);
        }

        extensionManager.getProxy().modifyProductListForInventoryCheck(context, allProducts, allSkus);

        if (!allProducts.isEmpty()) {
            for (Product product : allProducts) {
                if (product.getDefaultSku() != null) {

                    Boolean qtyAvailable = inventoryService.isAvailable(product.getDefaultSku(), 1);
                    if (qtyAvailable != null && !qtyAvailable) {
                        outOfStockProducts.add(product.getId());
                    } else {
                        InventoryServiceExtensionHandler handler = inventoryServiceExtensionManager.getProxy();
                        ExtensionResultHolder<Boolean> holder = new ExtensionResultHolder<>();
                        handler.isProductBundleAvailable(product, 1, holder);
                        Boolean available = holder.getResult();
                        if (available != null && !available) {
                            outOfStockProducts.add(product.getId());
                        }
                    }
                }
            }
        } else {
            if (!allSkus.isEmpty()) {
                Map<Sku, Integer> inventoryAvailable = inventoryService.retrieveQuantitiesAvailable(allSkus);
                for (Map.Entry<Sku, Integer> entry : inventoryAvailable.entrySet()) {
                    if (entry.getValue() == null || entry.getValue() < 1) {
                        outOfStockSkus.add(entry.getKey().getId());
                    }
                }
            }
        }
        attrMap.put("outOfStockProducts", outOfStockProducts);
        attrMap.put("outOfStockSkus", outOfStockSkus);
    }

    protected void addCartData(Map<String, Object> attrMap) {
        Order cart = CartState.getCart();
        int cartQty = 0;
        List<Long> cartItemIdsWithOptions = new ArrayList<>();
        List<Long> cartItemIdsWithoutOptions = new ArrayList<>();

        if (cart != null && cart.getOrderItems() != null) {
            cartQty = cart.getItemCount();

            for (OrderItem item : cart.getOrderItems()) {
                if (item instanceof SkuAccessor) {
                    Sku sku = ((SkuAccessor) item).getSku();
                    if (sku != null && sku.getProduct() != null && item.getParentOrderItem() == null) {
                        if (useSku) {
                            cartItemIdsWithoutOptions.add(sku.getId());
                        } else {
                            Product product = sku.getProduct();
                            List<ProductOptionXref> optionXrefs = product.getProductOptionXrefs();
                            if (optionXrefs == null || optionXrefs.isEmpty()) {
                                cartItemIdsWithoutOptions.add(product.getId());
                            } else {
                                cartItemIdsWithOptions.add(product.getId());
                            } 
                        }
                        
                    }
                }
            }
        }

        attrMap.put("cartItemCount", cartQty);
        attrMap.put("cartItemIdsWithOptions", cartItemIdsWithOptions);
        attrMap.put("cartItemIdsWithoutOptions", cartItemIdsWithoutOptions);
    }
    
    protected void addCustomerData(Map<String, Object> attrMap) {
        Customer customer = CustomerState.getCustomer();
        String firstName = "";
        String lastName = "";
        boolean anonymous = false;

        if (customer != null) {
            if (!StringUtils.isEmpty(customer.getFirstName())) {
                firstName = customer.getFirstName();
            }

            if (!StringUtils.isEmpty(customer.getLastName())) {
                lastName = customer.getLastName();
            }

            if (customer.isAnonymous()) {
                anonymous = true;
            }
        }
        
        attrMap.put("firstName", firstName);
        attrMap.put("lastName", lastName);
        attrMap.put("anonymous", anonymous);
    }
    
    public String getUncacheableDataFunction(BroadleafTemplateContext context, Map<String, String> tagAttributes) {
        if (tagAttributes.containsKey("callbackBlock")) {
            return tagAttributes.get("callbackBlock");
        } else if (tagAttributes.containsKey("callback")) {
            return tagAttributes.get("callback") + ";\n";
        } else {
            return getDefaultCallbackFunction();
        }
    }

    public String getDefaultCallbackFunction() {
        return defaultCallbackFunction;
    }
    
    public void setDefaultCallbackFunction(String defaultCallbackFunction) {
        this.defaultCallbackFunction = defaultCallbackFunction;
    }

}
