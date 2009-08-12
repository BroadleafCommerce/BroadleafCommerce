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
package org.broadleafcommerce.order.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.call.MergeCartResponse;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    @Resource
    private CartService cartService;
    
    @Resource
    private CustomerService customerService;
    
    @Resource
    private OrderService orderService;
    
    @Resource
    private CatalogService catalogService;
    
    private int bundleCount = 0;

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveAllItemsToCartFromNamedOrder() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
    	cart = cartService.moveAllItemsToCartFromNamedOrder(namedOrder);
    	assert namedOrderItems.equals(cart.getOrderItems());
    	assert namedOrder.getOrderItems().size() == 0;
    }
    
    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddAllItemsToCartFromNamedOrder() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
    	cartService.setMoveNamedOrderItems(false);
    	cart = cartService.addAllItemsToCartFromNamedOrder(namedOrder);
    	assert namedOrderItems.equals(cart.getOrderItems());
    	cartService.setMoveNamedOrderItems(true);
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddAllItemsToCartFromNamedOrderWithoutExistingCart() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	cartService.setMoveNamedOrderItems(false);
    	Order cart = cartService.addAllItemsToCartFromNamedOrder(namedOrder);
    	assert namedOrderItems.equals(cart.getOrderItems());
    	cartService.setMoveNamedOrderItems(true);
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testAddItemToCartFromNamedOrder() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
    	cartService.setMoveNamedOrderItems(false);
    	OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder, namedOrderItems.get(0));
    	cartService.setMoveNamedOrderItems(true);
    	assert movedItem != null;
    	assert cart.getOrderItems().size() == 1;
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrder() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	Order cart = cartService.createNewCartForCustomer(namedOrder.getCustomer());
    	OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder, namedOrderItems.get(0));
    	
    	List<Order> customerNamedOrders = cartService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);
    	assert customerNamedOrders.size() == 0;
    	assert movedItem != null;
    	assert cart.getOrderItems().size() == 1;
    	assert namedOrder.getOrderItems().size() == 0;
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrderWithoutExistingCart() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder, namedOrderItems.get(0));
    	List<Order> customerNamedOrders = cartService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);

    	Order cart = cartService.findCartForCustomer(namedOrder.getCustomer());
    	assert customerNamedOrders.size() == 0;
    	assert movedItem != null;
    	assert cart.getOrderItems().size() == 1;
    	assert namedOrder.getOrderItems().size() == 0;
    }

    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testMoveItemToCartFromNamedOrderByIds() throws PricingException {
    	Order namedOrder = setUpNamedOrder();
    	List<OrderItem> namedOrderItems = new ArrayList<OrderItem>();
    	namedOrderItems.addAll(namedOrder.getOrderItems());
    	OrderItem movedItem = cartService.moveItemToCartFromNamedOrder(namedOrder.getCustomer().getId(), 
    			namedOrder.getName(), namedOrderItems.get(0).getId(), namedOrderItems.get(0).getQuantity());
    	List<Order> customerNamedOrders = cartService.findOrdersForCustomer(namedOrder.getCustomer(), OrderStatus.NAMED);
    	
    	Order cart = cartService.findCartForCustomer(namedOrder.getCustomer());
    	assert customerNamedOrders.size() == 0;
    	assert movedItem != null;
    	assert cart.getOrderItems().size() == 1;
    	assert namedOrder.getOrderItems().size() == 0;
    }

    
    
    //TODO: move this to OrderTest
    @Test(groups = { "testCartAndNamedOrder" })
    @Transactional
    public void testCreateNamedOrder() throws PricingException {
        Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));
        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);

        Category category = new CategoryImpl();
        category.setName("Pants");
        category.setActiveStartDate(activeStartCal.getTime());
        category = catalogService.saveCategory(category);
        Product newProduct = new ProductImpl();

        newProduct.setActiveStartDate(activeStartCal.getTime());

        newProduct.setDefaultCategory(category);
        newProduct.setName("Leather Pants");
        newProduct = catalogService.saveProduct(newProduct);

        Sku newSku = new SkuImpl();
        newSku.setName("Red Leather Pants");
        newSku.setRetailPrice(new Money(44.99));
        newSku.setActiveStartDate(activeStartCal.getTime());
        newSku.setDiscountable(true);
        newSku = catalogService.saveSku(newSku);
        List<Sku> allSkus = new ArrayList<Sku>();
        allSkus.add(newSku);
        newProduct.setAllSkus(allSkus);
        newProduct = catalogService.saveProduct(newProduct);

        Order order = orderService.createNamedOrderForCustomer("Pants Order", customer);

        OrderItem orderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem quantityNullOrderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), category.getId(), null);
        OrderItem skuNullOrderItem = orderService.addSkuToOrder(order.getId(), null,
                newProduct.getId(), category.getId(), 2);
        OrderItem orderNullOrderItem = orderService.addSkuToOrder(null, newSku.getId(),
                newProduct.getId(), category.getId(), 2);
        OrderItem productNullOrderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                null, category.getId(), 2);
        OrderItem categoryNullOrderItem = orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), null, 2);
        
        assert orderItem != null;
        assert skuNullOrderItem == null;
        assert quantityNullOrderItem == null;
        assert orderNullOrderItem == null;
        assert productNullOrderItem != null;
        assert categoryNullOrderItem != null;
    }
    
    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeToEmptyCart() throws PricingException {
    	Order anonymousCart = setUpAnonymousCartWithInactiveSku();
    	Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));
    	MergeCartResponse response = cartService.mergeCart(customer, anonymousCart.getId());
    	assert response.getAddedItems().size() == 2;
    	assert response.getOrder().getOrderItems().size() == 2;
    	assert response.isMerged() == true;
    	assert response.getRemovedItems().size() == 2;
    }
    
    @Transactional
    @Test(groups = { "testMergeCart" }) 
    public void testMergeToExistingCart() throws PricingException {
    	//sets up anonymous cart with a DiscreteOrderItem, inactive DiscreteOrderItem, BundleOrderItem, and inactive BundleOrderItem
    	Order anonymousCart = setUpAnonymousCartWithInactiveSku();
    	Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));
    	
    	//sets up existing cart with a DiscreteOrderItem, inactive DiscreteOrderItem, BundleOrderItem, and inactive BundleOrderItem
    	setUpExistingCartWithInactiveSkuAndInactiveBundle(customer);
    	MergeCartResponse response = cartService.mergeCart(customer, anonymousCart.getId());
    	assert response.getAddedItems().size() == 2;
    	assert response.getOrder().getOrderItems().size() == 4;
    	assert response.isMerged();
    	assert response.getRemovedItems().size() == 4;
    }
    
    private Order setUpNamedOrder() throws PricingException {
        Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));

        Sku newSku = addTestSku("Small Cube Box", "Cube Box", "Boxes");

        Order order = orderService.createNamedOrderForCustomer("Boxes Named Order", customer);
        
        Product newProduct = newSku.getAllParentProducts().get(0);
        Category newCategory = newProduct.getDefaultCategory();

        orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), newCategory.getId(), 2);
    	
        return order;
    }
    
    private Order setUpAnonymousCartWithInactiveSku() throws PricingException {
        Customer customer = customerService.saveCustomer(customerService.createCustomerFromId(null));

        Order order = cartService.createNewCartForCustomer(customer);

        Sku newSku = addTestSku("Small Plastic Crate", "Plastic Crate", "Crates");
        Sku newInactiveSku = addTestSku("Small Red Plastic Crate", "Plastic Crate", "Crates", false);
        
        Product newProduct = newSku.getAllParentProducts().get(0);
        Category newCategory = newProduct.getDefaultCategory();

        orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), newCategory.getId(), 2);
        orderService.addSkuToOrder(order.getId(), newInactiveSku.getId(),
        		newProduct.getId(), newCategory.getId(), 2);
    	
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequest());
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequestWithInactiveSku());
        
        return order;
    }
    


    private Order setUpExistingCartWithInactiveSkuAndInactiveBundle(Customer customer) throws PricingException {
        Sku newSku = addTestSku("Large Plastic Crate", "Plastic Crate", "Crates");
        Sku newInactiveSku = addTestSku("Large Red Plastic Crate", "Plastic Crate", "Crates", false);
        
        Product newProduct = newSku.getAllParentProducts().get(0);
        Category newCategory = newProduct.getDefaultCategory();

        Order order = cartService.createNewCartForCustomer(customer);

        orderService.addSkuToOrder(order.getId(), newSku.getId(),
        		newProduct.getId(), newCategory.getId(), 2);
        orderService.addSkuToOrder(order.getId(), newInactiveSku.getId(),
        		newProduct.getId(), newCategory.getId(), 2);
        
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequest());
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequestWithInactiveSku());

        return order;
    }
    
    private Sku addTestSku(String skuName, String productName, String categoryName) {
    	return addTestSku(skuName, productName, categoryName, true);
    }
    
    private Sku addTestSku(String skuName, String productName, String categoryName, boolean active) {
    	Calendar activeStartCal = Calendar.getInstance();
    	activeStartCal.add(Calendar.DAY_OF_YEAR, -2);

    	Category category = new CategoryImpl();
        category.setName(categoryName);
        category.setActiveStartDate(activeStartCal.getTime());
        category = catalogService.saveCategory(category);
        Product newProduct = new ProductImpl();

        Calendar activeEndCal = Calendar.getInstance();
        activeEndCal.add(Calendar.DAY_OF_YEAR, -1);
        newProduct.setActiveStartDate(activeStartCal.getTime());
        
        newProduct.setDefaultCategory(category);
        newProduct.setName(productName);
        newProduct = catalogService.saveProduct(newProduct);

        List<Product> products = new ArrayList<Product>();
        products.add(newProduct);
        
        Sku newSku = new SkuImpl();
        newSku.setName(skuName);
        newSku.setRetailPrice(new Money(44.99));
        newSku.setActiveStartDate(activeStartCal.getTime());
        
        if (!active) {
        	newSku.setActiveEndDate(activeEndCal.getTime());
        }
        newSku.setDiscountable(true);
        newSku = catalogService.saveSku(newSku);
        newSku.setAllParentProducts(products);
        
        List<Sku> allSkus = new ArrayList<Sku>();
        allSkus.add(newSku);
        newProduct.setAllSkus(allSkus);
        newProduct = catalogService.saveProduct(newProduct);

        return newSku;
    }
    
    
    private BundleOrderItemRequest createBundleOrderItemRequest() {
        Sku screwSku = addTestSku("Screw", "Bookshelf", "Components");
        Sku shelfSku = addTestSku("Shelf", "Bookshelf", "Components");
        Sku bracketsSku = addTestSku("Brackets", "Bookshelf", "Components");
        Category category = screwSku.getAllParentProducts().get(0).getDefaultCategory();
        
        List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
        discreteOrderItems.add(createDiscreteOrderItemRequest(screwSku, 20));
        discreteOrderItems.add(createDiscreteOrderItemRequest(shelfSku, 3));
        discreteOrderItems.add(createDiscreteOrderItemRequest(bracketsSku, 6));
        
        BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
        itemRequest.setCategory(category);
        itemRequest.setName("test bundle " + bundleCount++);
        itemRequest.setQuantity(1);
        itemRequest.setDiscreteOrderItems(discreteOrderItems);
        return itemRequest;
    }
    
    private BundleOrderItemRequest createBundleOrderItemRequestWithInactiveSku() {
    	Sku drawerSku = addTestSku("Drawer", "Drawer System", "Systems");
    	Sku nailsSku = addTestSku("Nails", "Drawer System", "Systems");
    	Sku tracksSku = addTestSku("Tracks", "Drawer System", "Systems", false);
    	Category category = drawerSku.getAllParentProducts().get(0).getDefaultCategory();
    	
    	List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
    	discreteOrderItems.add(createDiscreteOrderItemRequest(drawerSku, 20));
    	discreteOrderItems.add(createDiscreteOrderItemRequest(nailsSku, 3));
    	discreteOrderItems.add(createDiscreteOrderItemRequest(tracksSku, 6));
    	
    	BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
    	itemRequest.setCategory(category);
    	itemRequest.setName("test bundle " + bundleCount++);
    	itemRequest.setQuantity(1);
    	itemRequest.setDiscreteOrderItems(discreteOrderItems);
    	return itemRequest;
    }
    
    private DiscreteOrderItemRequest createDiscreteOrderItemRequest(Sku sku, int quantity) {
    	Product product = sku.getAllParentProducts().get(0);
    	DiscreteOrderItemRequest request = new DiscreteOrderItemRequest();
        request.setSku(sku);
        request.setQuantity(quantity);
        request.setProduct(product);
        request.setCategory(product.getDefaultCategory());
        return request;
    }
}
