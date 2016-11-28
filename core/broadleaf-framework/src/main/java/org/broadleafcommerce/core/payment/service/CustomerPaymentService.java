/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.order.domain.OrderCustomer;
import org.broadleafcommerce.core.payment.domain.CustomerPayment;

import java.util.List;

public interface CustomerPaymentService {

    public CustomerPayment saveCustomerPayment(CustomerPayment customerPayment);

    public List<CustomerPayment> readCustomerPaymentsByCustomerId(Long customerId);

    public CustomerPayment readCustomerPaymentById(Long customerPaymentId);

    public CustomerPayment readCustomerPaymentByToken(String token);

    public void deleteCustomerPaymentById(Long customerPaymentId);

    public CustomerPayment create();

    public CustomerPayment findDefaultPaymentForCustomer(OrderCustomer orderCustomer);

    public CustomerPayment setAsDefaultPayment(CustomerPayment payment);

    public OrderCustomer deleteCustomerPaymentFromCustomer(OrderCustomer orderCustomer, CustomerPayment payment);

}
