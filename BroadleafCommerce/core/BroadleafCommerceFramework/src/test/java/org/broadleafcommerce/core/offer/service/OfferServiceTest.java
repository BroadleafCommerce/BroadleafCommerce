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
package org.broadleafcommerce.core.offer.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOfferImpl;
import org.broadleafcommerce.core.offer.domain.CustomerOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OfferRuleImpl;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

/**
 * 
 * @author jfischer
 *
 */
public class OfferServiceTest extends TestCase { 
	
	private OfferServiceImpl offerService;
	private CustomerOfferDao customerOfferDaoMock;
	private OfferCodeDao offerCodeDaoMock;
	private OfferDao offerDaoMock;
	private OfferDataItemProvider dataProvider = new OfferDataItemProvider();
	
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
	}
	
	public void replay() {
		EasyMock.replay(customerOfferDaoMock);
		EasyMock.replay(offerCodeDaoMock);
		EasyMock.replay(offerDaoMock);
	}
	
	public void verify() {
		EasyMock.verify(customerOfferDaoMock);
		EasyMock.verify(offerCodeDaoMock);
		EasyMock.verify(offerDaoMock);
	}
	
	public void testApplyOffersToOrder_Order() throws Exception {
		CandidateOrderOfferAnswer candidateOrderOfferAnswer = new CandidateOrderOfferAnswer();
		OrderAdjustmentAnswer orderAdjustmentAnswer = new OrderAdjustmentAnswer();
		EasyMock.expect(offerDaoMock.createCandidateOrderOffer()).andAnswer(candidateOrderOfferAnswer).atLeastOnce();
		EasyMock.expect(offerDaoMock.createOrderAdjustment()).andAnswer(orderAdjustmentAnswer).atLeastOnce();
		
		CandidateItemOfferAnswer candidateItemOfferAnswer = new CandidateItemOfferAnswer();
		OrderItemAdjustmentAnswer orderItemAdjustmentAnswer = new OrderItemAdjustmentAnswer();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(candidateItemOfferAnswer).atLeastOnce();
		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(orderItemAdjustmentAnswer).atLeastOnce();
		
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", OfferDiscountType.PERCENT_OFF);
		
		offerService.applyOffersToOrder(offers, order);
		
		int adjustmentCount = order.getOrderAdjustments().size();
		
		assertTrue(adjustmentCount == 1);
		assertTrue(order.getAdjustmentPrice().equals(new Money(116.95D)));
		
		order = dataProvider.createBasicOrder();
		offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", OfferDiscountType.PERCENT_OFF);
		List<Offer> offers2 = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		offers.addAll(offers2);
		
		offerService.applyOffersToOrder(offers, order);
		
		//with the item offers in play, the subtotal restriction for the order offer is no longer valid
		adjustmentCount = 0;
		for (OrderItem item : order.getOrderItems()) {
			if (item.getOrderItemAdjustments() != null) {
				adjustmentCount += item.getOrderItemAdjustments().size();
			}
		}
		
		assertTrue(adjustmentCount == 2);
		adjustmentCount = order.getOrderAdjustments().size();
		assertTrue(adjustmentCount == 0);
		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
		
		order = dataProvider.createBasicOrder();
		OfferRule orderRule = new OfferRuleImpl();
		orderRule.setMatchRule("order.subTotal.getAmount()>124");
		offers.get(0).getOfferMatchRules().put(OfferRuleType.ORDER.getType(), orderRule);
		
		offerService.applyOffersToOrder(offers, order);
		
		//now that the order restriction has been lessened, even with the item level discounts applied, the order offer still qualifies
		adjustmentCount = 0;
		for (OrderItem item : order.getOrderItems()) {
			if (item.getOrderItemAdjustments() != null) {
				adjustmentCount += item.getOrderItemAdjustments().size();
			}
		}
		
		assertTrue(adjustmentCount == 2);
		adjustmentCount = order.getOrderAdjustments().size();
		assertTrue(adjustmentCount == 1);
		assertTrue(order.getAdjustmentPrice().equals(new Money(112.45D)));
		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
		
		order = dataProvider.createBasicOrder();
		//offers.get(0).setCombinableWithOtherOffers(false);
		List<Offer> offers3 = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", OfferDiscountType.AMOUNT_OFF);
		offers.addAll(offers3);
		
		offerService.applyOffersToOrder(offers, order);
		
		adjustmentCount = order.getOrderAdjustments().size();
		assertTrue(adjustmentCount == 2);
		
		order = dataProvider.createBasicOrder();
		offers.get(0).setCombinableWithOtherOffers(false);
		
		offerService.applyOffersToOrder(offers, order);
		
		//there is a non combinable order offer now
		adjustmentCount = 0;
		for (OrderItem item : order.getOrderItems()) {
			if (item.getOrderItemAdjustments() != null) {
				adjustmentCount += item.getOrderItemAdjustments().size();
			}
		}
		
		assertTrue(adjustmentCount == 2);
		adjustmentCount = order.getOrderAdjustments().size();
		assertTrue(adjustmentCount == 1);
		assertTrue(order.getAdjustmentPrice().equals(new Money(112.45D)));
		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
		
		order = dataProvider.createBasicOrder();
		offers.get(0).setTotalitarianOffer(true);
		
		offerService.applyOffersToOrder(offers, order);
		
		//there is a totalitarian order offer now - it is better than the item offers - the item offers are removed
		adjustmentCount = 0;
		for (OrderItem item : order.getOrderItems()) {
			if (item.getOrderItemAdjustments() != null) {
				adjustmentCount += item.getOrderItemAdjustments().size();
			}
		}
		
		assertTrue(adjustmentCount == 0);
		adjustmentCount = order.getOrderAdjustments().size();
		assertTrue(adjustmentCount == 1);
		assertTrue(order.getAdjustmentPrice().equals(new Money(116.95D)));
		assertTrue(order.getSubTotal().equals(new Money(129.95D)));
		
		order = dataProvider.createBasicOrder();
		offers.get(0).setValue(new BigDecimal(".05"));
		offers.get(2).setValue(new BigDecimal(".01"));
		offers.get(2).setDiscountType(OfferDiscountType.PERCENT_OFF);
		
		offerService.applyOffersToOrder(offers, order);
		
		//even though the first order offer is totalitarian, it is worth less than the order item offer, so it is removed.
		//the other order offer is still valid, however, and is included.
		adjustmentCount = 0;
		for (OrderItem item : order.getOrderItems()) {
			if (item.getOrderItemAdjustments() != null) {
				adjustmentCount += item.getOrderItemAdjustments().size();
			}
		}
		
		assertTrue(adjustmentCount == 2);
		adjustmentCount = order.getOrderAdjustments().size();
		assertTrue(adjustmentCount == 1);
		assertTrue(order.getAdjustmentPrice().equals(new Money(124.94D)));
		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
		
		verify();
	}
	
	public void testApplyOffersToOrder_Items() throws Exception {
		CandidateItemOfferAnswer answer = new CandidateItemOfferAnswer();
		OrderItemAdjustmentAnswer answer2 = new OrderItemAdjustmentAnswer();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(2);
		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(answer2).times(2);
		
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		
		offerService.applyOffersToOrder(offers, order);
		
		int adjustmentCount = 0;
		for (OrderItem item : order.getOrderItems()) {
			if (item.getOrderItemAdjustments() != null) {
				adjustmentCount += item.getOrderItemAdjustments().size();
			}
		}
		
		assertTrue(adjustmentCount == 2);
		
		order = dataProvider.createBasicOrder();
		
		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		
		offerService.applyOffersToOrder(offers, order);
		
		adjustmentCount = 0;
		for (OrderItem item : order.getOrderItems()) {
			if (item.getOrderItemAdjustments() != null) {
				adjustmentCount += item.getOrderItemAdjustments().size();
			}
		}
		
		//Qualifiers are there, but the targets are not, so no adjustments
		assertTrue(adjustmentCount == 0);
		
		verify();
	}

	public void testBuildOfferListForOrder() throws Exception {
		EasyMock.expect(customerOfferDaoMock.readCustomerOffersByCustomer(EasyMock.isA(Customer.class))).andReturn(new ArrayList<CustomerOffer>());
		EasyMock.expect(offerDaoMock.readOffersByAutomaticDeliveryType()).andReturn(dataProvider.createCustomerBasedOffer(null, dataProvider.yesterday(), dataProvider.tomorrow(), OfferDiscountType.PERCENT_OFF));
		
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<Offer> offers = offerService.buildOfferListForOrder(order);
		
		assertTrue(offers.size() == 1);
		
		verify();
	}

	public class CandidateItemOfferAnswer implements IAnswer<CandidateItemOffer> {

		public CandidateItemOffer answer() throws Throwable {
			return new CandidateItemOfferImpl();
		}
		
	}
	
	public class OrderItemAdjustmentAnswer implements IAnswer<OrderItemAdjustment> {

		public OrderItemAdjustment answer() throws Throwable {
			return new OrderItemAdjustmentImpl();
		}
		
	}
	
	public class CandidateOrderOfferAnswer implements IAnswer<CandidateOrderOffer> {

		public CandidateOrderOffer answer() throws Throwable {
			return new CandidateOrderOfferImpl();
		}
		
	}
	
	public class OrderAdjustmentAnswer implements IAnswer<OrderAdjustment> {

		public OrderAdjustment answer() throws Throwable {
			return new OrderAdjustmentImpl();
		}
		
	}
}
