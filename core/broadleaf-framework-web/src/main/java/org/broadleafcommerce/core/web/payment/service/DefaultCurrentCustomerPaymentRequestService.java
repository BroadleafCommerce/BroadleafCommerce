/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.payment.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.service.CurrentCustomerPaymentRequestService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.PaymentRequestDTOService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAttribute;
import org.broadleafcommerce.profile.core.domain.CustomerAttributeImpl;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blDefaultCurrentCustomerPaymentRequestService")
public class DefaultCurrentCustomerPaymentRequestService implements CurrentCustomerPaymentRequestService {

    private static final Log LOG = LogFactory.getLog(DefaultCurrentCustomerPaymentRequestService.class);

    @Resource(name = "blPaymentRequestDTOService")
    protected PaymentRequestDTOService paymentRequestDTOService;

    @Resource(name = "blCustomerService")
    protected CustomerService customerService;

    @Override
    public PaymentRequestDTO getPaymentRequestFromCurrentCustomer() {
        Customer customer = CustomerState.getCustomer();
        PaymentRequestDTO customerRequest = new PaymentRequestDTO();
        return paymentRequestDTOService.populateCustomerInfo(customerRequest, customer);
    }

    @Override
    public void addCustomerAttributeToCurrentCustomer(String customerAttributeKey, String customerAttributeValue) throws PaymentException {
        addCustomerAttributeToCustomer(null, customerAttributeKey, customerAttributeValue);
    }

    @Override
    public void addCustomerAttributeToCustomer(Long customerId, String customerAttributeKey, String customerAttributeValue) throws PaymentException {
        Customer currentCustomer = CustomerState.getCustomer();
        Long currentCustomerId = currentCustomer.getId();

        if (customerId != null && !currentCustomerId.equals(customerId)) {
            logWarningIfCustomerMismatch(currentCustomerId, customerId);
            currentCustomer = customerService.readCustomerById(customerId);
        }

        CustomerAttribute customerAttribute = new CustomerAttributeImpl();
        customerAttribute.setName(customerAttributeKey);
        customerAttribute.setValue(customerAttributeValue);
        customerAttribute.setCustomer(currentCustomer);
        currentCustomer.getCustomerAttributes().put(customerAttributeKey, customerAttribute);

        customerService.saveCustomer(currentCustomer);

    }

    @Override
    public String retrieveCustomerAttributeFromCurrentCustomer(String customerAttributeKey) {
        return retrieveCustomerAttributeFromCustomer(null, customerAttributeKey);
    }

    @Override
    public String retrieveCustomerAttributeFromCustomer(Long customerId, String customerAttributeKey) {
        Customer currentCustomer = CustomerState.getCustomer();
        Long currentCustomerId = currentCustomer.getId();

        if (customerId != null && !currentCustomerId.equals(customerId)) {
            logWarningIfCustomerMismatch(currentCustomerId, customerId);
            currentCustomer = customerService.readCustomerById(customerId);
        }

        if (currentCustomer.getCustomerAttributes().containsKey(customerAttributeKey)) {
            return currentCustomer.getCustomerAttributes().get(customerAttributeKey).getValue();
        }

        return null;
    }

    protected void logWarningIfCustomerMismatch(Long currentCustomerId, Long customerId) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(String.format("The current customer resolved from customer state [%s] is not the same as the requested customer ID [%s]. Session may have expired or local cart state was lost. This may need manual review.", currentCustomerId, customerId));
        }
    }
}
