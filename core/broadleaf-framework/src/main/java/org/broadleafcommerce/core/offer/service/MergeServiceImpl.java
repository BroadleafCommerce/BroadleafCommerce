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

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemImpl;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.manipulation.BundleOrderItemSplitContainer;
import org.broadleafcommerce.core.order.service.manipulation.OrderItemSplitContainer;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Service("blMergeService")
public class MergeServiceImpl implements MergeService {

    @Resource(name = "blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fulfillmentGroupItemDao;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name="blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blPromotableItemFactory")
    protected PromotableItemFactory promotableItemFactory;

    @Override
    public void gatherSplitItemsInBundles(Order order) throws PricingException {
        List<DiscreteOrderItem> itemsToRemove = new ArrayList<DiscreteOrderItem>();
        List<FulfillmentGroupItem> fgItemsToRemove = new ArrayList<FulfillmentGroupItem>();
        Map<Long, Map<String, Object[]>> gatherMap = new HashMap<Long, Map<String, Object[]>>();
        for (FulfillmentGroup group : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgItem : group.getFulfillmentGroupItems()) {
                OrderItem orderItem = fgItem.getOrderItem();
                if (orderItem instanceof DiscreteOrderItem && ((DiscreteOrderItem) orderItem).getBundleOrderItem() != null) {
                    Map<String, Object[]> gatheredItem = gatherMap.get(((DiscreteOrderItem) orderItem).getSku().getId());
                    if (gatheredItem == null) {
                        gatheredItem = new HashMap<String, Object[]>();
                        gatherMap.put(((DiscreteOrderItem) orderItem).getSku().getId(), gatheredItem);
                    }

                    gatherFulfillmentGroupLinkedDiscreteOrderItem(itemsToRemove, fgItemsToRemove, gatheredItem, fgItem, (DiscreteOrderItem) orderItem, null, orderMultishipOptionService.findOrderMultishipOptions(order.getId()), false);
                }
            }
        }
        for (Map<String, Object[]> values : gatherMap.values()) {
            for (Object[] item : values.values()) {
                orderItemService.saveOrderItem((OrderItem) item[0]);
                fulfillmentGroupItemDao.save((FulfillmentGroupItem) item[1]);
            }
        }
        for (FulfillmentGroupItem fgItem : fgItemsToRemove) {
            FulfillmentGroup fg = fgItem.getFulfillmentGroup();
            fg.getFulfillmentGroupItems().remove(fgItem);
            fulfillmentGroupItemDao.delete(fgItem);
            if (fg.getFulfillmentGroupItems().isEmpty()) {
                order.getFulfillmentGroups().remove(fg);
                fulfillmentGroupService.delete(fg);
            }
        }
        for (DiscreteOrderItem orderItem : itemsToRemove) {
            if (orderItem.getBundleOrderItem() != null) {
                BundleOrderItem bundleOrderItem = orderItem.getBundleOrderItem();
                bundleOrderItem.getDiscreteOrderItems().remove(orderItem);
                orderItem.setBundleOrderItem(null);
                orderItemService.saveOrderItem(bundleOrderItem);
            }
        }

    }

    @Override
    public void mergeSplitItems(final PromotableOrder order) {
        try {
            mergeSplitDiscreteOrderItems(order);

            mergeSplitBundleOrderItems(order);

            order.resetDiscreteOrderItems();

            for (PromotableOrderItem myItem : order.getDiscountableDiscreteOrderItems()) {
                //reset adjustment retail and sale values, since their transient values are erased after the above persistence events
                if (myItem.isHasOrderItemAdjustments()) {
                    for (OrderItemAdjustment adjustment : myItem.getDelegate().getOrderItemAdjustments()) {
                        PromotableOrderItemAdjustment promotableOrderItemAdjustment = promotableItemFactory.createPromotableOrderItemAdjustment(adjustment, myItem);
                        myItem.resetAdjustmentPrice();
                        promotableOrderItemAdjustment.computeAdjustmentValues();
                        myItem.computeAdjustmentPrice();
                    }
                }
            }


        } catch (PricingException e) {
            throw new RuntimeException("Could not propagate the items split by the promotion engine into the order", e);
        }
    }

