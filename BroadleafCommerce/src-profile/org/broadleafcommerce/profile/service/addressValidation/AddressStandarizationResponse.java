package org.broadleafcommerce.profile.service.addressValidation;

import org.broadleafcommerce.profile.domain.Address;

public class AddressStandarizationResponse {
    private Address address;
    private String returnText;
    private boolean errorDetected = false;

    public Address getAddress() {
        return address;
    }

    public String getReturnText() {
        return returnText;
    }

    public boolean isErrorDetected() {
        return errorDetected;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setErrorDetected(boolean errorDetected) {
        this.errorDetected = errorDetected;
    }

    public void setReturnText(String returnText) {
        this.returnText = returnText;
    }
}
