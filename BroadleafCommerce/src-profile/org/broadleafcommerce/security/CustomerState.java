package org.broadleafcommerce.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.stereotype.Component;

@Component("customerState")
public class CustomerState {

    private static final String DEFAULTSESSIONATTRIBUTENAME = "customer_session";

    @Resource
    private CustomerDao customerDao;

    public Customer getCustomer(HttpServletRequest request) {
        Object sessionReference = request.getSession().getAttribute(DEFAULTSESSIONATTRIBUTENAME);
        Customer customer;
        if (sessionReference instanceof Long) {
            Long customerId = (Long) sessionReference;
            if (customerId != null) {
                customer = customerDao.readCustomerById(customerId);
            } else {
                customer = null;
            }
        } else {
            customer = null;
        }

        return customer;
    }

    public void setCustomer(Customer customer, HttpServletRequest request) {
        request.getSession().setAttribute(DEFAULTSESSIONATTRIBUTENAME, customer.getId());
    }

    public Long getCustomerId(HttpServletRequest request) {
        return getCustomer(request) == null ? null : getCustomer(request).getId();
    }
}