    @Override
    public void finalizeCart(PromotableOrder order) throws PricingException {
        if (order.isHasMultiShipOptions()) {
            List<OrderMultishipOption> multishipOptions = new ArrayList<OrderMultishipOption>(order.getMultiShipOptions());
            List<FulfillmentGroupItem> itemsToRemove = new ArrayList<FulfillmentGroupItem>();
            for (OrderMultishipOption option : multishipOptions) {
                for (FulfillmentGroupItem item : order.getDelegate().getFulfillmentGroups().get(0).getFulfillmentGroupItems()) {
                    if (option.getOrderItem().getId().equals(item.getOrderItem().getId())) {
                        FulfillmentGroupRequest fgr = new FulfillmentGroupRequest();
                        fgr.setOrder(order.getDelegate());
                        if (option.getAddress() != null) {
                            fgr.setAddress(option.getAddress());
                        }
                        if (option.getFulfillmentOption() != null) {
                            fgr.setOption(option.getFulfillmentOption());
                        }
                        FulfillmentGroup fg = fulfillmentGroupService.addFulfillmentGroupToOrder(fgr, false);
                        fg = fulfillmentGroupService.save(fg);
                        order.getDelegate().getFulfillmentGroups().add(fg);

                        FulfillmentGroupItem fulfillmentGroupItem = fulfillmentGroupItemDao.create();
                        fulfillmentGroupItem.setFulfillmentGroup(fg);
                        fulfillmentGroupItem.setOrderItem(option.getOrderItem());
                        fulfillmentGroupItem.setQuantity(1);
                        fulfillmentGroupItem = fulfillmentGroupItemDao.save(fulfillmentGroupItem);
                        fg.getFulfillmentGroupItems().add(fulfillmentGroupItem);

                        if (item.getQuantity() - 1 <= 0) {
                            itemsToRemove.add(item);
                        } else {
                            item.setQuantity(item.getQuantity()-1);
                        }
                    }
                }
            }

            for (FulfillmentGroupItem item : itemsToRemove) {
                FulfillmentGroup fg = item.getFulfillmentGroup();
                fg.getFulfillmentGroupItems().remove(item);
                fulfillmentGroupItemDao.delete(item);
                if (fg.getFulfillmentGroupItems().size() == 0) {
                    order.getDelegate().getFulfillmentGroups().remove(fg);
                    fg.setOrder(null);
                    fulfillmentGroupService.delete(fg);
                    orderService.save(order.getDelegate(), false);
                }
            }
        }
    }

    @Override
    public void prepareCart(PromotableOrder promotableOrder) {
        try {
            Order order = promotableOrder.getDelegate();
            if (!CollectionUtils.isEmpty(order.getFulfillmentGroups())) {
                List<OrderMultishipOption> options = orderMultishipOptionService.findOrderMultishipOptions(order.getId());
                promotableOrder.setMultiShipOptions(options);
                promotableOrder.setHasMultiShipOptions(!CollectionUtils.isEmpty(options));
                //collapse to a single fg - we'll rebuild later
                fulfillmentGroupService.collapseToOneFulfillmentGroup(order, false);
                order.getFulfillmentGroups().get(0).setAddress(null);
                order.getFulfillmentGroups().get(0).setFulfillmentOption(null);
            }
        } catch (PricingException e) {
            throw new RuntimeException("Could not prepare the cart", e);
        }
    }

    @Override
    public void gatherCart(PromotableOrder promotableOrder) {
        Order order = promotableOrder.getDelegate();
        try {
            if (!CollectionUtils.isEmpty(order.getFulfillmentGroups())) {
                //stage 1 - gather possible split items - including those inside a bundle order item
                gatherFulfillmentGroupLinkedDiscreteOrderItems(order, promotableOrder.getMultiShipOptions());
                //stage 2 - gather the bundles themselves
                gatherFulfillmentGroupLinkedBundleOrderItems(order);
            } else {
                //stage 1 - gather possible split items - including those inside a bundle order item
                gatherOrderLinkedDiscreteOrderItems(order);
                //stage 2 - gather the bundles themselves
                gatherOrderLinkedBundleOrderItems(order);
            }

        } catch (PricingException e) {
            throw new RuntimeException("Could not gather the cart", e);
        }
        promotableOrder.resetDiscreteOrderItems();
    }

