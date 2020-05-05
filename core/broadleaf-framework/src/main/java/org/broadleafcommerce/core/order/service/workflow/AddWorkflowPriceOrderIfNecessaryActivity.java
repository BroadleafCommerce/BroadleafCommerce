/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service.workflow;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

/**
 * As of Broadleaf version 3.1.0, saves of individual aspects of an Order (such as OrderItems and FulfillmentGroupItems) no
 * longer happen in their respective activities. Instead, we will now handle these saves in this activity exclusively.
 *
 * This provides the ability for an implementation to not require a transactional wrapper around the entire workflow and
 * instead only requires it around this particular activity. This is only recommended if there are long running steps in
 * the workflow, such as an external service call to check availability.
 *
 * @author Andre Azzolini (apazzolini)
 */
@Component("blAddWorkflowPriceOrderIfNecessaryActivity")
public class AddWorkflowPriceOrderIfNecessaryActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {

    public static final int ORDER = 5000;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fgItemDao;

    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    public AddWorkflowPriceOrderIfNecessaryActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        Order order = request.getOrder();

        // If the UpdateOrderMultishipOptionActivity identified that we should delete order item multiship options,
        // go ahead and carry out that delete here.
        if (CollectionUtils.isNotEmpty(request.getMultishipOptionsToDelete())) {
            for (Long[] pack : request.getMultishipOptionsToDelete()) {
                if (pack[1] == null) {
                    orderMultishipOptionService.deleteOrderItemOrderMultishipOptions(pack[0]);
                } else {
                    orderMultishipOptionService.deleteOrderItemOrderMultishipOptions(pack[0], pack[1].intValue());
                }
            }
        }

        // We potentially have some FulfillmentGroupItems that were identified in the FulfillmentGroupItemStrategy as
        // ones that should be deleted. Delete them here.
        if (CollectionUtils.isNotEmpty(request.getFgisToDelete())) {
            for (FulfillmentGroupItem fgi : request.getFgisToDelete()) {
                for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                    ListIterator<FulfillmentGroupItem> fgItemIter = fg.getFulfillmentGroupItems().listIterator();
                    while (fgItemIter.hasNext()) {
                        FulfillmentGroupItem fgi2 = fgItemIter.next();
                        if (fgi2 == fgi) {
                            fgItemIter.remove();
                            fgItemDao.delete(fgi2);
                        }
                    }
                }
            }
        }

        // We now need to delete any OrderItems that were marked as such, including their children, if any
        for (OrderItem oi : request.getOisToDelete()) {
            order.getOrderItems().remove(oi);

            if (oi.getParentOrderItem() != null) {
                OrderItem parentItem = oi.getParentOrderItem();
                parentItem.getChildOrderItems().remove(oi);
            }

            orderItemService.delete(oi);
        }

        // We need to build up a map of OrderItem to which FulfillmentGroupItems reference that particular OrderItem.
        // We'll also save the order item and build up a map of the unsaved items to their saved counterparts.
        Map<OrderItem, List<FulfillmentGroupItem>> oiFgiMap = new HashMap<>();
        Map<OrderItem, OrderItem> savedOrderItems = new HashMap<>();
        for (OrderItem oi : order.getOrderItems()) {
                getOiFgiMap(order, oiFgiMap, oi);
                savedOrderItems.put(oi, orderItemService.saveOrderItem(oi));
        }

        // Now, we'll update the orderitems in the order to their saved counterparts
        ListIterator<OrderItem> li = order.getOrderItems().listIterator();
        List<OrderItem> oisToAdd = new ArrayList<>();
        while (li.hasNext()) {
            OrderItem oi = li.next();
            OrderItem savedOi = savedOrderItems.get(oi);
            oisToAdd.add(savedOi);
            li.remove();
        }
        order.getOrderItems().addAll(oisToAdd);

        for (Entry<OrderItem, List<FulfillmentGroupItem>> entry : oiFgiMap.entrySet()) {
            // Update any FulfillmentGroupItems that reference order items
            for (FulfillmentGroupItem fgi : entry.getValue()) {
                fgi.setOrderItem(savedOrderItems.get(entry.getKey()));
            }

            // We also need to update the orderItem on the request in case it's used by the caller of this workflow
            if (entry.getKey() == request.getOrderItem()) {
                request.setOrderItem(savedOrderItems.get(entry.getKey()));
            }
        }

        // We need to add the new item to the parent's child order items as well.
        updateChildOrderItem(request, order);

        // If a custom implementation needs to handle additional saves before the parent Order is saved, this method
        // can be overridden to provide that functionality.
        preSaveOperation(request);

        // Now that our collection items in our Order have been saved and the state of our Order is in a place where we
        // won't get a transient save exception, we are able to go ahead and save the order with optional pricing.
        order = orderService.save(order, request.isPriceOrder());
        request.setOrder(order);

        return context;
    }

    /**
     * Traverses the current OrderItem for a match to the parentOrderItemId.
     * If found, populates and returns true.
     *
     * @param request
     * @param orderItem
     * @return
     */
    protected void updateChildOrderItem(CartOperationRequest request, Order order) {
        boolean updated = false;
        for (OrderItem oi : order.getOrderItems()) {
            if (oi instanceof BundleOrderItem) {
                BundleOrderItem boi = (BundleOrderItem) oi;
                updated = checkAndUpdateChildren(request, boi); //check the bundle children
                if (!updated) {
                    for (OrderItem discreteOrderItem : boi.getDiscreteOrderItems()) { //check the bundle discrete items
                        if (checkAndUpdateChildren(request, discreteOrderItem)) {
                            updated = true;
                            break; //break out of the discrete items loop
                        }
                    }
                }
            } else {
                updated = checkAndUpdateChildren(request, oi);
            }
            if (updated) {
                break;
            }
        }
    }

    protected boolean checkAndUpdateChildren(CartOperationRequest request, OrderItem orderItem) {
        boolean parentUpdated = false;
        if (orderItem.getId().equals(request.getItemRequest().getParentOrderItemId())) {
            orderItem.getChildOrderItems().add(request.getOrderItem());
            parentUpdated = true;
        } else {
            if (CollectionUtils.isNotEmpty(orderItem.getChildOrderItems())) {
                for (OrderItem childOrderItem : orderItem.getChildOrderItems()) {
                    parentUpdated = checkAndUpdateChildren(request, childOrderItem);
                    if (parentUpdated) {
                        break;
                    }
                }
            }
        }
        return parentUpdated;
    }

    protected void getOiFgiMap(Order order, Map<OrderItem, List<FulfillmentGroupItem>> oiFgiMap, OrderItem oi) {
        List<FulfillmentGroupItem> fgis = new ArrayList<>();

        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgi : fg.getFulfillmentGroupItems()) {
                if (fgi.getOrderItem().equals(oi)) {
                    fgis.add(fgi);
                }
            }
        }

        oiFgiMap.put(oi, fgis);
    }

    /**
     * Intended to be overridden by a custom implementation if there is a requirement to perform additional logic or
     * saves before triggering the main Order save with pricing.
     *
     * @param request
     */
    protected void preSaveOperation(CartOperationRequest request) {
        // Broadleaf implementation does nothing here
    }

}
