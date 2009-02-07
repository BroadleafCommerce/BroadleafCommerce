package org.broadleafcommerce.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.util.Basket;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("basket")
public class ListBasketFormController {
    protected final Log logger = LogFactory.getLog(getClass());

    private String redirectUrl = "";
    private Basket basket = new Basket();
    private OrderService orderService;
    private CustomerService customerService;

    @ModelAttribute("basket")
    public Basket getbasket() {
        Order basketOrder = getCustomerBasket();
        basket.setOrder(basketOrder);
        basket.setItems(orderService.getItemsForOrder(basketOrder.getId()));
        return basket;
        // return basket;
    }

    // @ModelAttribute("basket")
    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String addSellableItem(@RequestParam("sellableItemId") Long sellableItemId) {
        Order basketOrder = getCustomerBasket();
        orderService.addItemToOrder(basketOrder.getId(), sellableItemId, 1);
        return "redirect:" + redirectUrl;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String listBasket() {
        return "listBasket";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateQuantity(Basket basket, BindingResult result) {
        for (OrderItem orderItem : basket.getItems()) {
            orderService.updateItemInOrder(orderItem.getOrder(), orderItem);
        }
        return "redirect:" + redirectUrl;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String removeItem(@RequestParam("sellableItemId") Long sellableItemId) {
        Order basketOrder = getCustomerBasket();
        orderService.removeItemFromOrder(basketOrder.getId(), sellableItemId);
        return "redirect:" + redirectUrl;
    }

    private Order getCustomerBasket() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = customerService.readCustomerByUsername(auth.getName());
        return orderService.getCurrentBasketForUserId(customer.getId());
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
