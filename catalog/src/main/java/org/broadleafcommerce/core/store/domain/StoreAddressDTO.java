package org.broadleafcommerce.core.store.domain;


public class StoreAddressDTO {

    protected Long addressExternalId;
    
    protected String postalCode;
    
    public Long getAddressExternalId() {
        return addressExternalId;
    }
    
    public void setAddressExternalId(Long addressExternalId) {
        this.addressExternalId = addressExternalId;
    }

    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
}
