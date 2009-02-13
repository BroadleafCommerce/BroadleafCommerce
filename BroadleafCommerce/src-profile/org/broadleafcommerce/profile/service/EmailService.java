package org.broadleafcommerce.profile.service;

import org.broadleafcommerce.profile.domain.Customer;

public interface EmailService {

    public void sendEmail(Customer customer, String template, String fromAddress, String subject);
}