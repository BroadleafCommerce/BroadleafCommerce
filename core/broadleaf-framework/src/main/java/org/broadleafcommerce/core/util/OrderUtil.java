/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Utility class for Order related needs   
 * 
 * @author Daniel Colgrove (dcolgrove)
 */
public class OrderUtil {

    /**
     * Traverses an Order and produces a simple JSON structure for output
     * 
     * @param order
     * @return 
     */
    public static JSONObject convertOrderToJSON(Order order) {
        try {
            JSONObject containerJSON = new JSONObject();
            JSONObject orderJSON = new JSONObject();
            JSONArray itemsJSON = new JSONArray();
            JSONArray fgJSON = new JSONArray();

            orderJSON.put("orderId",  order.getId());
            
            containerJSON.put("order", orderJSON);
            orderJSON.put("items", itemsJSON);
            orderJSON.put("fulfillmentGroups", fgJSON);
           
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem instanceof BundleOrderItem) {
                    BundleOrderItem boi = (BundleOrderItem) orderItem;
                    JSONObject bundle = convertOneItem(boi);
                    itemsJSON.put(bundle);
                    JSONArray bundleItems = new JSONArray();
                    bundle.put("bundledItems",  bundleItems);
                    List<DiscreteOrderItem> doItems = boi.getDiscreteOrderItems();
                    for (DiscreteOrderItem doi : doItems) {
                        bundleItems.put(convertItems(doi));
                    }
                } else {
                    if (orderItem instanceof DiscreteOrderItem) {
                        DiscreteOrderItem doi = (DiscreteOrderItem) orderItem;
                        itemsJSON.put(convertItems(doi));
                    }
                }
            }
            fgJSON.put(convertFulfillmentGroups(order.getFulfillmentGroups()));
            return containerJSON;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    protected static JSONObject convertItems(OrderItem doi) throws JSONException {
        JSONObject parentItem = convertOneItem(doi);
        if (CollectionUtils.isNotEmpty(doi.getChildOrderItems())) {
            JSONArray children = new JSONArray();
            parentItem.put("childItems",  children);
            for (OrderItem childOrderItem : doi.getChildOrderItems()) {
                children.put(convertItems(childOrderItem));
            }
        }
        return parentItem;
    }

    protected static JSONObject convertOneItem(OrderItem item) throws JSONException {
        JSONObject itemJson = new JSONObject();
        itemJson.put("quantity", item.getQuantity());
        if (item instanceof DiscreteOrderItem) {
            DiscreteOrderItem doi = (DiscreteOrderItem) item;
            itemJson.put("skuId", doi.getSku().getId());
            itemJson.put("itemId",  doi.getId());
        } else {
            if (item instanceof BundleOrderItem) {
                BundleOrderItem doi = (BundleOrderItem) item;
                itemJson.put("skuId", doi.getSku().getId());
                itemJson.put("itemId",  doi.getId());
            }
        }
        itemJson.put("quantity", item.getQuantity());
        return itemJson;
    }

    protected static JSONObject convertFulfillmentGroups(List<FulfillmentGroup> groups) throws JSONException {
        JSONObject groupJSON = new JSONObject();
        for(FulfillmentGroup group : groups) {
            JSONArray children = new JSONArray();
            groupJSON.put("items",  children);
            for(FulfillmentGroupItem item : group.getFulfillmentGroupItems()) {
                JSONObject fgItem = new JSONObject();
                children.put(fgItem);
                fgItem.put("orderItem", item.getOrderItem().getId());
                fgItem.put("quantity",  item.getQuantity());
            }
        }
        return groupJSON;
    }
    
}
