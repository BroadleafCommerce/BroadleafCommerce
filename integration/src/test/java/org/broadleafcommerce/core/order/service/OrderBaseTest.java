/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.test.CommonSetupBaseTest;

@SuppressWarnings("deprecation")
public class OrderBaseTest extends CommonSetupBaseTest {

    private int bundleCount = 0;
    
    protected Customer createNamedCustomer() {
    	Customer customer = customerService.createCustomerFromId(null);
    	customer.setUsername(String.valueOf(customer.getId()));
    	return customer;
    }
    
	public Order setUpNamedOrder() throws PricingException, AddToCartException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = orderService.createNamedOrderForCustomer("Boxes Named Order", customer);
        
        Product newProduct = addTestProduct("Cube Box", "Boxes");        
        Category newCategory = newProduct.getDefaultCategory();
        
        order = orderService.addItem(order.getId(), 
        		new OrderItemRequestDTO(newProduct.getId(), newProduct.getDefaultSku().getId(), newCategory.getId(), 2), 
        		true);

        return order;
    }
	
}
