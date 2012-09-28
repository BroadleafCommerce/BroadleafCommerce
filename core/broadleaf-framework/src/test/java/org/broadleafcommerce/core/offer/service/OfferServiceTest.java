/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.offer.service;

import junit.framework.TestCase;

/**
 * 
 * @author jfischer
 *
 */
public class OfferServiceTest extends TestCase { 
	
//	private OfferServiceImpl offerService;
//	private CustomerOfferDao customerOfferDaoMock;
//	private OfferCodeDao offerCodeDaoMock;
//	private OfferDao offerDaoMock;
//	private OrderService orderServiceMock;
//	private OrderItemService orderItemServiceMock;
//	private FulfillmentGroupItemDao fgItemDaoMock;
//	private OfferDataItemProvider dataProvider = new OfferDataItemProvider();
//
//	private FulfillmentGroupService fgServiceMock;
//    private OrderMultishipOptionService multishipOptionServiceMock;
//
//	@Override
//	protected void setUp() throws Exception {
//		offerService = new OfferServiceImpl();
//		customerOfferDaoMock = EasyMock.createMock(CustomerOfferDao.class);
//		offerCodeDaoMock = EasyMock.createMock(OfferCodeDao.class);
//		offerDaoMock = EasyMock.createMock(OfferDao.class);
//		offerService.setCustomerOfferDao(customerOfferDaoMock);
//		offerService.setOfferCodeDao(offerCodeDaoMock);
//		offerService.setOfferDao(offerDaoMock);
//		orderServiceMock = EasyMock.createMock(OrderService.class);
//		orderItemServiceMock = EasyMock.createMock(OrderItemService.class);
//		fgItemDaoMock = EasyMock.createMock(FulfillmentGroupItemDao.class);
//		fgServiceMock = EasyMock.createMock(FulfillmentGroupService.class);
//        multishipOptionServiceMock = EasyMock.createMock(OrderMultishipOptionService.class);
//
//		OrderOfferProcessor orderProcessor = new OrderOfferProcessorImpl();
//		orderProcessor.setOfferDao(offerDaoMock);
//		orderProcessor.setOrderService(orderServiceMock);
//		orderProcessor.setFulfillmentGroupItemDao(fgItemDaoMock);
//		orderProcessor.setOrderItemService(orderItemServiceMock);
//		orderProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
//		orderProcessor.setFulfillmentGroupService(fgServiceMock);
//        orderProcessor.setOrderMultishipOptionService(multishipOptionServiceMock);
//		offerService.setOrderOfferProcessor(orderProcessor);
//
//		ItemOfferProcessor itemProcessor = new ItemOfferProcessorImpl();
//		itemProcessor.setOfferDao(offerDaoMock);
//		itemProcessor.setOrderService(orderServiceMock);
//		itemProcessor.setFulfillmentGroupItemDao(fgItemDaoMock);
//		itemProcessor.setOrderItemService(orderItemServiceMock);
//		itemProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
//		itemProcessor.setFulfillmentGroupService(fgServiceMock);
//        itemProcessor.setOrderMultishipOptionService(multishipOptionServiceMock);
//		offerService.setItemOfferProcessor(itemProcessor);
//
//		FulfillmentGroupOfferProcessor fgProcessor = new FulfillmentGroupOfferProcessorImpl();
//		fgProcessor.setOfferDao(offerDaoMock);
//		fgProcessor.setOrderService(orderServiceMock);
//		fgProcessor.setFulfillmentGroupItemDao(fgItemDaoMock);
//		fgProcessor.setOrderItemService(orderItemServiceMock);
//		fgProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
//		fgProcessor.setFulfillmentGroupService(fgServiceMock);
//        fgProcessor.setOrderMultishipOptionService(multishipOptionServiceMock);
//		offerService.setFulfillmentGroupOfferProcessor(fgProcessor);
//		offerService.setPromotableItemFactory(new PromotableItemFactoryImpl());
//	}
//
//	public void replay() {
//		EasyMock.replay(customerOfferDaoMock);
//		EasyMock.replay(offerCodeDaoMock);
//		EasyMock.replay(offerDaoMock);
//		EasyMock.replay(orderServiceMock);
//		EasyMock.replay(orderItemServiceMock);
//		EasyMock.replay(fgItemDaoMock);
//
//		EasyMock.replay(fgServiceMock);
//        EasyMock.replay(multishipOptionServiceMock);
//	}
//
//	public void verify() {
//		EasyMock.verify(customerOfferDaoMock);
//		EasyMock.verify(offerCodeDaoMock);
//		EasyMock.verify(offerDaoMock);
//		EasyMock.verify(orderServiceMock);
//		EasyMock.verify(orderItemServiceMock);
//		EasyMock.verify(fgItemDaoMock);
//
//		EasyMock.verify(fgServiceMock);
//        EasyMock.verify(multishipOptionServiceMock);
//	}
//
//	public void testApplyOffersToOrder_Order() throws Exception {
//		CandidateOrderOfferAnswer candidateOrderOfferAnswer = new CandidateOrderOfferAnswer();
//		OrderAdjustmentAnswer orderAdjustmentAnswer = new OrderAdjustmentAnswer();
//		EasyMock.expect(offerDaoMock.createCandidateOrderOffer()).andAnswer(candidateOrderOfferAnswer).atLeastOnce();
//		EasyMock.expect(offerDaoMock.createOrderAdjustment()).andAnswer(orderAdjustmentAnswer).atLeastOnce();
//
//		CandidateItemOfferAnswer candidateItemOfferAnswer = new CandidateItemOfferAnswer();
//		OrderItemAdjustmentAnswer orderItemAdjustmentAnswer = new OrderItemAdjustmentAnswer();
//		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(candidateItemOfferAnswer).atLeastOnce();
//		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(orderItemAdjustmentAnswer).atLeastOnce();
//
//		EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
//		EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
//        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class),EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();
//
//        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();
//		EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
//		EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();
//
//        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new IAnswer<List<OrderMultishipOption>>() {
//            @Override
//            public List<OrderMultishipOption> answer() throws Throwable {
//                return new ArrayList<OrderMultishipOption>();
//            }
//        }).anyTimes();
//
//        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
//        EasyMock.expectLastCall().anyTimes();
//        EasyMock.expect(fgServiceMock.collapseToOneFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
//        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
//
//        replay();
//
//		Order order = dataProvider.createBasicOrder().getDelegate();
//		List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", OfferDiscountType.PERCENT_OFF);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		int adjustmentCount = order.getOrderAdjustments().size();
//
//		assertTrue(adjustmentCount == 1);
//		assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(116.95D)));
//
//		order = dataProvider.createBasicOrder().getDelegate();
//		offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", OfferDiscountType.PERCENT_OFF);
//		List<Offer> offers2 = dataProvider.createItemBasedOfferWithItemCriteria(
//			"order.subTotal.getAmount()>20",
//			OfferDiscountType.PERCENT_OFF,
//			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
//			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
//		);
//		offers.addAll(offers2);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		//with the item offers in play, the subtotal restriction for the order offer is no longer valid
//		adjustmentCount = 0;
//		for (OrderItem item : order.getOrderItems()) {
//			if (item.getOrderItemAdjustments() != null) {
//				adjustmentCount += item.getOrderItemAdjustments().size();
//			}
//		}
//
//		assertTrue(adjustmentCount == 2);
//		adjustmentCount = order.getOrderAdjustments().size();
//		assertTrue(adjustmentCount == 0);
//		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
//
//		order = dataProvider.createBasicOrder().getDelegate();
//		OfferRule orderRule = new OfferRuleImpl();
//		orderRule.setMatchRule("order.subTotal.getAmount()>124");
//		offers.get(0).getOfferMatchRules().put(OfferRuleType.ORDER.getType(), orderRule);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		//now that the order restriction has been lessened, even with the item level discounts applied, the order offer still qualifies
//		adjustmentCount = 0;
//		for (OrderItem item : order.getOrderItems()) {
//			if (item.getOrderItemAdjustments() != null) {
//				adjustmentCount += item.getOrderItemAdjustments().size();
//			}
//		}
//
//		assertTrue(adjustmentCount == 2);
//		adjustmentCount = order.getOrderAdjustments().size();
//		assertTrue(adjustmentCount == 1);
//		assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(112.45D)));
//		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
//
//		order = dataProvider.createBasicOrder().getDelegate();
//		//offers.get(0).setCombinableWithOtherOffers(false);
//		List<Offer> offers3 = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", OfferDiscountType.AMOUNT_OFF);
//		offers.addAll(offers3);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		adjustmentCount = order.getOrderAdjustments().size();
//		assertTrue(adjustmentCount == 2);
//
//		order = dataProvider.createBasicOrder().getDelegate();
//		offers.get(0).setCombinableWithOtherOffers(false);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		//there is a non combinable order offer now
//		adjustmentCount = 0;
//		for (OrderItem item : order.getOrderItems()) {
//			if (item.getOrderItemAdjustments() != null) {
//				adjustmentCount += item.getOrderItemAdjustments().size();
//			}
//		}
//
//		assertTrue(adjustmentCount == 2);
//		adjustmentCount = order.getOrderAdjustments().size();
//		assertTrue(adjustmentCount == 1);
//		assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(112.45D)));
//		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
//
//		order = dataProvider.createBasicOrder().getDelegate();
//		offers.get(0).setTotalitarianOffer(true);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		//there is a totalitarian order offer now - it is better than the item offers - the item offers are removed
//		adjustmentCount = 0;
//		for (OrderItem item : order.getOrderItems()) {
//			if (item.getOrderItemAdjustments() != null) {
//				adjustmentCount += item.getOrderItemAdjustments().size();
//			}
//		}
//
//		assertTrue(adjustmentCount == 0);
//		adjustmentCount = order.getOrderAdjustments().size();
//		assertTrue(adjustmentCount == 1);
//		assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(116.95D)));
//		assertTrue(order.getSubTotal().equals(new Money(129.95D)));
//
//		order = dataProvider.createBasicOrder().getDelegate();
//		offers.get(0).setValue(new BigDecimal(".05"));
//		offers.get(2).setValue(new BigDecimal(".01"));
//		offers.get(2).setDiscountType(OfferDiscountType.PERCENT_OFF);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		//even though the first order offer is totalitarian, it is worth less than the order item offer, so it is removed.
//		//the other order offer is still valid, however, and is included.
//		adjustmentCount = 0;
//		for (OrderItem item : order.getOrderItems()) {
//			if (item.getOrderItemAdjustments() != null) {
//				adjustmentCount += item.getOrderItemAdjustments().size();
//			}
//		}
//
//		assertTrue(adjustmentCount == 2);
//		adjustmentCount = order.getOrderAdjustments().size();
//		assertTrue(adjustmentCount == 1);
//		assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(124.94D)));
//		assertTrue(order.getSubTotal().equals(new Money(124.95D)));
//
//		verify();
//	}
//
//	public void testApplyOffersToOrder_Items() throws Exception {
//		CandidateItemOfferAnswer answer = new CandidateItemOfferAnswer();
//		OrderItemAdjustmentAnswer answer2 = new OrderItemAdjustmentAnswer();
//		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(2);
//		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(answer2).times(2);
//
//        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();
//        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class),EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();
//		EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
//		EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();
//
//		EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
//		EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
//
//        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new IAnswer<List<OrderMultishipOption>>() {
//            @Override
//            public List<OrderMultishipOption> answer() throws Throwable {
//                return new ArrayList<OrderMultishipOption>();
//            }
//        }).anyTimes();
//
//        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
//        EasyMock.expectLastCall().anyTimes();
//        EasyMock.expect(fgServiceMock.collapseToOneFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
//        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
//
//		replay();
//
//		Order order = dataProvider.createBasicOrder().getDelegate();
//		List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
//			"order.subTotal.getAmount()>20",
//			OfferDiscountType.PERCENT_OFF,
//			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
//			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
//		);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		int adjustmentCount = 0;
//		for (OrderItem item : order.getOrderItems()) {
//			if (item.getOrderItemAdjustments() != null) {
//				adjustmentCount += item.getOrderItemAdjustments().size();
//			}
//		}
//
//		assertTrue(adjustmentCount == 2);
//
//		order = dataProvider.createBasicOrder().getDelegate();
//
//		offers = dataProvider.createItemBasedOfferWithItemCriteria(
//			"order.subTotal.getAmount()>20",
//			OfferDiscountType.PERCENT_OFF,
//			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
//			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
//		);
//
//		offerService.applyOffersToOrder(offers, order);
//
//		adjustmentCount = 0;
//		for (OrderItem item : order.getOrderItems()) {
//			if (item.getOrderItemAdjustments() != null) {
//				adjustmentCount += item.getOrderItemAdjustments().size();
//			}
//		}
//
//		//Qualifiers are there, but the targets are not, so no adjustments
//		assertTrue(adjustmentCount == 0);
//
//		verify();
//	}
//
//	public void testBuildOfferListForOrder() throws Exception {
//		EasyMock.expect(customerOfferDaoMock.readCustomerOffersByCustomer(EasyMock.isA(Customer.class))).andReturn(new ArrayList<CustomerOffer>());
//		EasyMock.expect(offerDaoMock.readOffersByAutomaticDeliveryType()).andReturn(dataProvider.createCustomerBasedOffer(null, dataProvider.yesterday(), dataProvider.tomorrow(), OfferDiscountType.PERCENT_OFF));
//
//		replay();
//
//		Order order = dataProvider.createBasicOrder().getDelegate();
//		List<Offer> offers = offerService.buildOfferListForOrder(order);
//
//		assertTrue(offers.size() == 1);
//
//		verify();
//	}
//
//	public class CandidateItemOfferAnswer implements IAnswer<CandidateItemOffer> {
//
//		@Override
//        public CandidateItemOffer answer() throws Throwable {
//			return new CandidateItemOfferImpl();
//		}
//
//	}
//
//	public class OrderItemAdjustmentAnswer implements IAnswer<OrderItemAdjustment> {
//
//		@Override
//        public OrderItemAdjustment answer() throws Throwable {
//			return new OrderItemAdjustmentImpl();
//		}
//
//	}
//
//	public class CandidateOrderOfferAnswer implements IAnswer<CandidateOrderOffer> {
//
//		@Override
//        public CandidateOrderOffer answer() throws Throwable {
//			return new CandidateOrderOfferImpl();
//		}
//
//	}
//
//	public class OrderAdjustmentAnswer implements IAnswer<OrderAdjustment> {
//
//		@Override
//        public OrderAdjustment answer() throws Throwable {
//			return new OrderAdjustmentImpl();
//		}
//
//	}
}
