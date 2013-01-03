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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteriaImpl;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OfferRuleImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
public class OfferDataItemProvider {

	public static Long orderItemId = 1L;
	public static Long orderId = 1L;
	
	public static Long getOrderItemId() {
		return orderItemId++;
	}
	
	public static Long getOrderId() {
		return orderId++;
	}
	
	protected static Map<Long, Order> orders = new HashMap<Long, Order>();
	
	public static IAnswer<FulfillmentGroup> getAddItemToFulfillmentGroupAnswer() {
		return new IAnswer<FulfillmentGroup>() {
			@Override
            public FulfillmentGroup answer() throws Throwable {
				FulfillmentGroupItemRequest fgItemRequest = (FulfillmentGroupItemRequest) EasyMock.getCurrentArguments()[0];
				FulfillmentGroup fg = fgItemRequest.getFulfillmentGroup();
				FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
				fgItem.setOrderItem(fgItemRequest.getOrderItem());
				fgItem.setQuantity(fgItemRequest.getQuantity());
				fg.getFulfillmentGroupItems().add(fgItem);
				
				return fg;
			}
		};
	}

    public static IAnswer<FulfillmentGroupItem> getCreateFulfillmentGroupItemAnswer() {
        return new IAnswer<FulfillmentGroupItem>() {
            @Override
            public FulfillmentGroupItem answer() throws Throwable {
                return new FulfillmentGroupItemImpl();
            }
        };
    }
	
	public static IAnswer<OrderItem> getAddOrderItemToOrderAnswer() {
		return new IAnswer<OrderItem>() {
			@Override
            public OrderItem answer() throws Throwable {
				Order order = (Order) EasyMock.getCurrentArguments()[0];
				order.getOrderItems().add((OrderItem) EasyMock.getCurrentArguments()[1]);
				if (((OrderItem) EasyMock.getCurrentArguments()[1]).getId() == null) {
					((OrderItem) EasyMock.getCurrentArguments()[1]).setId(OfferDataItemProvider.getOrderItemId());
				}
				return (OrderItem) EasyMock.getCurrentArguments()[1];
			}
		};
	}
	
	public static IAnswer<OrderItem> getSaveOrderItemAnswer() {
		return new IAnswer<OrderItem>() {
			@Override
            public OrderItem answer() throws Throwable {
				OrderItem orderItem = (OrderItem) EasyMock.getCurrentArguments()[0];
				if (orderItem.getId() == null) {
					orderItem.setId(getOrderItemId());
				}
				return orderItem;
			}
		};
	}
	
	public static IAnswer<Order> getSaveOrderAnswer() {
		return new IAnswer<Order>() {
			@Override
            public Order answer() throws Throwable {
				Order order = (Order) EasyMock.getCurrentArguments()[0];
				order.setId(getOrderId());
				orders.put(order.getId(), order);
				return order;
			}
		};
	}

