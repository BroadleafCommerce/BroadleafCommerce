package org.broadleafcommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("basket")
public class ListBasketFormController {
    /*protected final Log logger = LogFactory.getLog(getClass());

    private String redirectUrl = "";
    private Basket basket = new Basket();
    private OrderService orderService;
    private CustomerService customerService;

    @ModelAttribute("basket")
    public Basket getbasket() {
        BroadleafOrder basketOrder = getCustomerBasket();
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
    public String addSku(@RequestParam("skuId") Long skuId) {
    	BroadleafOrder basketOrder = getCustomerBasket();
        orderService.addItemToOrder(basketOrder.getId(), skuId, 1);
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
    public String removeItem(@RequestParam("skuId") Long skuId) {
        BroadleafOrder basketOrder = getCustomerBasket();
        orderService.removeItemFromOrder(basketOrder.getId(), skuId);
        return "redirect:" + redirectUrl;
    }

    private BroadleafOrder getCustomerBasket() {
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
    */
}
