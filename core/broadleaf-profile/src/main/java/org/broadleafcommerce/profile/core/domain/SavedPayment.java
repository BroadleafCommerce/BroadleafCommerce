package org.broadleafcommerce.profile.core.domain;

/**
 * @author Jacob Mitash
 */
public interface SavedPayment {

    String getPaymentName();

    void setPaymentName(String paymentName);

    String getPersonName();

    void setPersonName(String name);

    boolean isDefaultMethod();

    void setDefaultMethod(boolean defaultMethod);

    String getLastFourDigits();

    void setLastFourDigits(String lastFourDigits);

    String getExpiration();

    void setExpiration(String expiration);

    String getToken();

    void setToken(String token);

    Customer getCustomer();

    void setCustomer(Customer customer);
}
