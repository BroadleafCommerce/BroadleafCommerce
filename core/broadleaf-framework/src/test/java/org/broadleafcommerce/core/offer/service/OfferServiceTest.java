/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOfferImpl;
import org.broadleafcommerce.core.offer.domain.CustomerOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXref;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXrefImpl;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OfferRuleImpl;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessor;
import org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.processor.OfferTimeZoneProcessor;
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessorImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetailImpl;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.OrderItemQualifierImpl;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * 
 * @author jfischer
 *
 */
public class OfferServiceTest extends TestCase { 
    
    protected OfferServiceImpl offerService;
    protected CustomerOfferDao customerOfferDaoMock;
    protected OfferCodeDao offerCodeDaoMock;
    protected OfferDao offerDaoMock;
    protected OrderItemDao orderItemDaoMock;
    protected OrderService orderServiceMock;
    protected OrderItemService orderItemServiceMock;
    protected FulfillmentGroupItemDao fgItemDaoMock;
    protected OfferDataItemProvider dataProvider = new OfferDataItemProvider();
    protected OfferTimeZoneProcessor offerTimeZoneProcessorMock;

    private FulfillmentGroupService fgServiceMock;
    private OrderMultishipOptionService multishipOptionServiceMock;

    @Override
    protected void setUp() throws Exception {
        offerService = new OfferServiceImpl();
        customerOfferDaoMock = EasyMock.createMock(CustomerOfferDao.class);
        orderServiceMock = EasyMock.createMock(OrderService.class);
        offerCodeDaoMock = EasyMock.createMock(OfferCodeDao.class);
        offerDaoMock = EasyMock.createMock(OfferDao.class);
        orderItemDaoMock = EasyMock.createMock(OrderItemDao.class);
        offerService.setCustomerOfferDao(customerOfferDaoMock);
        offerService.setOfferCodeDao(offerCodeDaoMock);
        offerService.setOfferDao(offerDaoMock);
        offerService.setOrderService(orderServiceMock);
        orderItemServiceMock = EasyMock.createMock(OrderItemService.class);
        fgItemDaoMock = EasyMock.createMock(FulfillmentGroupItemDao.class);
        fgServiceMock = EasyMock.createMock(FulfillmentGroupService.class);
        multishipOptionServiceMock = EasyMock.createMock(OrderMultishipOptionService.class);
        offerTimeZoneProcessorMock = EasyMock.createMock(OfferTimeZoneProcessor.class);

        OfferServiceUtilitiesImpl offerServiceUtilities = new OfferServiceUtilitiesImpl();
        offerServiceUtilities.setOfferDao(offerDaoMock);
        offerServiceUtilities.setPromotableItemFactory(new PromotableItemFactoryImpl());

        OrderOfferProcessorImpl orderProcessor = new OrderOfferProcessorImpl();
        orderProcessor.setOfferDao(offerDaoMock);
        orderProcessor.setOrderItemDao(orderItemDaoMock);
        orderProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
        orderProcessor.setOfferTimeZoneProcessor(offerTimeZoneProcessorMock);
        orderProcessor.setOfferServiceUtilities(offerServiceUtilities);
        offerService.setOrderOfferProcessor(orderProcessor);


        ItemOfferProcessorImpl itemProcessor = new ItemOfferProcessorImpl();
        itemProcessor.setOfferDao(offerDaoMock);
        itemProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
        itemProcessor.setOfferServiceUtilities(offerServiceUtilities);
        offerService.setItemOfferProcessor(itemProcessor);

        FulfillmentGroupOfferProcessor fgProcessor = new FulfillmentGroupOfferProcessorImpl();
        fgProcessor.setOfferDao(offerDaoMock);
        fgProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
        offerService.setFulfillmentGroupOfferProcessor(fgProcessor);
        offerService.setPromotableItemFactory(new PromotableItemFactoryImpl());
    }

