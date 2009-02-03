package org.broadleafcommerce.profile.service;

import org.broadleafcommerce.profile.domain.User;


public interface EmailService {

   public void sendEmail(User user, String template, String fromAddress, String subject);
   
}