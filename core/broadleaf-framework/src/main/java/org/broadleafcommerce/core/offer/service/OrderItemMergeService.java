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

import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactory;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

/**
 * @author Jeff Fischer
 */
public interface OrderItemMergeService {

    public void gatherSplitItemsInBundles(Order order) throws PricingException;

    public void mergeSplitItems(final PromotableOrder order);

    public void finalizeCart(PromotableOrder order) throws PricingException;

    public void prepareCart(PromotableOrder promotableOrder);

    public void gatherCart(PromotableOrder promotableOrder);

    public void initializeBundleSplitItems(PromotableOrder order);

    public void initializeSplitItems(PromotableOrder order);

    /**
     * Returns a key that determines whether or not two items can be merged together.
     * If the keys match, the system will merge the items.
     *
     * Uses the CartService.automaticallyMergeLikeItems to determine if the key should
     * include non-promotion related merging.
     *
     * This process complements the merging and splitting required for the system.  To fully
     * understand this flag, you need to understand the reason that the system splits items.
     *
     * In the cart, an item can only have one price.   Consider the example, where you have a cart-item with a quantity
     * of two and a buy-one get one free promotion.   After the promotion is applied, you would have two
     * items in the cart: one at the regular price, and one free.
     *
     * Now, assume the promotion is removed.   The system will automatically merge the two items
     * back together.
     *
     * This method generates an "identifier" (or key) that determines whether two items can
     * be merged.   The expected behavior allows users to amend the merging requirements for
     * promotions and choose to always merge like items by setting the CartService.automaticallyMergeLikeItems
     * to true.
     *
     * If merging is too aggressive for your implementation, you might choose to override this
     * method to add more specific rules for merging like items.    The key is that the system
     * will merge items that return the same String identifier.
     *
     * The out of box implementation insures the following:
     * - Items in a bundle do not get merged outside of the bundle
     * - Items do not get merged with items in a separate fulfillment group
     * - If the CartService.automaticallyMergeLikeItems is false, only items that were
     *   previously split by the promotion engine will be merged; otherwise, the system
     *   will try to merge all like items.
     *
     * @param orderItem
     * @param extraIdentifier
     * @return
     */
    public String buildIdentifier(OrderItem orderItem, String extraIdentifier);

    public FulfillmentGroupItemDao getFulfillmentGroupItemDao();

    public void setFulfillmentGroupItemDao(FulfillmentGroupItemDao fulfillmentGroupItemDao);

    public FulfillmentGroupService getFulfillmentGroupService();

    public void setFulfillmentGroupService(FulfillmentGroupService fulfillmentGroupService);

    public OrderItemService getOrderItemService();

    public void setOrderItemService(OrderItemService orderItemService);

    public OrderMultishipOptionService getOrderMultishipOptionService();

    public void setOrderMultishipOptionService(OrderMultishipOptionService orderMultishipOptionService);

    public OrderService getOrderService();

    public void setOrderService(OrderService orderService);

    public PromotableItemFactory getPromotableItemFactory();

    public void setPromotableItemFactory(PromotableItemFactory promotableItemFactory);
}