    public void replay() {
        EasyMock.replay(customerOfferDaoMock);
        EasyMock.replay(offerCodeDaoMock);
        EasyMock.replay(offerDaoMock);
        EasyMock.replay(orderItemDaoMock);
        EasyMock.replay(orderServiceMock);
        EasyMock.replay(orderItemServiceMock);
        EasyMock.replay(fgItemDaoMock);

        EasyMock.replay(fgServiceMock);
        EasyMock.replay(multishipOptionServiceMock);
        EasyMock.replay(offerTimeZoneProcessorMock);
    }

    public void verify() {
        EasyMock.verify(customerOfferDaoMock);
        EasyMock.verify(offerCodeDaoMock);
        EasyMock.verify(offerDaoMock);
        EasyMock.verify(orderItemDaoMock);
        EasyMock.verify(orderServiceMock);
        EasyMock.verify(orderItemServiceMock);
        EasyMock.verify(fgItemDaoMock);

        EasyMock.verify(fgServiceMock);
        EasyMock.verify(multishipOptionServiceMock);
        EasyMock.verify(offerTimeZoneProcessorMock);
    }

    public void testApplyOffersToOrder_Order() throws Exception {
        final ThreadLocal<Order> myOrder = new ThreadLocal<Order>();
        EasyMock.expect(offerDaoMock.createOrderItemPriceDetailAdjustment()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAdjustmentAnswer()).anyTimes();

        CandidateOrderOfferAnswer candidateOrderOfferAnswer = new CandidateOrderOfferAnswer();
        OrderAdjustmentAnswer orderAdjustmentAnswer = new OrderAdjustmentAnswer();
        EasyMock.expect(offerDaoMock.createOrderAdjustment()).andAnswer(orderAdjustmentAnswer).atLeastOnce();

        OrderItemPriceDetailAnswer orderItemPriceDetailAnswer = new OrderItemPriceDetailAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemPriceDetail()).andAnswer(orderItemPriceDetailAnswer).atLeastOnce();

