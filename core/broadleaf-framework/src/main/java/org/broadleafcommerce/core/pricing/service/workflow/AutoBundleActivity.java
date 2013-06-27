/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.BaseActivity;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This pricing workflow step will automatically bundle items in the cart.
 *
 * For example, if a ProductBundle exists of two items and the user has
 * one of the items in their cart.   If they then add the second item,
 * this activity will replace the two items with the ProductBundle.
 *
 * This only occurs if the ProductBundle is set to "automatically" bundle.
 *
 */
public class AutoBundleActivity extends BaseActivity<PricingContext> {
    @Resource(name="blCatalogService")
    protected CatalogService catalogService;

    @Resource(name="blOrderService")
    protected OrderService orderService;

    @Resource(name="blOrderItemDao")
    protected OrderItemDao orderItemDao;

    @Resource(name="blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fulfillmentGroupItemDao;

    public PricingContext execute(PricingContext context) throws Exception {
        Order order = context.getSeedData();
        order = handleAutomaticBundling(order);
        context.setSeedData(order);
        return context;
    }

    public Order handleAutomaticBundling(Order order) throws PricingException, RemoveFromCartException {
        boolean itemsHaveBeenUnbundled = false;
        List<DiscreteOrderItem> unbundledItems = null;

        List<ProductBundle> productBundles = catalogService.findAutomaticProductBundles();
        Set<Long> processedBundleIds = new HashSet<Long>();
        for (ProductBundle productBundle : productBundles) {
            int existingUses = countExistingUsesOfBundle(order, productBundle);
            Integer maxApplications = null;
            for (SkuBundleItem skuBundleItem : productBundle.getSkuBundleItems()) {
                int maxSkuApplications = countMaximumApplications(order, skuBundleItem, processedBundleIds);
                if (maxApplications == null || maxApplications > maxSkuApplications) {
                    maxApplications = maxSkuApplications;
                }
            }
            processedBundleIds.add(productBundle.getId());

            if (maxApplications != existingUses) {
                if (! itemsHaveBeenUnbundled) {
                    // Store the discrete items that were part of automatic bundles
                    unbundledItems = unBundleItems(order);
                    order = removeAutomaticBundles(order);
                    itemsHaveBeenUnbundled = true;
                }

                // Create a new bundle with maxApplication occurrences
                order = bundleItems(order, productBundle, maxApplications, unbundledItems);
            }
        }
        return order;
    }

    /**
     * Removes all automatic bundles from the order and replaces with DiscreteOrderItems.
     *
     * @param order
     * @throws PricingException 
     */
    private Order removeAutomaticBundles(Order order) throws PricingException {
        List<BundleOrderItem> bundlesToRemove = new ArrayList<BundleOrderItem>();

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                if (bundleOrderItem.getProductBundle() != null && bundleOrderItem.getProductBundle().getAutoBundle()) {
                    bundlesToRemove.add(bundleOrderItem);
                }
            }
        }

        for (BundleOrderItem bundleOrderItem : bundlesToRemove) {
            try {
                order = orderService.removeItem(order.getId(), bundleOrderItem.getId(), false);
            } catch (RemoveFromCartException e) {
                throw new PricingException("Could not remove item", e);
            }
        }