    @Override
    public void initializeBundleSplitItems(PromotableOrder order) {
        List<OrderItem> basicOrderItems = order.getDelegate().getOrderItems();
        for (OrderItem basicOrderItem : basicOrderItems) {
            if (basicOrderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleOrderItem = (BundleOrderItem) basicOrderItem;
                List<BundleOrderItem> searchHit = order.searchBundleSplitItems(bundleOrderItem);
                if (searchHit == null) {
                    searchHit = new ArrayList<BundleOrderItem>();
                    BundleOrderItemSplitContainer container = new BundleOrderItemSplitContainer();
                    container.setKey(bundleOrderItem);
                    container.setSplitItems(searchHit);
                    order.getBundleSplitItems().add(container);
                }
                BundleOrderItem temp = (BundleOrderItem) bundleOrderItem.clone();
                for (int x=0;x<temp.getDiscreteOrderItems().size();x++) {
                    temp.getDiscreteOrderItems().get(x).setId(bundleOrderItem.getDiscreteOrderItems().get(x).getId());
                }
                temp.setId(-1L);
                searchHit.add(temp);
            }
        }
    }

    @Override
    public void initializeSplitItems(PromotableOrder order) {
        List<PromotableOrderItem> items = order.getDiscountableDiscreteOrderItems();
        for (PromotableOrderItem item : items) {
            List<PromotableOrderItem> temp = new ArrayList<PromotableOrderItem>();
            temp.add(item);
            OrderItemSplitContainer container = new OrderItemSplitContainer();
            container.setKey(item.getDelegate());
            container.setSplitItems(temp);
            order.getSplitItems().add(container);
        }
    }

