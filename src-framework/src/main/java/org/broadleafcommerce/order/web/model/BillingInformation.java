package org.broadleafcommerce.order.web.model;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.profile.domain.CustomerAddress;

public class BillingInformation {

    private CustomerAddress customerAddress;
    private Long creditCardNumber;
    private Integer cvvCodeNewCard;
    private String creditCardType;
    private Integer expirationMonth;
    private String expirationMonthString;
    private Integer expirationYear;

    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Long getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(Long creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public Integer getCvvCodeNewCard() {
        return cvvCodeNewCard;
    }

    public void setCvvCodeNewCard(Integer cvvCodeNewCard) {
        this.cvvCodeNewCard = cvvCodeNewCard;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getExpirationMonthString() {
        return expirationMonthString;
    }

    public void setExpirationMonthString(String expirationMonthString) {
        this.expirationMonthString = expirationMonthString;
        if (StringUtils.trimToNull(expirationMonthString) == null) {
            this.expirationMonth = null;
        } else {
            this.expirationMonth = Integer.valueOf(expirationMonthString);
        }
    }
}
