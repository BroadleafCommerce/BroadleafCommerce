/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.order.service.legacy;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.test.legacy.LegacyCommonSetupBaseTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class LegacyOrderBaseTest extends LegacyCommonSetupBaseTest {

    @Resource(name = "blOrderService")
    protected LegacyCartService cartService;
    
    private int bundleCount = 0;
    
    protected Customer createNamedCustomer() {
        Customer customer = customerService.createCustomerFromId(null);
        customer.setUsername(String.valueOf(customer.getId()));
        return customer;
    }
    
    public Order setUpNamedOrder() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNamedOrderForCustomer("Boxes Named Order", customer);
        
        Product newProduct = addTestProduct("Cube Box", "Boxes");        
        Category newCategory = newProduct.getDefaultCategory();

        cartService.addSkuToOrder(order.getId(), newProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2);
        
        return order;
    }
    
    public Order setUpAnonymousCartWithInactiveSku() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        Product newProduct = addTestProduct("Plastic Crate", "Crates");
        Product newInactiveProduct = addTestProduct("Plastic Crate", "Crates", false);
        
        Category newCategory = newProduct.getDefaultCategory();

        cartService.addSkuToOrder(order.getId(), newProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2);
        cartService.addSkuToOrder(order.getId(), newInactiveProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2);
        
        cartService.addBundleItemToOrder(order, createBundleOrderItemRequest());
        cartService.addBundleItemToOrder(order, createBundleOrderItemRequestWithInactiveSku());
        
        return order;
    }

    public Order setUpAnonymousCartWithGiftWrap() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        Product newProduct = addTestProduct("Plastic Crate", "Crates");
        Product newInactiveProduct = addTestProduct("Plastic Crate", "Crates");
        Product giftWrapProduct = addTestProduct("Gift Box", "Gift Wraps");

        Category newCategory = newProduct.getDefaultCategory();

        List<OrderItem> addedItems = new ArrayList<OrderItem>();
        addedItems.add(cartService.addSkuToOrder(order.getId(), newProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2));
        addedItems.add(cartService.addSkuToOrder(order.getId(), newInactiveProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2));

        cartService.addGiftWrapItemToOrder(order, createGiftWrapOrderItemRequest(giftWrapProduct, giftWrapProduct.getDefaultSku(), 1, addedItems));

        return order;
    }

    public Order setUpAnonymousCartWithInactiveGiftWrap() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        Product newProduct = addTestProduct("Plastic Crate", "Crates");
        Product newInactiveProduct = addTestProduct("Plastic Crate", "Crates", false);
        Product giftWrapProduct = addTestProduct("Gift Box", "Gift Wraps");

        Category newCategory = newProduct.getDefaultCategory();

        List<OrderItem> addedItems = new ArrayList<OrderItem>();
        addedItems.add(cartService.addSkuToOrder(order.getId(), newProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2));
        addedItems.add(cartService.addSkuToOrder(order.getId(), newInactiveProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2));

        cartService.addGiftWrapItemToOrder(order, createGiftWrapOrderItemRequest(giftWrapProduct, giftWrapProduct.getDefaultSku(), 1, addedItems));

        return order;
    }
    
    public Order initializeExistingCartWithInactiveSkuAndInactiveBundle(Customer customer) throws PricingException {
        Product newProduct = addTestProduct("Plastic Crate", "Crates");
        Product newInactiveProduct = addTestProduct("Plastic Crate", "Crates", false);
        
        Category newCategory = newProduct.getDefaultCategory();

        Order order = cartService.createNewCartForCustomer(customer);

        cartService.addSkuToOrder(order.getId(), newProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2);
        cartService.addSkuToOrder(order.getId(), newInactiveProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2);
        
        cartService.addBundleItemToOrder(order, createBundleOrderItemRequest());
        cartService.addBundleItemToOrder(order, createBundleOrderItemRequestWithInactiveSku());

        return order;
    }

    public Order initializeExistingCart(Customer customer) throws PricingException {
        Product newProduct = addTestProduct("Plastic Crate", "Crates");
        Product newOtherProduct = addTestProduct("Plastic Crate", "Crates");
        
        Category newCategory = newProduct.getDefaultCategory();
        
        Order order = cartService.createNewCartForCustomer(customer);
        
        cartService.addSkuToOrder(order.getId(), newProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2);
        cartService.addSkuToOrder(order.getId(), newOtherProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2);
        
        return order;
    }
    
    public BundleOrderItemRequest createBundleOrderItemRequest() {
        Product screwProduct = addTestProduct("Bookshelf", "Components");
        Product shelfProduct = addTestProduct("Bookshelf", "Components");
        Product bracketsProduct = addTestProduct("Bookshelf", "Components");
        Category category = screwProduct.getDefaultCategory();
        
        List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
        discreteOrderItems.add(createDiscreteOrderItemRequest(screwProduct, screwProduct.getDefaultSku(), 20));
        discreteOrderItems.add(createDiscreteOrderItemRequest(shelfProduct, shelfProduct.getDefaultSku(), 3));
        discreteOrderItems.add(createDiscreteOrderItemRequest(bracketsProduct, bracketsProduct.getDefaultSku(), 6));
        
        BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
        itemRequest.setCategory(category);
        itemRequest.setName("test bundle " + bundleCount++);
        itemRequest.setQuantity(1);
        itemRequest.setDiscreteOrderItems(discreteOrderItems);
        return itemRequest;
    }
    
    public BundleOrderItemRequest createBundleOrderItemRequestWithInactiveSku() {
        Product drawerProduct = addTestProduct("Drawer System", "Systems");
        Product nailsProduct = addTestProduct("Drawer System", "Systems");
        Product tracksProduct = addTestProduct("Drawer System", "Systems", false);
        Category category = drawerProduct.getDefaultCategory();
        
        List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
        discreteOrderItems.add(createDiscreteOrderItemRequest(drawerProduct, drawerProduct.getDefaultSku(), 20));
        discreteOrderItems.add(createDiscreteOrderItemRequest(nailsProduct, nailsProduct.getDefaultSku(), 3));
        discreteOrderItems.add(createDiscreteOrderItemRequest(tracksProduct, tracksProduct.getDefaultSku(), 6));
        
        BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
        itemRequest.setCategory(category);
        itemRequest.setName("test bundle " + bundleCount++);
        itemRequest.setQuantity(1);
        itemRequest.setDiscreteOrderItems(discreteOrderItems);
        return itemRequest;
    }
    
    public DiscreteOrderItemRequest createDiscreteOrderItemRequest(Product product, Sku sku, int quantity) {
        DiscreteOrderItemRequest request = new DiscreteOrderItemRequest();
        request.setSku(sku);
        request.setQuantity(quantity);
        request.setProduct(product);
        request.setCategory(product.getDefaultCategory());
        return request;
    }

    public GiftWrapOrderItemRequest createGiftWrapOrderItemRequest(Product product, Sku sku, int quantity, List<OrderItem> wrappedItems) {
        GiftWrapOrderItemRequest request = new GiftWrapOrderItemRequest();
        request.setSku(sku);
        request.setQuantity(quantity);
        request.setProduct(product);
        request.setCategory(product.getDefaultCategory());
        request.setWrappedItems(wrappedItems);

        return request;
    }

    public Order setUpAnonymousCartWithInactiveBundleGiftWrap() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        Product newProduct = addTestProduct("Plastic Crate", "Crates");
        Product newInactiveProduct = addTestProduct("Plastic Crate", "Crates", false);
        Product giftWrapProduct = addTestProduct("Gift Box", "Gift Wraps");
        Category category = newProduct.getDefaultCategory();

        List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
        discreteOrderItems.add(createDiscreteOrderItemRequest(newProduct, newProduct.getDefaultSku(), 1));
        discreteOrderItems.add(createDiscreteOrderItemRequest(newInactiveProduct, newInactiveProduct.getDefaultSku(), 1));
        discreteOrderItems.add(createGiftWrapOrderItemRequest(giftWrapProduct, giftWrapProduct.getDefaultSku(), 1, new ArrayList<OrderItem>()));

        BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
        itemRequest.setCategory(category);
        itemRequest.setName("test bundle " + bundleCount++);
        itemRequest.setQuantity(1);
        itemRequest.setDiscreteOrderItems(discreteOrderItems);

        BundleOrderItem newBundle = (BundleOrderItem) cartService.addBundleItemToOrder(order, itemRequest);
        List<OrderItem> addedItems = new ArrayList<OrderItem>();
        GiftWrapOrderItem giftItem = null;
        for (DiscreteOrderItem addedItem : newBundle.getDiscreteOrderItems()) {
            if (addedItem instanceof GiftWrapOrderItem) {
                giftItem = (GiftWrapOrderItem) addedItem;
            } else {
                addedItems.add(addedItem);
            }
        }
        for (OrderItem addedItem : addedItems) {
            addedItem.setGiftWrapOrderItem(giftItem);
        }
        giftItem.getWrappedItems().addAll(addedItems);
        order = cartService.save(order, false);

        return order;
    }

    public Order setUpAnonymousCartWithBundleGiftWrap() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        BundleOrderItemRequest itemRequest = createBundleOrderItemRequestWithGiftWrap();

        BundleOrderItem newBundle = (BundleOrderItem) cartService.addBundleItemToOrder(order, itemRequest);
        List<OrderItem> addedItems = new ArrayList<OrderItem>();
        GiftWrapOrderItem giftItem = null;
        for (DiscreteOrderItem addedItem : newBundle.getDiscreteOrderItems()) {
            if (addedItem instanceof GiftWrapOrderItem) {
                giftItem = (GiftWrapOrderItem) addedItem;
            } else {
                addedItems.add(addedItem);
            }
        }
        for (OrderItem addedItem : addedItems) {
            addedItem.setGiftWrapOrderItem(giftItem);
        }
        giftItem.getWrappedItems().addAll(addedItems);
        order = cartService.save(order, false);

        return order;
    }

    protected BundleOrderItemRequest createBundleOrderItemRequestWithGiftWrap() {
        Product newProduct = addTestProduct("Plastic Crate", "Crates");
        Product newActiveProduct = addTestProduct("Plastic Crate", "Crates");
        Product giftWrapProduct = addTestProduct("Gift Box", "Gift Wraps");
        Category category = newProduct.getDefaultCategory();

        List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
        discreteOrderItems.add(createDiscreteOrderItemRequest(newProduct, newProduct.getDefaultSku(), 1));
        discreteOrderItems.add(createDiscreteOrderItemRequest(newActiveProduct, newActiveProduct.getDefaultSku(), 1));
        discreteOrderItems.add(createGiftWrapOrderItemRequest(giftWrapProduct, giftWrapProduct.getDefaultSku(), 1, new ArrayList<OrderItem>()));

        BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
        itemRequest.setCategory(category);
        itemRequest.setName("test bundle " + bundleCount++);
        itemRequest.setQuantity(1);
        itemRequest.setDiscreteOrderItems(discreteOrderItems);
        return itemRequest;
    }

    public Order setUpAnonymousCartWithBundleGiftWrapReferringToRootItems() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        Product newProduct = addTestProduct("Plastic Bowl", "Bowls");
        Product newActiveProduct = addTestProduct("Plastic Bowl", "Bowls");

        Category newCategory = newProduct.getDefaultCategory();

        List<OrderItem> addedItems = new ArrayList<OrderItem>();
        addedItems.add(cartService.addSkuToOrder(order.getId(), newProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2));
        addedItems.add(cartService.addSkuToOrder(order.getId(), newActiveProduct.getDefaultSku().getId(),
                newProduct.getId(), newCategory.getId(), 2));

        BundleOrderItem newBundle = (BundleOrderItem) cartService.addBundleItemToOrder(order, createBundleOrderItemRequestWithGiftWrap());
        GiftWrapOrderItem giftItem = null;
        for (DiscreteOrderItem addedItem : newBundle.getDiscreteOrderItems()) {
            if (addedItem instanceof GiftWrapOrderItem) {
                giftItem = (GiftWrapOrderItem) addedItem;
            }
        }
        for (OrderItem addedItem : addedItems) {
            addedItem.setGiftWrapOrderItem(giftItem);
        }
        giftItem.getWrappedItems().addAll(addedItems);
        order = cartService.save(order, false);

        return order;
    }

    public Order setUpAnonymousCartWithBundleGiftWrapReferringItemsInAnotherBundle() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        BundleOrderItem newBundle = (BundleOrderItem) cartService.addBundleItemToOrder(order, createBundleOrderItemRequest());
        BundleOrderItem newBundle2 = (BundleOrderItem) cartService.addBundleItemToOrder(order, createBundleOrderItemRequestWithGiftWrap());
        GiftWrapOrderItem giftItem = null;
        for (DiscreteOrderItem addedItem : newBundle2.getDiscreteOrderItems()) {
            if (addedItem instanceof GiftWrapOrderItem) {
                giftItem = (GiftWrapOrderItem) addedItem;
            }
        }
        for (DiscreteOrderItem addedItem : newBundle.getDiscreteOrderItems()) {
            addedItem.setGiftWrapOrderItem(giftItem);
        }
        giftItem.getWrappedItems().addAll(newBundle.getDiscreteOrderItems());
        order = cartService.save(order, false);

        return order;
    }

}