    protected void gatherOrderLinkedBundleOrderItems(Order order) throws PricingException {
        Map<String, BundleOrderItem> gatherBundle = new HashMap<String, BundleOrderItem>();
        List<BundleOrderItem> bundlesToRemove = new ArrayList<BundleOrderItem>();
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                String identifier = buildIdentifier(orderItem, null);
                BundleOrderItem retrieved = gatherBundle.get(identifier);
                if (retrieved == null) {
                    gatherBundle.put(identifier, (BundleOrderItem) orderItem);
                    continue;
                }
                retrieved.setQuantity(retrieved.getQuantity() + orderItem.getQuantity());
                bundlesToRemove.add((BundleOrderItem) orderItem);
            }
        }
        for (BundleOrderItem bundleOrderItem : gatherBundle.values()) {
            orderItemService.saveOrderItem(bundleOrderItem);
        }
        for (BundleOrderItem orderItem : bundlesToRemove) {
            try {
                orderService.removeItem(order.getId(), orderItem.getId(), false);
            } catch (RemoveFromCartException e) {
                throw new PricingException("Item could not be removed", e);
            }
        }
    }

    protected void gatherOrderLinkedDiscreteOrderItems(Order order) throws PricingException {
        List<DiscreteOrderItem> itemsToRemove = new ArrayList<DiscreteOrderItem>();
        Map<String, OrderItem> gatheredItem = new HashMap<String, OrderItem>();
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                for (DiscreteOrderItem discreteOrderItem : ((BundleOrderItem) orderItem).getDiscreteOrderItems()) {
                    gatherOrderLinkedDiscreteOrderItem(itemsToRemove, gatheredItem, discreteOrderItem, String.valueOf(orderItem.getId()));
                }
            } else {
                gatherOrderLinkedDiscreteOrderItem(itemsToRemove, gatheredItem, (DiscreteOrderItem) orderItem, null);
            }

        }
        for (OrderItem orderItem : gatheredItem.values()) {
            orderItemService.saveOrderItem(orderItem);
        }
        for (DiscreteOrderItem orderItem : itemsToRemove) {
            if (orderItem.getBundleOrderItem() == null) {
                try {
                    orderService.removeItem(order.getId(), orderItem.getId(), false);
                } catch (RemoveFromCartException e) {
                    throw new PricingException("Could not remove item", e);
                }
            } else {
                BundleOrderItem bundleOrderItem = orderItem.getBundleOrderItem();
                fulfillmentGroupService.removeOrderItemFromFullfillmentGroups(order, orderItem);
                bundleOrderItem.getDiscreteOrderItems().remove(orderItem);
                orderItem.setBundleOrderItem(null);
                orderItemService.saveOrderItem(bundleOrderItem);
            }
        }
    }

    protected void gatherFulfillmentGroupLinkedBundleOrderItems(Order order) throws PricingException {
        List<BundleOrderItem> bundlesToRemove = new ArrayList<BundleOrderItem>();
        Map<Long, Map<String, Object[]>> gatherBundle = new HashMap<Long, Map<String, Object[]>>();
        for (FulfillmentGroup group : order.getFulfillmentGroups()) {
            Map<String, Object[]> gatheredItem = gatherBundle.get(group.getId());
            if (gatheredItem == null) {
                gatheredItem = new HashMap<String, Object[]>();
                gatherBundle.put(group.getId(), gatheredItem);
            }
            for (FulfillmentGroupItem fgItem : group.getFulfillmentGroupItems()) {
                OrderItem orderItem = fgItem.getOrderItem();
                if (orderItem instanceof BundleOrderItem) {
                    String identifier = buildIdentifier(orderItem, null);
                    Object[] gatheredOrderItem = gatheredItem.get(identifier);
                    if (gatheredOrderItem == null) {
                        gatheredItem.put(identifier, new Object[]{orderItem, fgItem});
                        continue;
                    }
                    ((OrderItem) gatheredOrderItem[0]).setQuantity(((OrderItem) gatheredOrderItem[0]).getQuantity() + orderItem.getQuantity());
                    ((FulfillmentGroupItem) gatheredOrderItem[1]).setQuantity(((FulfillmentGroupItem) gatheredOrderItem[1]).getQuantity() + fgItem.getQuantity());
                    bundlesToRemove.add((BundleOrderItem) orderItem);
                }
            }
        }
        for (Map<String, Object[]> values : gatherBundle.values()) {
            for (Object[] item : values.values()) {
                orderItemService.saveOrderItem((OrderItem) item[0]);
                fulfillmentGroupItemDao.save((FulfillmentGroupItem) item[1]);
            }
        }
        for (BundleOrderItem orderItem : bundlesToRemove) {
            try {
                orderService.removeItem(order.getId(), orderItem.getId(), false);
            } catch (RemoveFromCartException e) {
                throw new PricingException("Item could not be removed", e);
            }
        }
    }

    protected void gatherFulfillmentGroupLinkedDiscreteOrderItems(Order order, List<OrderMultishipOption> options) throws PricingException {
        List<DiscreteOrderItem> itemsToRemove = new ArrayList<DiscreteOrderItem>();
        List<FulfillmentGroupItem> fgItemsToRemove = new ArrayList<FulfillmentGroupItem>();
        Map<Long, Map<String, Object[]>> gatherMap = new HashMap<Long, Map<String, Object[]>>();
        for (FulfillmentGroup group : order.getFulfillmentGroups()) {
            Map<String, Object[]> gatheredItem = gatherMap.get(group.getId());
            if (gatheredItem == null) {
                gatheredItem = new HashMap<String, Object[]>();
                gatherMap.put(group.getId(), gatheredItem);
            }
            for (FulfillmentGroupItem fgItem : group.getFulfillmentGroupItems()) {
                OrderItem orderItem = fgItem.getOrderItem();
                if (orderItem instanceof BundleOrderItem) {
                    for (DiscreteOrderItem discreteOrderItem : ((BundleOrderItem) orderItem).getDiscreteOrderItems()) {
                        if (CollectionUtils.isEmpty(orderItem.getOrderItemAdjustments())) {
                            gatherFulfillmentGroupLinkedDiscreteOrderItem(itemsToRemove, fgItemsToRemove, gatheredItem, fgItem, discreteOrderItem, String.valueOf(orderItem.getId()), options, true);
                        }
                    }
                } else if (CollectionUtils.isEmpty(orderItem.getOrderItemAdjustments())) {
                    gatherFulfillmentGroupLinkedDiscreteOrderItem(itemsToRemove, fgItemsToRemove, gatheredItem, fgItem, (DiscreteOrderItem) orderItem, null, options, true);
                }
            }
        }
        for (Map<String, Object[]> values : gatherMap.values()) {
            for (Object[] item : values.values()) {
                orderItemService.saveOrderItem((OrderItem) item[0]);
                fulfillmentGroupItemDao.save((FulfillmentGroupItem) item[1]);
            }
        }
        for (FulfillmentGroupItem fgItem : fgItemsToRemove) {
            FulfillmentGroup fg = fgItem.getFulfillmentGroup();
            fg.getFulfillmentGroupItems().remove(fgItem);
            fulfillmentGroupItemDao.delete(fgItem);
            if (fg.getFulfillmentGroupItems().isEmpty()) {
                order.getFulfillmentGroups().remove(fg);
                fg.setOrder(null);
            }
        }

        for (DiscreteOrderItem orderItem : itemsToRemove) {
            if (orderItem.getBundleOrderItem() == null) {
                orderItem.getOrder().getOrderItems().remove(orderItem);
                orderItem.setOrder(null);
            } else {
                BundleOrderItem bundleOrderItem = orderItem.getBundleOrderItem();
                bundleOrderItem.getDiscreteOrderItems().remove(orderItem);
                orderItem.setBundleOrderItem(null);
                orderItemService.saveOrderItem(bundleOrderItem);
            }
        }
    }

    protected void gatherOrderLinkedDiscreteOrderItem(List<DiscreteOrderItem> itemsToRemove, Map<String, OrderItem> gatheredItem, DiscreteOrderItem orderItem, String extraIdentifier) {
        if (CollectionUtils.isEmpty(orderItem.getOrderItemAdjustments())) {
            String identifier = buildIdentifier(orderItem, extraIdentifier);

            OrderItem gatheredOrderItem = gatheredItem.get(identifier);
            if (gatheredOrderItem == null) {
                gatheredItem.put(identifier, orderItem);
                return;
            }
            gatheredOrderItem.setQuantity(gatheredOrderItem.getQuantity() + orderItem.getQuantity());
            itemsToRemove.add(orderItem);
        }
    }

    protected void gatherFulfillmentGroupLinkedDiscreteOrderItem(List<DiscreteOrderItem> itemsToRemove, List<FulfillmentGroupItem> fgItemsToRemove, Map<String, Object[]> gatheredItem, FulfillmentGroupItem fgItem, DiscreteOrderItem orderItem, String extraIdentifier, List<OrderMultishipOption> options, boolean includePrice) {
        String identifier = buildIdentifier(orderItem, extraIdentifier, includePrice);
        Object[] gatheredOrderItem = gatheredItem.get(identifier);
        if (gatheredOrderItem == null) {
            gatheredItem.put(identifier, new Object[]{orderItem, fgItem});
            return;
        }
        for (OrderMultishipOption option : options) {
            if (option.getOrderItem().getId().equals(orderItem.getId())) {
                option.setOrderItem((OrderItem) gatheredOrderItem[0]);
                orderMultishipOptionService.save(option);
            }
        }
        ((FulfillmentGroupItem) gatheredOrderItem[1]).setQuantity(((FulfillmentGroupItem) gatheredOrderItem[1]).getQuantity() + fgItem.getQuantity());
        if (!((OrderItem) gatheredOrderItem[0]).getId().equals(orderItem.getId())) {
            ((OrderItem) gatheredOrderItem[0]).setQuantity(((OrderItem) gatheredOrderItem[0]).getQuantity() + fgItem.getQuantity());
            itemsToRemove.add(orderItem);
        }
        fgItemsToRemove.add(fgItem);
    }

    /**
     * Returns null if the item is not part of a bundle.
     * @return
     */
    private Long getBundleId(OrderItem item) {
        if (item instanceof DiscreteOrderItem) {
            DiscreteOrderItem discreteItem =  (DiscreteOrderItem) item;
            if (discreteItem.getBundleOrderItem() != null) {
                return discreteItem.getBundleOrderItem().getId();
            }
        }
        return null;
    }

    protected void mergeSplitDiscreteOrderItems(PromotableOrder order) throws PricingException {
        if (CollectionUtils.isEmpty(order.getDelegate().getFulfillmentGroups())) {
            FulfillmentGroup fg = fulfillmentGroupService.createEmptyFulfillmentGroup();
            fg.setOrder(order.getDelegate());
            order.getDelegate().getFulfillmentGroups().add(fg);
        }
        order.getDelegate().getFulfillmentGroups().get(0).setAddress(null);
        order.getDelegate().getFulfillmentGroups().get(0).setFulfillmentOption(null);

        //If adjustments are removed - merge split items back together before adding to the cart
        List<PromotableOrderItem> itemsToRemove = new ArrayList<PromotableOrderItem>();
        List<DiscreteOrderItem> delegatesToRemove = new ArrayList<DiscreteOrderItem>();
        Iterator<PromotableOrderItem> finalItems = order.getDiscountableDiscreteOrderItems().iterator();
        Map<String, PromotableOrderItem> allItems = new HashMap<String, PromotableOrderItem>();
        while (finalItems.hasNext()) {
            PromotableOrderItem nextItem = finalItems.next();
            List<PromotableOrderItem> mySplits = order.searchSplitItems(nextItem);
            if (!CollectionUtils.isEmpty(mySplits)) {
                PromotableOrderItem cloneItem = nextItem.clone();
                cloneItem.clearAllDiscount();
                cloneItem.clearAllQualifiers();
                cloneItem.removeAllAdjustments();
                cloneItem.setQuantity(0);
                Iterator<PromotableOrderItem> splitItemIterator = mySplits.iterator();
                while (splitItemIterator.hasNext()) {
                    PromotableOrderItem splitItem = splitItemIterator.next();
                    if (!splitItem.isHasOrderItemAdjustments()) {
                        cloneItem.setQuantity(cloneItem.getQuantity() + splitItem.getQuantity());
                        splitItemIterator.remove();
                    }
                }
                if (cloneItem.getQuantity() > 0) {
                    String identifier = String.valueOf(cloneItem.getSku().getId());
                    Long bundleItemId = getBundleId(cloneItem.getDelegate());
                    if (bundleItemId != null) {
                        identifier += bundleItemId;
                    }
                    if (allItems.containsKey(identifier)) {
                        PromotableOrderItem savedItem = allItems.get(identifier);
                        savedItem.setQuantity(savedItem.getQuantity() + cloneItem.getQuantity());
                    } else {
                        allItems.put(identifier, cloneItem);
                        mySplits.add(cloneItem);
                    }
                }

                if (nextItem.getDelegate().getBundleOrderItem() == null) {
                    if (mySplits.contains(nextItem)) {
                        mySplits.remove(nextItem);
                    } else {
                        itemsToRemove.add(nextItem);
                        delegatesToRemove.add(nextItem.getDelegate());
                    }
                } else {
                    itemsToRemove.add(nextItem);
                    delegatesToRemove.add(nextItem.getDelegate());
                }
            }
        }

        for (OrderItemSplitContainer key : order.getSplitItems()) {
            List<PromotableOrderItem> mySplits = key.getSplitItems();
            if (!CollectionUtils.isEmpty(mySplits)) {
                Iterator<FulfillmentGroupItem> fgItems = order.getDelegate().getFulfillmentGroups().get(0).getFulfillmentGroupItems().iterator();
                while(fgItems.hasNext()) {
                    FulfillmentGroupItem fgItem = fgItems.next();
                    if (fgItem.getOrderItem().equals(key.getKey())) {
                        fulfillmentGroupItemDao.delete(fgItem);
                        fgItems.remove();
                    }
                }
                for (PromotableOrderItem myItem : mySplits) {
                    myItem.assignFinalPrice();
                    DiscreteOrderItem delegateItem = myItem.getDelegate();
                    if (delegateItem.getBundleOrderItem() == null) {
                        delegateItem = (DiscreteOrderItem) addOrderItemToOrder(order.getDelegate(), delegateItem, false);
                        for (int j=0;j<delegateItem.getQuantity();j++){
                            Iterator<OrderMultishipOption> itr = new ArrayList<OrderMultishipOption>(order.getMultiShipOptions()).iterator();
                            while(itr.hasNext()) {
                                OrderMultishipOption option = itr.next();
                                if ((option.getOrderItem() instanceof DiscreteOrderItem) && option.getOrderItem().equals(key.getKey())) {
                                    option.setOrderItem(delegateItem);
                                    orderMultishipOptionService.save(option);
                                    itr.remove();
                                    break;
                                }
                            }
                        }
                        FulfillmentGroupItem fgItem = fulfillmentGroupItemDao.create();
                        fgItem.setQuantity(delegateItem.getQuantity());
                        fgItem.setOrderItem(delegateItem);
                        fgItem.setFulfillmentGroup(order.getDelegate().getFulfillmentGroups().get(0));
                        fgItem = fulfillmentGroupItemDao.save(fgItem);
                        order.getDelegate().getFulfillmentGroups().get(0).getFulfillmentGroupItems().add(fgItem);
                    }
                    myItem.setDelegate(delegateItem);
                }
            }
        }

        //compile a list of any gift wrap items that we're keeping
        List<GiftWrapOrderItem> giftWrapItems = new ArrayList<GiftWrapOrderItem>();
        for (DiscreteOrderItem discreteOrderItem : order.getDelegate().getDiscreteOrderItems()) {
            if (discreteOrderItem instanceof GiftWrapOrderItem) {
                if (!delegatesToRemove.contains(discreteOrderItem)) {
                    giftWrapItems.add((GiftWrapOrderItem) discreteOrderItem);
                } else {
                    Iterator<OrderItem> wrappedItems = ((GiftWrapOrderItem) discreteOrderItem).getWrappedItems().iterator();
                    while (wrappedItems.hasNext()) {
                        OrderItem wrappedItem = wrappedItems.next();
                        wrappedItem.setGiftWrapOrderItem(null);
                        wrappedItems.remove();
                    }
                }
            }
        }

        for (PromotableOrderItem itemToRemove : itemsToRemove) {
            DiscreteOrderItem delegateItem = itemToRemove.getDelegate();

            mergeSplitGiftWrapOrderItems(order, giftWrapItems, itemToRemove, delegateItem);

            if (delegateItem.getBundleOrderItem() == null) {
                Iterator<OrderItem> orderItems = order.getDelegate().getOrderItems().iterator();
                while (orderItems.hasNext()) {
                    OrderItem orderItem = orderItems.next();
                    if (orderItem.getId().equals(itemToRemove.getDelegate().getId())) {
                        orderItem.setOrder(null);
                        orderItems.remove();
                    }
                }
            }
        }
    }

    protected OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem, Boolean priceOrder) throws PricingException {
        List<OrderItem> orderItems = order.getOrderItems();
        newOrderItem.setOrder(order);
        newOrderItem = orderItemService.saveOrderItem(newOrderItem);
        orderItems.add(newOrderItem);
        orderService.save(order, priceOrder);
        return newOrderItem;
    }

    protected void mergeSplitGiftWrapOrderItems(PromotableOrder order, List<GiftWrapOrderItem> giftWrapItems, PromotableOrderItem itemToRemove, DiscreteOrderItem delegateItem) {
        for (GiftWrapOrderItem giftWrapOrderItem : giftWrapItems) {
            List<OrderItem> newItems = new ArrayList<OrderItem>();
            Iterator<OrderItem> wrappedItems = giftWrapOrderItem.getWrappedItems().iterator();
            boolean foundItems = false;
            while (wrappedItems.hasNext()) {
                OrderItem wrappedItem = wrappedItems.next();
                if (wrappedItem.equals(delegateItem)) {
                    foundItems = true;
                    //add in the new wrapped items (split or not)
                    List<PromotableOrderItem> searchHits = order.searchSplitItems(itemToRemove);
                    if (!CollectionUtils.isEmpty(searchHits)) {
                        for (PromotableOrderItem searchHit : searchHits) {
                            newItems.add(searchHit.getDelegate());
                            searchHit.getDelegate().setGiftWrapOrderItem(giftWrapOrderItem);
                        }
                    }
                    //eradicate the old wrapped items
                    delegateItem.setGiftWrapOrderItem(null);
                    wrappedItems.remove();
                }
            }
            if (foundItems) {
                giftWrapOrderItem.getWrappedItems().addAll(newItems);
                orderItemService.saveOrderItem(giftWrapOrderItem);
            }
        }
    }

    protected void mergeSplitBundleOrderItems(PromotableOrder order) throws PricingException {
        List<BundleOrderItemSplitContainer> bundleContainers = order.getBundleSplitItems();
        for (BundleOrderItemSplitContainer bundleOrderItemSplitContainer : bundleContainers) {
            if (bundleOrderItemSplitContainer.getKey().shouldSumItems()) {
                BundleOrderItem val = bundleOrderItemSplitContainer.getSplitItems().get(0);
                val.getDiscreteOrderItems().clear();
                val.setId(null);
                List<DiscreteOrderItem> itemsToAdd = new ArrayList<DiscreteOrderItem>();
                for (DiscreteOrderItem discreteOrderItem : bundleOrderItemSplitContainer.getKey().getDiscreteOrderItems()) {
                    for (FulfillmentGroup fg : order.getDelegate().getFulfillmentGroups()) {
                        Iterator<FulfillmentGroupItem> fgItems = fg.getFulfillmentGroupItems().iterator();
                        while (fgItems.hasNext()) {
                            FulfillmentGroupItem fgItem = fgItems.next();
                            if (fgItem.getOrderItem().getId() == discreteOrderItem.getId()) {
                                fgItem.setFulfillmentGroup(null);
                                fgItems.remove();
                            }
                        }
                    }
                    PromotableOrderItem poi = new PromotableOrderItemImpl(discreteOrderItem, null, null);
                    List<PromotableOrderItem> items = order.searchSplitItems(poi);
                    for (PromotableOrderItem temp : items) {
                        DiscreteOrderItem delegate = temp.getDelegate();
                        delegate.setId(null);
                        delegate.setBundleOrderItem(null);
                        delegate = (DiscreteOrderItem) orderItemService.saveOrderItem(delegate);
                        delegate.setBundleOrderItem(val);
                        itemsToAdd.add(delegate);

                        for (int j=0;j<delegate.getQuantity();j++){
                            Iterator<OrderMultishipOption> itr = new ArrayList<OrderMultishipOption>(order.getMultiShipOptions()).iterator();
                            while(itr.hasNext()) {
                                OrderMultishipOption option = itr.next();
                                if ((option.getOrderItem() instanceof DiscreteOrderItem) && option.getOrderItem().equals(discreteOrderItem)) {
                                    option.setOrderItem(delegate);
                                    orderMultishipOptionService.save(option);
                                    itr.remove();
                                    break;
                                }
                            }
                        }
                        FulfillmentGroupItem fgItem = fulfillmentGroupItemDao.create();
                        fgItem.setQuantity(delegate.getQuantity());
                        fgItem.setOrderItem(delegate);
                        fgItem.setFulfillmentGroup(order.getDelegate().getFulfillmentGroups().get(0));
                        fgItem = fulfillmentGroupItemDao.save(fgItem);
                        order.getDelegate().getFulfillmentGroups().get(0).getFulfillmentGroupItems().add(fgItem);
                    }
                }
                val.getDiscreteOrderItems().addAll(itemsToAdd);

                order.getDelegate().getOrderItems().remove(bundleOrderItemSplitContainer.getKey());
                bundleOrderItemSplitContainer.getKey().setOrder(null);

                order.getDelegate().getFulfillmentGroups().get(0).setAddress(null);
                order.getDelegate().getFulfillmentGroups().get(0).setFulfillmentOption(null);

                val = (BundleOrderItem) addOrderItemToOrder(order.getDelegate(), val, false);
            }
        }

        orderService.save(order.getDelegate(), false);
    }

    @Override
    public String buildIdentifier(OrderItem orderItem, String extraIdentifier) {
        return buildIdentifier(orderItem, extraIdentifier, true);
    }

    public String buildIdentifier(OrderItem orderItem, String extraIdentifier, boolean includePrice) {
        StringBuffer identifier = new StringBuffer();
        if (orderItem.getSplitParentItemId() != null || orderService.getAutomaticallyMergeLikeItems()) {
            if (!orderService.getAutomaticallyMergeLikeItems()) {
                identifier.append(orderItem.getSplitParentItemId());
            } else {
                if (orderItem instanceof BundleOrderItem) {
                    BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                    if (bundleOrderItem.getSku() != null) {
                        identifier.append(bundleOrderItem.getSku().getId());
                    } else {
                        if (orderItem.getSplitParentItemId() != null) {
                            identifier.append(orderItem.getSplitParentItemId());
                        } else {
                            identifier.append(orderItem.getId());
                        }
                    }
                } else if (orderItem instanceof DiscreteOrderItem) {
                    DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
                    identifier.append(discreteOrderItem.getSku().getId());
                    identifier.append('_').append(discreteOrderItem.getBundleOrderItem());
                } else {
                    if (orderItem.getSplitParentItemId() != null) {
                        identifier.append(orderItem.getSplitParentItemId());
                    } else {
                        identifier.append(orderItem.getId());
                    }
                }
            }

            if (includePrice) {
                identifier.append('_').append(orderItem.getPrice().stringValue());
            }
            if (extraIdentifier != null) {
                identifier.append('_').append(extraIdentifier);
            }

            addOptionAttributesToIdentifier(identifier, orderItem);
        } else {
            identifier.append(orderItem.getId());
        }
        return identifier.toString();
    }

    /**
     * Appends the item attributes so that items with different attibutes are not merged together
     * as part of the merge/split logic.
     *
     * @param identifier
     * @param orderItem
     */
    protected void addOptionAttributesToIdentifier(StringBuffer identifier, OrderItem orderItem) {
        if (orderItem.getOrderItemAttributes() != null && orderItem.getOrderItemAttributes().size() > 0) {
            List<String> valueList = new ArrayList<String>();
            for (OrderItemAttribute itemAttribute : orderItem.getOrderItemAttributes().values()) {
                valueList.add(itemAttribute.getName() + "_" + itemAttribute.getValue());
            }
            Collections.sort(valueList);
            identifier.append('_');
            for (String value : valueList) {
                identifier.append(value);
            }
        }
    }
}
