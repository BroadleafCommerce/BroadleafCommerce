package org.broadleafcommerce.email.service;

import org.broadleafcommerce.email.domain.EmailListType;
import org.broadleafcommerce.email.service.validator.EmailListRequest;

public interface EmailListService {
    public void subscribe(EmailListRequest emailListRequest);

    public EmailListType retrieveCurrentList(String emailAddress);

    public boolean isOnList(String emailAddress, EmailListType emailListType);

    public boolean emailExists(String email);

    public void changeEmailAddress(String oldEmail, String newEmail);

    public void unsubscribe(String email, EmailListType emailListType);

    public void unsubscribe(String email);

    public void setCustomerStore(String email, String store);

    public void sendWelcomeEmail(String email);

}
