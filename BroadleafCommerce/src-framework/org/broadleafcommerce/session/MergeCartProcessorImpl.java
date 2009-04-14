package org.broadleafcommerce.session;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.order.service.MergeCartResponse;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.security.CustomerState;
import org.broadleafcommerce.security.MergeCartProcessor;
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

    public void execute(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer loggedInCustomer = customerService.readCustomerByUsername((String) authResult.getPrincipal());
        Customer anonymousCustomer = CustomerState.getCustomer(request);
        Long anonymousCartId = orderService.findCartForCustomer(anonymousCustomer) == null ? null : orderService.findCartForCustomer(anonymousCustomer).getId();
        MergeCartResponse mergeCartResponse = orderService.mergeCart(loggedInCustomer, anonymousCartId);
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
