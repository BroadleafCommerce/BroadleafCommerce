package org.broadleafcommerce.order.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.order.web.model.ProductItem;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 1) Created the WishlistController as an extension of the CartController
 * 2) Methods addItem, addItems, removeItem, and updateItemQuantity are defined in the CartController
 * 3) Renamed selectWishlist method to viewWishlists
 * 4) Renamed addWishlist method to createWishlist
 * 5) Modified the moveItemToCart method to take in a OrderItem object
 * 6) Removed the displayWishlist method because it is not needed
 * 7) Changed all the Error objects to BindingResult
 * 8) Override the retrieveOrder method to return a wishlist order
 */
@Controller("wishlistController")
public class WishlistController extends CartController {

    public WishlistController() {
        super();
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String viewWishlists(ModelMap model, HttpServletRequest request) {
        List<Order> wishlistOrders = cartService.findOrdersForCustomer(customerState.getCustomer(request), OrderStatus.NAMED.toString());
        model.addAttribute("wishlists", wishlistOrders);
        return "success";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String createWishlist(ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = retrieveOrder(request);
        if (model.containsAttribute("wishlistItems")) {
            List<ProductItem> productItemList = (List<ProductItem>)model.get("productItemList");
            if (productItemList != null) {
                for (ProductItem productItem : productItemList) {
                    try {
                        cartService.addSkuToOrder(wishlistOrder.getId(), productItem.getSkuId(), productItem.getProductId(),productItem.getCategoryId(), productItem.getQuantity());
                    } catch (PricingException e) {
                        e.printStackTrace();
                        //TODO How to handle from the UI perspective???
                    }
                }
            }
        }
        model.remove("wishlistItems");
        model.addAttribute("wishList", wishlistOrder);
        return "success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String removeWishlist(@RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        cartService.removeNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        return "success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String moveItemToCart(@ModelAttribute OrderItem orderItem, @RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = cartService.findNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        try {
            cartService.moveItemToCartFromNamedOrder(wishlistOrder, orderItem);
        } catch (Exception e) {
            e.printStackTrace();
            /*
             * TODO Kenny will look at handling this properly
             */
        }
        return "redirect:success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String moveAllItemsToCart(@RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = cartService.findNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        try {
            cartService.moveAllItemsToCartFromNamedOrder(wishlistOrder);
        } catch (Exception e) {
            e.printStackTrace();
            /*
             * TODO Kenny will look at handling this properly
             */
        }
        return "redirect:success";
    }

    // override the retreiveOrder method in CartController to return a name wishlist order
    protected Order retrieveOrder(HttpServletRequest request) {
        String wishlistName = request.getParameter("wishlistName");
        Order currentWishlistOrder = cartService.createNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        return currentWishlistOrder;
    }

}
