/*
 * Copyright 2012 the original author or authors.
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
package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemImpl;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * The controller responsible for wishlist management activities, including
 * viewing a wishlist, moving items from the wishlist to the cart, and
 * removing items from the wishlist
 *
 *
 * @author jfridye
 */
public class BroadleafAccountWishlistController extends AbstractAccountController {

    private String accountWishlistView = "account/wishlist";

    public String viewWishlist(HttpServletRequest request, HttpServletResponse response, Model model) {
        Order wishlist = orderService.findNamedOrderForCustomer("wishlist", CustomerState.getCustomer());
        model.addAttribute("wishlist", wishlist);
        return ajaxRender(getAccountWishlistView(), request, model);
    }

    public String removeItemFromWishlist(HttpServletRequest request, HttpServletResponse response, Model model, Long itemId) {
        Order wishlist = orderService.findNamedOrderForCustomer("wishlist", CustomerState.getCustomer());
        try {
            orderService.removeItem(wishlist.getId(), itemId, false);
        } catch (RemoveFromCartException e) {
            e.printStackTrace();
        }
        model.addAttribute("wishlist", wishlist);
        return ajaxRender(getAccountWishlistView(), request, model);
    }

    public String moveItemToCart(HttpServletRequest request, HttpServletResponse response, Model model, Long orderItemId) {

        Order wishlist = orderService.findNamedOrderForCustomer("wishlist", CustomerState.getCustomer());

        List<OrderItem> orderItems = wishlist.getOrderItems();

        OrderItem orderItem = null;

        for (OrderItem item : orderItems) {
            if (orderItemId == item.getId()) {
                orderItem = item;
                break;
            }
        }

        if (orderItem != null) {
            try {
                orderService.addItemFromNamedOrder(wishlist, orderItem, false);
            } catch (RemoveFromCartException e) {
                e.printStackTrace();
            } catch (AddToCartException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("The item id provided was not found in the wishlist");
        }

        model.addAttribute("wishlist", wishlist);

        return ajaxRender(getAccountWishlistView(), request, model);
    }

    public String moveAllItemsToCart(HttpServletRequest request, HttpServletResponse response, Model model) {
        Order wishlist = orderService.findNamedOrderForCustomer("wishlist", CustomerState.getCustomer());
        try {
            orderService.addAllItemsFromNamedOrder(wishlist, false);
        } catch (RemoveFromCartException e) {
            e.printStackTrace();
        } catch (AddToCartException e) {
            e.printStackTrace();
        }
        model.addAttribute("wishlist", wishlist);
        return ajaxRender(getAccountWishlistView(), request, model);
    }

    public void setAccountWishlistView(String updateAccountView) {
        this.accountWishlistView = updateAccountView;
    }

    public String getAccountWishlistView() {
        return accountWishlistView;
    }

}