    public static IAnswer<Order> getSameOrderAnswer() {
        return new IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                return (Order) EasyMock.getCurrentArguments()[0];
            }
        };
    }

	
	public static IAnswer<FulfillmentGroupItem> getSaveFulfillmentGroupItemAnswer() {
		return new IAnswer<FulfillmentGroupItem>() {
			@Override
            public FulfillmentGroupItem answer() throws Throwable {
				return (FulfillmentGroupItem) EasyMock.getCurrentArguments()[0];
			}
		};
	}
	
	public static IAnswer<Order> getRemoveItemFromOrderAnswer() {
		return new IAnswer<Order>() {
			@Override
            public Order answer() throws Throwable {
				Long orderId = (Long) EasyMock.getCurrentArguments()[0];
				Order order = orders.get(orderId);
				
				Iterator<OrderItem> orderItemItr = order.getOrderItems().listIterator();
				while (orderItemItr.hasNext()) {
					OrderItem item = orderItemItr.next();
					if (item.getId().equals(EasyMock.getCurrentArguments()[1])) {
						orderItemItr.remove();
					}
				}
				
				for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
					Iterator<FulfillmentGroupItem> itr = fg.getFulfillmentGroupItems().iterator();
					while (itr.hasNext()) {
						if (itr.next().getOrderItem().getId().equals(EasyMock.getCurrentArguments()[1])) {
							itr.remove();
						}
					}
				}
				return order;
			}
		};
	}
	
	public PromotableOrder createBasicOrder() {
		Order order = new OrderImpl();
		order.setId(getOrderId());
		
		Category category1 = new CategoryImpl();
		category1.setName("test1");
		category1.setId(1L);
		
		Product product1 = new ProductImpl();
		
		Sku sku1 = new SkuImpl();
		sku1.setName("test1");
        sku1.setId(1L);
		sku1.setDiscountable(true);
		sku1.setRetailPrice(new Money(19.99D));
		product1.setDefaultSku(sku1);
		
		category1.getAllProducts().add(product1);
		
		Category category2 = new CategoryImpl();
		category2.setName("test2");
		category2.setId(2L);
		
		Product product2 = new ProductImpl();
		
		Sku sku2 = new SkuImpl();
		sku2.setName("test2");
        sku2.setId(2L);
		sku2.setDiscountable(true);
		sku2.setRetailPrice(new Money(29.99D));
		product2.setDefaultSku(sku2);
		
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
		orderItem1.setId(getOrderItemId());
		orderItem1.setOrder(order);
		
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
		orderItem2.setId(getOrderItemId());
		orderItem2.setOrder(order);
		
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
		fg1.setId(1L);
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

        Phone primary = new PhoneImpl();
        primary.setPhoneNumber("972-976-1234");
		address1.setPhonePrimary(primary);
		
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
		fg1.setType(FulfillmentType.SHIPPING);
		fg1.setOrder(order);
		
		FulfillmentGroupItem fgItem1 = new FulfillmentGroupItemImpl();
		fgItem1.setFulfillmentGroup(fg1);
		fgItem1.setOrderItem(orderItem1);
		fgItem1.setQuantity(2);
		//fgItem1.setRetailPrice(new Money(19.99D));
		fg1.getFulfillmentGroupItems().add(fgItem1);
		
		order.getFulfillmentGroups().add(fg1);
		
		FulfillmentGroup fg2 = new FulfillmentGroupImpl();
		fg2.setId(2L);
		Address address2 = new AddressImpl();
		address2.setAddressLine1("124 Test Road");
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
		fg2.setType(FulfillmentType.SHIPPING);
		fg2.setOrder(order);
		
		FulfillmentGroupItem fgItem2 = new FulfillmentGroupItemImpl();
		fgItem2.setFulfillmentGroup(fg2);
		fgItem2.setOrderItem(orderItem2);
		fgItem2.setQuantity(3);
		//fgItem2.setRetailPrice(new Money(29.99D));
		fg2.getFulfillmentGroupItems().add(fgItem2);
		
		order.getFulfillmentGroups().add(fg2);
		
		order.setSubTotal(new Money((2 * 19.99D) + (3 * 29.99D)));
		
		orders.put(order.getId(), order);
		
		PromotableOrder promotableOrder = new PromotableOrderImpl(order, new PromotableItemFactoryImpl());
		
		return promotableOrder;
	}
	
	public Offer createOffer(
		String appliesToCustomerRules, 
		String appliesToFulfillmentGroupRules, 
		String appliesToRules, 
		boolean applyToSalePrice,
		boolean combinableWithOtherOffers,
		OfferDeliveryType deliveryType,
		OfferDiscountType type,
		Date endDate,
		int maxUses,
		OfferItemRestrictionRuleType qualifierType,
		OfferItemRestrictionRuleType targetType,
		int priority,
		Set<OfferItemCriteria> qualifyingItemCriteria,
		boolean stackable,
		Date startDate,
        Set<OfferItemCriteria> targetItemCriteria,
		boolean totalitarianOffer,
		OfferType offerType,
		BigDecimal value
	) {
		Offer offer = new OfferImpl();
		OfferRule customerRule = new OfferRuleImpl();
		customerRule.setMatchRule(appliesToCustomerRules);
		offer.getOfferMatchRules().put(OfferRuleType.CUSTOMER.getType(), customerRule);
		OfferRule fgRule = new OfferRuleImpl();
		fgRule.setMatchRule(appliesToFulfillmentGroupRules);
		offer.getOfferMatchRules().put(OfferRuleType.FULFILLMENT_GROUP.getType(), fgRule);
		OfferRule orderRule = new OfferRuleImpl();
		orderRule.setMatchRule(appliesToRules);
		offer.getOfferMatchRules().put(OfferRuleType.ORDER.getType(), orderRule);
		offer.setApplyDiscountToSalePrice(applyToSalePrice);
		offer.setCombinableWithOtherOffers(combinableWithOtherOffers);
		offer.setDeliveryType(deliveryType);
		offer.setDiscountType(type);
		offer.setEndDate(endDate);
		offer.setMaxUses(maxUses);
		offer.setOfferItemQualifierRuleType(qualifierType);
		offer.setOfferItemTargetRuleType(targetType);
		offer.setPriority(priority);
		offer.setQualifyingItemCriteria(qualifyingItemCriteria);
		offer.setStackable(stackable);
		offer.setStartDate(startDate);
		offer.setTargetItemCriteria(targetItemCriteria);
		offer.setTotalitarianOffer(totalitarianOffer);
		offer.setType(offerType);
		offer.setValue(value);
		offer.setTreatAsNewFormat(true);
		
		return offer;
	}
	
	public Date yesterday() {
		long now = System.currentTimeMillis();
		long then = now - (1000 * 60 * 60 * 24);
		return new Date(then);
	}
	
	public Date tomorrow() {
		long now = System.currentTimeMillis();
		long then = now + (1000 * 60 * 60 * 24);
		return new Date(then);
	}
	
	public List<Offer> createCustomerBasedOffer(String customerRule, Date startDate, Date endDate, OfferDiscountType discountType) {
		Offer offer = createOffer(customerRule, null, null, true, true, OfferDeliveryType.AUTOMATIC, discountType, endDate, 0, OfferItemRestrictionRuleType.NONE, OfferItemRestrictionRuleType.NONE, 1, null, true, startDate, null, false, OfferType.ORDER, BigDecimal.valueOf(10));
		List<Offer> offers = new ArrayList<Offer>();
		offers.add(offer);
		
		return offers;
	}
	
	public List<Offer> createOrderBasedOffer(String orderRule, OfferDiscountType discountType) {
		Offer offer = createOffer(null, null, orderRule, true, true, OfferDeliveryType.AUTOMATIC, discountType, tomorrow(), 0, OfferItemRestrictionRuleType.NONE, OfferItemRestrictionRuleType.NONE, 1, null, true, yesterday(), null, false, OfferType.ORDER, BigDecimal.valueOf(10));
		List<Offer> offers = new ArrayList<Offer>();
		offers.add(offer);
		
		return offers;
	}
	
	public List<Offer> createFGBasedOffer(String orderRule, String fgRule, OfferDiscountType discountType) {
		Offer offer = createOffer(null, fgRule, orderRule, true, true, OfferDeliveryType.AUTOMATIC, discountType, tomorrow(), 0, OfferItemRestrictionRuleType.NONE, OfferItemRestrictionRuleType.NONE, 1, null, true, yesterday(), null, false, OfferType.FULFILLMENT_GROUP, BigDecimal.valueOf(10));
		List<Offer> offers = new ArrayList<Offer>();
		offers.add(offer);
		
		return offers;
	}
	
	public List<Offer> createItemBasedOffer(String orderRule, String targetRule, OfferDiscountType discountType) {
		List<Offer> offers = createOrderBasedOffer(orderRule, discountType);
		offers.get(0).setType(OfferType.ORDER_ITEM);
		
		if (targetRule != null) {
            Set<OfferItemCriteria> targetSet = new HashSet<OfferItemCriteria>();
			OfferItemCriteria targetCriteria = new OfferItemCriteriaImpl();
			targetCriteria.setQualifyingOffer(offers.get(0));
			targetCriteria.setQuantity(1);
			targetCriteria.setOrderItemMatchRule(targetRule);
            targetSet.add(targetCriteria);
			
			offers.get(0).setTargetItemCriteria(targetSet);
		}
		
		return offers;
	}
	
	public List<Offer> createOrderBasedOfferWithItemCriteria(String orderRule, OfferDiscountType discountType, String orderItemMatchRule) {
		List<Offer> offers = createOrderBasedOffer(orderRule, discountType);
		
		OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
		qualCriteria.setQualifyingOffer(offers.get(0));
		qualCriteria.setQuantity(1);
		qualCriteria.setOrderItemMatchRule(orderItemMatchRule);
		Set<OfferItemCriteria> criterias = new HashSet<OfferItemCriteria>();
		criterias.add(qualCriteria);
		
		offers.get(0).setQualifyingItemCriteria(criterias);
		
		return offers;
	}
	
	public List<Offer> createFGBasedOfferWithItemCriteria(String orderRule, String fgRule, OfferDiscountType discountType, String orderItemMatchRule) {
		List<Offer> offers = createFGBasedOffer(orderRule, fgRule, discountType);
		
		OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
		qualCriteria.setQualifyingOffer(offers.get(0));
		qualCriteria.setQuantity(1);
		qualCriteria.setOrderItemMatchRule(orderItemMatchRule);
		Set<OfferItemCriteria> criterias = new HashSet<OfferItemCriteria>();
		criterias.add(qualCriteria);
		
		offers.get(0).setQualifyingItemCriteria(criterias);
		
		return offers;
	}
	
	public List<Offer> createItemBasedOfferWithItemCriteria(String orderRule, OfferDiscountType discountType, String qualRule, String targetRule) {
		List<Offer> offers = createItemBasedOffer(orderRule, targetRule, discountType);
		
		if (qualRule != null) {
			OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
			qualCriteria.setQualifyingOffer(offers.get(0));
			qualCriteria.setQuantity(1);
			qualCriteria.setOrderItemMatchRule(qualRule);
			Set<OfferItemCriteria> criterias = new HashSet<OfferItemCriteria>();
			criterias.add(qualCriteria);
			
			offers.get(0).setQualifyingItemCriteria(criterias);
		}
		
		return offers;
	}
}
