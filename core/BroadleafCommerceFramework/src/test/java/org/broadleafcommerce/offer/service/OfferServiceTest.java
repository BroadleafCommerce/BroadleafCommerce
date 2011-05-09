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
package org.broadleafcommerce.offer.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CustomerOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.OfferServiceImpl;
import org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessorImpl;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.easymock.classextension.EasyMock;

/**
 * 
 * @author jfischer
 *
 */
public class OfferServiceTest extends TestCase { 
	
	private OfferService offerService;
	private CustomerOfferDao customerOfferDaoMock;
	private OfferCodeDao offerCodeDaoMock;
	private OfferDao offerDaoMock;
	
	@Override
	protected void setUp() throws Exception {
		offerService = new OfferServiceImpl();
		customerOfferDaoMock = EasyMock.createMock(CustomerOfferDao.class);
		offerCodeDaoMock = EasyMock.createMock(OfferCodeDao.class);
		offerDaoMock = EasyMock.createMock(OfferDao.class);
		offerService.setCustomerOfferDao(customerOfferDaoMock);
		offerService.setOfferCodeDao(offerCodeDaoMock);
		offerService.setOfferDao(offerDaoMock);
		
		OrderOfferProcessor orderProcessor = new OrderOfferProcessorImpl();
		orderProcessor.setOfferDao(offerDaoMock);
		offerService.setOrderOfferProcessor(orderProcessor);
		
		ItemOfferProcessor itemProcessor = new ItemOfferProcessorImpl();
		itemProcessor.setOfferDao(offerDaoMock);
		offerService.setItemOfferProcessor(itemProcessor);
		
		FulfillmentGroupOfferProcessor fgProcessor = new FulfillmentGroupOfferProcessorImpl();
		fgProcessor.setOfferDao(offerDaoMock);
		offerService.setFulfillmentGroupOfferProcessor(fgProcessor);
		
		EasyMock.expect(customerOfferDaoMock.readCustomerOffersByCustomer(EasyMock.isA(Customer.class))).andReturn(new ArrayList<CustomerOffer>());
	}
	
