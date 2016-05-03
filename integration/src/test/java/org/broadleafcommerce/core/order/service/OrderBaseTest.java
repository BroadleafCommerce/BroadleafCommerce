/*
 * #%L
 * BroadleafCommerce Integration
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

import org.apache.commons.lang.time.DateUtils;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.test.CommonSetupBaseTest;

import java.util.Date;

public class OrderBaseTest extends CommonSetupBaseTest {

    protected Customer createNamedCustomer() {
        Customer customer = customerService.createCustomerFromId(null);
        customer.setUsername(String.valueOf(customer.getId()));
        return customer;
    }
    
    public Order setUpNamedOrder() throws AddToCartException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = orderService.createNamedOrderForCustomer("Boxes Named Order", customer);
        
        Product newProduct = addTestProduct("Cube Box", "Boxes");        
        Category newCategory = newProduct.getDefaultCategory();
        
        order = orderService.addItem(order.getId(), 
                new OrderItemRequestDTO(newProduct.getId(), newProduct.getDefaultSku().getId(), newCategory.getId(), 2), 
                true);

        return order;
    }
    
    public Order setUpCartWithActiveSku() throws AddToCartException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = orderService.createNewCartForCustomer(customer);

        Product newProduct = addTestProduct("Plastic Crate Active", "Crates");
        Category newCategory = newProduct.getDefaultCategory();
        
        order = orderService.addItem(order.getId(), 
                new OrderItemRequestDTO(newProduct.getId(), newProduct.getDefaultSku().getId(), newCategory.getId(), 1), 
                true);

        return order;
    }
    
    public Order setUpCartWithInactiveSku() throws AddToCartException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = orderService.createNewCartForCustomer(customer);

        Product newProduct = addTestProduct("Plastic Crate Should Be Inactive", "Crates");
        Category newCategory = newProduct.getDefaultCategory();
        
        order = orderService.addItem(order.getId(), 
                new OrderItemRequestDTO(newProduct.getId(), newProduct.getDefaultSku().getId(), newCategory.getId(), 1), 
                true);
        
        // Make the SKU inactive
        newProduct.getDefaultSku().setActiveEndDate(DateUtils.addDays(new Date(), -1));
        catalogService.saveSku(newProduct.getDefaultSku());

        return order;
    }
    
}
