package org.broadleafcommerce.order.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.MergeCartResponse;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.profile.web.MergeCartProcessor;
import org.springframework.security.Authentication;
import org.springframework.stereotype.Service;

@Service("mergeCartProcessor")
public class MergeCartProcessorImpl implements MergeCartProcessor {

    private String mergeCartItemsAddedKey = "merge_cart_items_added";

    private String mergeCartItemsRemovedKey = "merge_cart_items_removed";

    @Resource
    private CustomerService customerService;

    @Resource
    private OrderService orderService;

    @Resource
    private CustomerState customerState;

    public void execute(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer loggedInCustomer = customerService.readCustomerByUsername((String) authResult.getPrincipal());
        Customer anonymousCustomer = customerState.getCustomer(request);
        Order cart = orderService.findCartForCustomer(anonymousCustomer);
        Long anonymousCartId;
        if (cart != null) {
            anonymousCartId = cart.getId();
        } else {
            anonymousCartId = null;
        }
        MergeCartResponse mergeCartResponse;
        try {
            mergeCartResponse = orderService.mergeCart(loggedInCustomer, anonymousCartId);
        } catch (PricingException e) {
            throw new RuntimeException(e);
        }
        if (!mergeCartResponse.getAddedItems().isEmpty()) {
            request.getSession().setAttribute(mergeCartItemsAddedKey, mergeCartResponse.getAddedItems());
        }
        if (!mergeCartResponse.getRemovedItems().isEmpty()) {
            request.getSession().setAttribute(mergeCartItemsRemovedKey, mergeCartResponse.getRemovedItems());
        }
    }

    public String getMergeCartItemsAddedKey() {
        return mergeCartItemsAddedKey;
    }

    public void setMergeCartItemsAddedKey(String mergeCartItemsAddedKey) {
        this.mergeCartItemsAddedKey = mergeCartItemsAddedKey;
    }

    public String getMergeCartItemsRemovedKey() {
        return mergeCartItemsRemovedKey;
    }

    public void setMergeCartItemsRemovedKey(String mergeCartItemsRemovedKey) {
        this.mergeCartItemsRemovedKey = mergeCartItemsRemovedKey;
    }
}
