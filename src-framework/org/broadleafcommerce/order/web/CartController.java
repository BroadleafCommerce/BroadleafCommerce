package org.broadleafcommerce.order.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.web.model.ProductItem;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.web.CustomerState;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 1) Removed showCartSummary method: method not need, populate cart page using the order object
 * 2) Add addItem method to add one item to the cart
 * 3) Renamed addItemsToCart to addItems to add one or more items to cart
 * 4) Removed addToCartLink method: does the same thing as addItems
 * 5) Removed all ajax specific checks; need to figure out a clean way to handle ajax calls
 * 6) Removed validateLogoShopItemsInOrder method which is TCS specific
 * 7) Removed creating a DiscreteOrderItemRequest in the addToCart method since the cartService.addSkuToOrder will create it for you
 * 8) Renamed removeItemFromCart to removeItem
 * 9) Changed CartSummary to a List of OrderItem
 * 10) Removed TCS specific beginCanadianCheckout method
 * 11) Removed viewSolution method, not sure what is purpose of this method
 * 12) Removed refererRedirect method since it is no longer used by this class
 * 13) Removed validateProductsAndCategory method since it was not used
 * 14) All Error objects have been changed to BindingResult
 */
@Controller("cartController")
public class CartController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    protected final CartService cartService;
    @Resource
    protected final CustomerState customerState;

    public CartController() {
        this.cartService = null;
        this.customerState = null;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String addItem(@ModelAttribute ProductItem productItem, BindingResult errors, ModelMap model,
            HttpServletRequest request) {
        List<ProductItem> productItemList = new ArrayList<ProductItem>();
        productItemList.add(productItem);
        addItems(productItemList, errors, model, request);
        List<OrderItem> orderItemsAdded = (List<OrderItem>)model.remove("orderItemsAdded");
        OrderItem orderItemAdded = orderItemsAdded.get(0);
        model.addAttribute("orderItemAdded", orderItemAdded);
        return "success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String addItems(@ModelAttribute List<ProductItem> productItemList, BindingResult errors, ModelMap model,
            HttpServletRequest request) {
        Order currentCartOrder = retrieveOrder(request);
        List<OrderItem> orderItemsAdded = new ArrayList<OrderItem>();
        for (ProductItem productItem : productItemList) {
            if (productItem.getQuantity() > 0) {
                try {
                    OrderItem orderItem = cartService.addSkuToOrder(currentCartOrder.getId(), productItem.getSkuId(), productItem.getProductId(), productItem.getCategoryId(), productItem.getQuantity());
                    orderItemsAdded.add(orderItem);
                } catch (PricingException e) {
                    e.printStackTrace();
                    //TODO Handle this exception appropriately from the UI perspective
                }
            }
        }
        model.addAttribute("orderItemsAdded", orderItemsAdded);
        return "success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String addToWishlist(@ModelAttribute List<ProductItem> productItemList, BindingResult errors, ModelMap model,
            HttpServletRequest request) {
        return "redirect:success";  // need to be redirected to the viewWishlists method of the WishlistController
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String removeItem(@RequestParam long orderItemId, ModelMap model, HttpServletRequest request) {
        Order currentCartOrder = retrieveOrder(request);
        try {
            currentCartOrder = cartService.removeItemFromOrder(currentCartOrder.getId(), orderItemId);
        } catch (PricingException e) {
            model.addAttribute("error", "remove");
            logger.error("An error occurred while removing an item from the cart.", e);
        }
        return "success";
    }

    // do we need this method? should begin checkout be a call to updateItemQuality with a redirect to checkout?
    // Checkout would decide what to display depending on the isStorePickup
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String beginCheckout(@ModelAttribute List<OrderItem> orderItemList, BindingResult errors, @RequestParam (required = false) Boolean isStorePickup, ModelMap model, HttpServletRequest request) {
        String view = updateItemQuantity(orderItemList, errors, model, request);
        if (!view.equals("error")) {
            model.addAttribute("isStorePickup", isStorePickup);
            if (SecurityContextHolder.getContext().getAuthentication() == null || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
                model.addAttribute("nextStep", "checkout");
                return "login";
            }
        }
        return view;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String updateItemQuantity(@ModelAttribute List<OrderItem> updatedOrderItemList, BindingResult errors, ModelMap model, HttpServletRequest request) {
        Order currentCartOrder = retrieveOrder(request);
        List<OrderItem> currentOrderItems = currentCartOrder.getOrderItems();
        for (OrderItem updatedOrderItem : updatedOrderItemList) {
            OrderItem currentOrderItem = (OrderItem)CollectionUtils.find(currentOrderItems,
                    new BeanPropertyValueEqualsPredicate("id", updatedOrderItem.getId()));
            //in case the item was removed from the cart from another browser tab
            if (currentOrderItem != null) {
                try{
                    //MIKE: Change to less than and equals to so that a quantity of 0 also removes the item
                    if (updatedOrderItem.getQuantity() >= 0) {
                        currentOrderItem.setQuantity(updatedOrderItem.getQuantity());
                        cartService.updateItemInOrder(currentCartOrder, currentOrderItem);
                    } else {
                        cartService.removeItemFromOrder(currentCartOrder, currentOrderItem);
                    }
                } catch (Exception e) {
                    logger.error("An error occurred while updating the quantity of an item.", e);
                    model.addAttribute("error", "quantity");
                    return "error";
                }
            }
        }
        return "success";
    }

    protected Order retrieveOrder(HttpServletRequest request) {
        Customer currentCustomer = customerState.getCustomer(request);
        Order currentCartOrder = null;
        if (currentCustomer != null) {
            currentCartOrder = cartService.findCartForCustomer(currentCustomer);
            if (currentCartOrder == null) {
                currentCartOrder = cartService.createNewCartForCustomer(currentCustomer);
            }
        }
        return currentCartOrder;
    }

}