        OrderItemQualifierAnswer orderItemQualifierAnswer = new OrderItemQualifierAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemQualifier()).andAnswer(orderItemQualifierAnswer).atLeastOnce();

        CandidateItemOfferAnswer candidateItemOfferAnswer = new CandidateItemOfferAnswer();
        OrderItemAdjustmentAnswer orderItemAdjustmentAnswer = new OrderItemAdjustmentAnswer();

        EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class),EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.findOrderById(EasyMock.isA(Long.class))).andAnswer(new IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                return myOrder.get();
            }
        }).anyTimes();

        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();
        EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();

        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new IAnswer<List<OrderMultishipOption>>() {
            @Override
            public List<OrderMultishipOption> answer() throws Throwable {
                return new ArrayList<OrderMultishipOption>();
            }
        }).anyTimes();

        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(fgServiceMock.collapseToOneShippableFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.expect(offerTimeZoneProcessorMock.getTimeZone(EasyMock.isA(OfferImpl.class))).andReturn(TimeZone.getTimeZone("CST")).anyTimes();

        replay();

        Order order = dataProvider.createBasicOrder();
        myOrder.set(order);
        List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", OfferDiscountType.PERCENT_OFF);

        offerService.applyAndSaveOffersToOrder(offers, order);

        int adjustmentCount = order.getOrderAdjustments().size();

        assertTrue(adjustmentCount == 1);
        assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(116.95D)));

        order = dataProvider.createBasicOrder();
        myOrder.set(order);
        offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", OfferDiscountType.PERCENT_OFF);
        List<Offer> offers2 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );
        offers.addAll(offers2);

        offerService.applyAndSaveOffersToOrder(offers, order);

        //with the item offers in play, the subtotal restriction for the order offer is no longer valid
        adjustmentCount = countItemAdjustments(order);
        int qualifierCount = countItemQualifiers(order);

        assertTrue(adjustmentCount == 2);
        assertTrue(qualifierCount == 2);
        adjustmentCount = order.getOrderAdjustments().size();
        assertTrue(adjustmentCount == 0);
        //assertTrue(order.getSubTotal().equals(new Money(124.95D)));

        order = dataProvider.createBasicOrder();
        myOrder.set(order);
        OfferRule orderRule = new OfferRuleImpl();
        //orderRule.setMatchRule("order.subTotal.getAmount()>124");
        orderRule.setMatchRule("order.subTotal.getAmount()>100");
        Offer offer = offers.get(0);
        OfferOfferRuleXref ruleXref = new OfferOfferRuleXrefImpl(offer, orderRule, OfferRuleType.ORDER.getType());
        offer.getOfferMatchRulesXref().put(OfferRuleType.ORDER.getType(), ruleXref);

        offerService.applyAndSaveOffersToOrder(offers, order);

        //now that the order restriction has been lessened, even with the item level discounts applied, 
        // the order offer still qualifies
        adjustmentCount = countItemAdjustments(order);
        qualifierCount = countItemQualifiers(order);

        assertTrue(adjustmentCount == 2);
        assertTrue(qualifierCount == 2);
        adjustmentCount = order.getOrderAdjustments().size();
        assertTrue(adjustmentCount == 1);
        assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(112.45D)));
        assertTrue(order.getSubTotal().equals(new Money(124.95D)));

        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        //offers.get(0).setCombinableWithOtherOffers(false);
        List<Offer> offers3 = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", OfferDiscountType.AMOUNT_OFF);
        offers.addAll(offers3);

        offerService.applyAndSaveOffersToOrder(offers, order);

        adjustmentCount = order.getOrderAdjustments().size();
        assertTrue(adjustmentCount == 2);

        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        offers.get(0).setCombinableWithOtherOffers(false);

        offerService.applyAndSaveOffersToOrder(offers, order);

        //there is a non combinable order offer now
        adjustmentCount = countItemAdjustments(order);
        qualifierCount = countItemQualifiers(order);

        assertTrue(adjustmentCount == 2);
        assertTrue(qualifierCount == 2);
        adjustmentCount = order.getOrderAdjustments().size();
        assertTrue(adjustmentCount == 1);
        assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(112.45D)));
        assertTrue(order.getSubTotal().equals(new Money(124.95D)));

        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        offers.get(0).setTotalitarianOffer(true);

        offerService.applyAndSaveOffersToOrder(offers, order);

        //there is a totalitarian order offer now - it is better than the item offers - the item offers are removed
        adjustmentCount = countItemAdjustments(order);
        qualifierCount = countItemQualifiers(order);

        assertTrue(adjustmentCount == 0);
        assertTrue(qualifierCount == 0);
        adjustmentCount = order.getOrderAdjustments().size();
        assertTrue(adjustmentCount == 1);
        assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(116.95D)));
        assertTrue(order.getSubTotal().equals(new Money(129.95D)));

        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        offers.get(0).setValue(new BigDecimal(".05"));
        offers.get(2).setValue(new BigDecimal(".01"));
        offers.get(2).setDiscountType(OfferDiscountType.PERCENT_OFF);

        offerService.applyAndSaveOffersToOrder(offers, order);

        //even though the first order offer is totalitarian, it is worth less than the order item offer, so it is removed.
        //the other order offer is still valid, however, and is included.
        adjustmentCount = countItemAdjustments(order);

        assertTrue(adjustmentCount == 2);
        adjustmentCount = order.getOrderAdjustments().size();
        assertTrue(adjustmentCount == 1);
        assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(124.94D)));
        assertTrue(order.getSubTotal().equals(new Money(124.95D)));

        verify();
    }

    private int countItemAdjustments(Order order) {
        int adjustmentCount = 0;
        for (OrderItem item : order.getOrderItems()) {
            for (OrderItemPriceDetail detail : item.getOrderItemPriceDetails()) {
                if (detail.getOrderItemPriceDetailAdjustments() != null) {
                    adjustmentCount += detail.getOrderItemPriceDetailAdjustments().size();
                }

            }
        }
        return adjustmentCount;
    }

    private int countItemQualifiers(Order order) {
        int qualifierCount = 0;
        for (OrderItem item : order.getOrderItems()) {
            for (OrderItemQualifier qualifier : item.getOrderItemQualifiers()) {
                qualifierCount = qualifierCount += qualifier.getQuantity();
            }
        }
        return qualifierCount;
    }

    public void testApplyOffersToOrder_Items() throws Exception {
        final ThreadLocal<Order> myOrder = new ThreadLocal<Order>();
        EasyMock.expect(offerDaoMock.createOrderItemPriceDetailAdjustment()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAdjustmentAnswer()).anyTimes();

        CandidateItemOfferAnswer answer = new CandidateItemOfferAnswer();
        OrderItemAdjustmentAnswer answer2 = new OrderItemAdjustmentAnswer();

        OrderItemPriceDetailAnswer orderItemPriceDetailAnswer = new OrderItemPriceDetailAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemPriceDetail()).andAnswer(orderItemPriceDetailAnswer).atLeastOnce();

        OrderItemQualifierAnswer orderItemQualifierAnswer = new OrderItemQualifierAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemQualifier()).andAnswer(orderItemQualifierAnswer).atLeastOnce();

        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();
        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class),EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();
        EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();

        EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();

        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new IAnswer<List<OrderMultishipOption>>() {
            @Override
            public List<OrderMultishipOption> answer() throws Throwable {
                return new ArrayList<OrderMultishipOption>();
            }
        }).anyTimes();

        EasyMock.expect(orderServiceMock.findOrderById(EasyMock.isA(Long.class))).andAnswer(new IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                return myOrder.get();
            }
        }).anyTimes();

        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(fgServiceMock.collapseToOneShippableFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(offerTimeZoneProcessorMock.getTimeZone(EasyMock.isA(OfferImpl.class))).andReturn(TimeZone.getTimeZone("CST")).anyTimes();

        replay();

        Order order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );

        offerService.applyAndSaveOffersToOrder(offers, order);

        int adjustmentCount = countItemAdjustments(order);

        assertTrue(adjustmentCount == 2);

        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);

        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );

        offerService.applyAndSaveOffersToOrder(offers, order);

        adjustmentCount = countItemAdjustments(order);

        //Qualifiers are there, but the targets are not, so no adjustments
        assertTrue(adjustmentCount == 0);

        verify();
    }

    public void testBuildOfferListForOrder() throws Exception {
        EasyMock.expect(customerOfferDaoMock.readCustomerOffersByCustomer(EasyMock.isA(Customer.class))).andReturn(new ArrayList<CustomerOffer>());
        EasyMock.expect(offerDaoMock.readOffersByAutomaticDeliveryType()).andReturn(dataProvider.createCustomerBasedOffer(null, dataProvider.yesterday(), dataProvider.tomorrow(), OfferDiscountType.PERCENT_OFF));

        replay();

        Order order = dataProvider.createBasicPromotableOrder().getOrder();
        List<Offer> offers = offerService.buildOfferListForOrder(order);

        assertTrue(offers.size() == 1);

        verify();
    }

    public class CandidateItemOfferAnswer implements IAnswer<CandidateItemOffer> {

        @Override
        public CandidateItemOffer answer() throws Throwable {
            return new CandidateItemOfferImpl();
        }

    }

    public class OrderItemAdjustmentAnswer implements IAnswer<OrderItemAdjustment> {

        @Override
        public OrderItemAdjustment answer() throws Throwable {
            return new OrderItemAdjustmentImpl();
        }

    }

    public class CandidateOrderOfferAnswer implements IAnswer<CandidateOrderOffer> {

        @Override
        public CandidateOrderOffer answer() throws Throwable {
            return new CandidateOrderOfferImpl();
        }

    }

    public class OrderAdjustmentAnswer implements IAnswer<OrderAdjustment> {

        @Override
        public OrderAdjustment answer() throws Throwable {
            return new OrderAdjustmentImpl();
        }

    }

    public class OrderItemPriceDetailAnswer implements IAnswer<OrderItemPriceDetail> {

        @Override
        public OrderItemPriceDetail answer() throws Throwable {
            return new OrderItemPriceDetailImpl();
        }
    }

    public class OrderItemQualifierAnswer implements IAnswer<OrderItemQualifier> {

        @Override
        public OrderItemQualifier answer() throws Throwable {
            return new OrderItemQualifierImpl();
        }
    }
}