        return order;
    }

    /**
     * Removes all automatic bundles from the order and replaces with DiscreteOrderItems.
     *
     * @param order
     */
    private List<DiscreteOrderItem> unBundleItems(Order order) throws PricingException{
        List<DiscreteOrderItem> unbundledItems = null;

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                if (bundleOrderItem.getProductBundle() != null && bundleOrderItem.getProductBundle().getAutoBundle()) {
                    if (unbundledItems == null) {
                        unbundledItems = new ArrayList<DiscreteOrderItem>();
                    }

                    for(DiscreteOrderItem item : bundleOrderItem.getDiscreteOrderItems()) {
                        DiscreteOrderItem newOrderItem = (DiscreteOrderItem) item.clone();
                        newOrderItem.setQuantity(item.getQuantity() * bundleOrderItem.getQuantity());
                        newOrderItem.setSkuBundleItem(null);
                        newOrderItem.setBundleOrderItem(null);   
                        newOrderItem.updateSaleAndRetailPrices();
                        newOrderItem.setOrder(order);
                        unbundledItems.add(newOrderItem);
                    }
                }
            }
        }
        return unbundledItems;
    }

    /**
     * Builds a BundleOrderItem based on the passed in productBundle.    Creates new DiscreteOrderItems.
     * Removes the existing matching DiscreteOrderItems or modifies the quantity if needed.
     *
     * @param order
     * @param productBundle
     * @param numApplications
     * @throws PricingException 
     * @throws ItemNotFoundException 
     */
    private Order bundleItems(Order order, ProductBundle productBundle, Integer numApplications, List<DiscreteOrderItem> unbundledItems) throws PricingException, RemoveFromCartException {

        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemDao.create(OrderItemType.BUNDLE);
        bundleOrderItem.setQuantity(numApplications);
        bundleOrderItem.setCategory(productBundle.getDefaultCategory());
        bundleOrderItem.setSku(productBundle.getDefaultSku());
        bundleOrderItem.setName(productBundle.getName());
        bundleOrderItem.setProductBundle(productBundle);
        bundleOrderItem.setOrder(order);

        // make a copy of the fulfillment group items since they will be deleted when the order items are removed
        Map<Long, FulfillmentGroupItem> skuIdFulfillmentGroupMap = new HashMap<Long, FulfillmentGroupItem>();
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                if (fulfillmentGroupItem.getOrderItem() instanceof DiscreteOrderItem) {
                    DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) fulfillmentGroupItem.getOrderItem();
                    skuIdFulfillmentGroupMap.put(discreteOrderItem.getSku().getId(), fulfillmentGroupItem);
                }
            }
        }

        for (SkuBundleItem skuBundleItem : productBundle.getSkuBundleItems()) {
            List<DiscreteOrderItem> itemMatches = new ArrayList<DiscreteOrderItem>();
            int skuMatches = populateItemMatchesForSku(itemMatches, order, unbundledItems, skuBundleItem.getSku().getId());
            int skusRequired = skuBundleItem.getQuantity()* numApplications;

            if (skuMatches < skusRequired) {
                throw new IllegalArgumentException("Something went wrong creating automatic bundles.  Not enough skus to fulfill bundle requirements for sku id: " + skuBundleItem.getSku().getId());
            }

            // remove-all-items from order
            // this call also deletes any fulfillment group items that are associated with that order item
            for (DiscreteOrderItem item : itemMatches) {
                order = orderService.removeItem(order.getId(), item.getId(), false);
            }

            DiscreteOrderItem baseItem = null;
            if (itemMatches.size() > 0) {
                baseItem = itemMatches.get(0);
            } else {
                 for (DiscreteOrderItem discreteOrderItem : unbundledItems) {
                     if (discreteOrderItem.getSku().getId().equals(skuBundleItem.getSku().getId())) {
                         baseItem = discreteOrderItem;
                     }
                 }
            }

            // Add item to the skuBundle
            DiscreteOrderItem newSkuBundleItem = (DiscreteOrderItem) baseItem.clone();
            newSkuBundleItem.setSkuBundleItem(skuBundleItem);
            newSkuBundleItem.setBundleOrderItem(bundleOrderItem);
            newSkuBundleItem.setQuantity(skuBundleItem.getQuantity());
            newSkuBundleItem.setOrder(null);

            bundleOrderItem.getDiscreteOrderItems().add(newSkuBundleItem);

            if (skuMatches > skusRequired) {
                // Add a non-bundled item to the order with the remaining sku count.
                DiscreteOrderItem newOrderItem = (DiscreteOrderItem) baseItem.clone();
                newOrderItem.setBundleOrderItem(null);
                newOrderItem.setSkuBundleItem(null);
                newOrderItem.setQuantity(skuMatches - skusRequired);
                newOrderItem = (DiscreteOrderItem) orderItemDao.save(newOrderItem);
                newOrderItem.setOrder(order);
                newOrderItem.updateSaleAndRetailPrices();

                // Re-associate fulfillment group item to newOrderItem
                FulfillmentGroupItem fulfillmentGroupItem = skuIdFulfillmentGroupMap.get(newSkuBundleItem.getSku().getId());
                if (fulfillmentGroupItem != null) {
                    FulfillmentGroupItem newFulfillmentGroupItem = fulfillmentGroupItem.clone();
                    newFulfillmentGroupItem.setOrderItem(newOrderItem);
                    newFulfillmentGroupItem.setQuantity(newOrderItem.getQuantity());
                    newFulfillmentGroupItem = fulfillmentGroupItemDao.save(newFulfillmentGroupItem);

                    //In case this activity is run inside a transaction, we need to set the relationships on the order directly
                    //these associations may have not been committed yet. This order is used in other activities and will not be reloaded if in a transaction.
                    for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                        if (newFulfillmentGroupItem.getFulfillmentGroup() != null && fg.getId().equals(newFulfillmentGroupItem.getFulfillmentGroup().getId())) {
                            fg.addFulfillmentGroupItem(newFulfillmentGroupItem);
                        }
                    }
                }

                order.getOrderItems().add(newOrderItem);
            }
        }

        bundleOrderItem.updateSaleAndRetailPrices();

        order.getOrderItems().add(bundleOrderItem);
        order =  orderService.save(order, false);

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleItem = (BundleOrderItem) orderItem;
                for (DiscreteOrderItem discreteOrderItem : bundleItem.getDiscreteOrderItems()) {
                    // Re-associate fulfillment group item to newly created skuBundles
                    FulfillmentGroupItem fulfillmentGroupItem = skuIdFulfillmentGroupMap.get(discreteOrderItem.getSku().getId());
                    if (fulfillmentGroupItem != null) {
                        FulfillmentGroupItem newFulfillmentGroupItem = fulfillmentGroupItem.clone();
                        newFulfillmentGroupItem.setOrderItem(discreteOrderItem);
                        newFulfillmentGroupItem.setQuantity(discreteOrderItem.getQuantity());

                        //In case this activity is run inside a transaction, we need to set the relationships on the order directly
                        //these associations may have not been committed yet. This order is used in other activities and will not be reloaded if in a transaction.
                        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                            if (newFulfillmentGroupItem.getFulfillmentGroup() != null && fg.getId().equals(newFulfillmentGroupItem.getFulfillmentGroup().getId())) {
                                fg.addFulfillmentGroupItem(newFulfillmentGroupItem);
                            }
                        }
                    }
                }
            }
        }

        //reload order with new fulfillment group items
        order = orderService.save(order, false);
        return order;
    }


    protected int countExistingUsesOfBundle(Order order, ProductBundle bundle) {
        int existingUses=0;
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
                if (bundleOrderItem.getProductBundle() != null) {
                    if (bundleOrderItem.getProductBundle().getId().equals(bundle.getId())) {
                        existingUses = existingUses+1;
                    }
                }
            }
        }
        return existingUses;
    }

    protected int populateItemMatchesForSku(List<DiscreteOrderItem> matchingItems, Order order, List<DiscreteOrderItem> unbundledItems, Long skuId) {
        int skuMatches = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof DiscreteOrderItem) {
                DiscreteOrderItem item = (DiscreteOrderItem) orderItem;
                if (skuId.equals(item.getSku().getId())) {
                    matchingItems.add(item);
                    skuMatches = skuMatches + item.getQuantity();
                }
            }
        }

        if (unbundledItems != null) {
            for (DiscreteOrderItem discreteOrderItem : unbundledItems) {
                if (skuId.equals(discreteOrderItem.getSku().getId())) {
                    skuMatches = skuMatches + discreteOrderItem.getQuantity();
                }
            }
        }
        return skuMatches;
    }

    protected int countMaximumApplications(Order order, SkuBundleItem skuBundleItem, Set<Long> processedBundles) {
        int skuMatches = 0;
        Long skuId = skuBundleItem.getSku().getId();
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof DiscreteOrderItem) {
                DiscreteOrderItem item = (DiscreteOrderItem) orderItem;
                if (skuId.equals(item.getSku().getId())) {
                    skuMatches = skuMatches + item.getQuantity();
                }
            } else if (orderItem instanceof BundleOrderItem) {

                BundleOrderItem bundleItem = (BundleOrderItem) orderItem;
                if (bundleItem.getProductBundle() != null && bundleItem.getProductBundle().getAutoBundle()) {
                    if (! processedBundles.contains(bundleItem.getId())) {
                        for(DiscreteOrderItem discreteItem : bundleItem.getDiscreteOrderItems()) {
                            if (skuId.equals(discreteItem.getSku().getId())) {
                                skuMatches = skuMatches + (discreteItem.getQuantity() * bundleItem.getQuantity());
                            }
                        }
                    }
                }
            }
        }

        return skuMatches / skuBundleItem.getQuantity();
    }

}