	private Order createBasicOrder() {
		Order order = new OrderImpl();
		
		Category category1 = new CategoryImpl();
		category1.setName("test1");
		
		Product product1 = new ProductImpl();
		product1.setName("test1");
		
		Sku sku1 = new SkuImpl();
		sku1.setName("test1");
		sku1.setDiscountable(true);
		sku1.setRetailPrice(new Money(19.99D));
		product1.getAllSkus().add(sku1);
		
		category1.getAllProducts().add(product1);
		
		Category category2 = new CategoryImpl();
		category2.setName("test2");
		
		Product product2 = new ProductImpl();
		product2.setName("test2");
		
		Sku sku2 = new SkuImpl();
		sku2.setName("test2");
		sku2.setDiscountable(true);
		sku2.setRetailPrice(new Money(29.99D));
		product2.getAllSkus().add(sku2);
		
		category2.getAllProducts().add(product2);
		
		DiscreteOrderItem orderItem1 = new DiscreteOrderItemImpl();
		orderItem1.setCategory(category1);
		orderItem1.setName("test1");
		orderItem1.setOrder(order);
		orderItem1.setOrderItemType(OrderItemType.DISCRETE);
		orderItem1.setProduct(product1);
		orderItem1.setQuantity(2);
		orderItem1.setSku(sku1);
		orderItem1.setRetailPrice(new Money(19.99D));
		orderItem1.setPrice(new Money(19.99D));
		
		order.getOrderItems().add(orderItem1);
		
		DiscreteOrderItem orderItem2 = new DiscreteOrderItemImpl();
		orderItem2.setCategory(category2);
		orderItem2.setName("test2");
		orderItem2.setOrder(order);
		orderItem2.setOrderItemType(OrderItemType.DISCRETE);
		orderItem2.setProduct(product2);
		orderItem2.setQuantity(3);
		orderItem2.setSku(sku2);
		orderItem2.setRetailPrice(new Money(29.99D));
		orderItem2.setPrice(new Money(29.99D));
		
		order.getOrderItems().add(orderItem2);
		
		Customer customer = new CustomerImpl();
		customer.setEmailAddress("test@test.com");
		customer.setFirstName("John");
		customer.setLastName("Tester");
		customer.setReceiveEmail(true);
		customer.setRegistered(true);
		
		order.setCustomer(customer);
		
		order.setEmailAddress("test@test.com");
		
		FulfillmentGroup fg1 = new FulfillmentGroupImpl();
		Address address1 = new AddressImpl();
		address1.setAddressLine1("123 Test Road");
		address1.setCity("Dallas");
		
		Country country = new CountryImpl();
		country.setAbbreviation("US");
		country.setName("United States");
		
		address1.setCountry(country);
		address1.setDefault(true);
		address1.setFirstName("John");
		address1.setLastName("Tester");
		address1.setPostalCode("75244");
		address1.setPrimaryPhone("972-976-1234");
		
		State state = new StateImpl();
		state.setAbbreviation("TX");
		state.setCountry(country);
		state.setName("Texas");
		
		address1.setState(state);
		fg1.setAddress(address1);
		fg1.setOrder(order);
		fg1.setPrimary(true);
		fg1.setRetailShippingPrice(new Money(10D));
		fg1.setShippingPrice(new Money(10D));
		fg1.setType(FulfillmentGroupType.SHIPPING);
		
		FulfillmentGroupItem fgItem1 = new FulfillmentGroupItemImpl();
		fgItem1.setFulfillmentGroup(fg1);
		fgItem1.setOrderItem(orderItem1);
		fgItem1.setQuantity(2);
		fgItem1.setRetailPrice(new Money(19.99D));
		fg1.getFulfillmentGroupItems().add(fgItem1);
		
		order.getFulfillmentGroups().add(fg1);
		
		FulfillmentGroup fg2 = new FulfillmentGroupImpl();
		Address address2 = new AddressImpl();
		address2.setAddressLine1("123 Test Road");
		address2.setCity("Dallas");
		
		Country country2 = new CountryImpl();
		country2.setAbbreviation("US");
		country2.setName("United States");
		
		address2.setCountry(country2);
		address2.setDefault(true);
		address2.setFirstName("John");
		address2.setLastName("Tester");
		address2.setPostalCode("75244");
		address2.setPrimaryPhone("972-976-1234");
		
		State state2 = new StateImpl();
		state2.setAbbreviation("TX");
		state2.setCountry(country2);
		state2.setName("Texas");
		
		address2.setState(state2);
		fg2.setAddress(address2);
		fg2.setOrder(order);
		fg2.setPrimary(true);
		fg2.setRetailShippingPrice(new Money(20D));
		fg2.setShippingPrice(new Money(20D));
		fg2.setType(FulfillmentGroupType.SHIPPING);
		
		FulfillmentGroupItem fgItem2 = new FulfillmentGroupItemImpl();
		fgItem2.setFulfillmentGroup(fg2);
		fgItem2.setOrderItem(orderItem2);
		fgItem2.setQuantity(3);
		fgItem2.setRetailPrice(new Money(29.99D));
		fg2.getFulfillmentGroupItems().add(fgItem2);
		
		order.getFulfillmentGroups().add(fg2);
		
		order.setSubTotal(new Money((2 * 19.99D) + (3 * 29.99D)));
		
		return order;
	}

	public void testCustomerRuleOnlyOffer() throws Exception {
		/*EasyMock.expect(offerDaoMock.readOffersByAutomaticDeliveryType()).andReturn(new ArrayList<Offer>());
		
		EasyMock.replay(customerOfferDaoMock);
		EasyMock.replay(offerCodeDaoMock);
		EasyMock.replay(offerDaoMock);
		
		Order order = createBasicOrder();
		List<Offer> offers = offerService.buildOfferListForOrder(order);
		
		assertTrue(offers.size() > 0);
		
		EasyMock.verify(customerOfferDaoMock);
		EasyMock.verify(offerCodeDaoMock);
		EasyMock.verify(offerDaoMock);*/
	}

}
