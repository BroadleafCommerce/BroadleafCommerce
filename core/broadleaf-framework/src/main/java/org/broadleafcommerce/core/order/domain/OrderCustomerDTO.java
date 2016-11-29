package org.broadleafcommerce.core.order.domain;

import java.util.Map;

public class OrderCustomerDTO {

    protected Long externalId;
    
    protected String firstName;

    protected String lastName;
    
    protected String emailAddress;
    
    protected boolean anonymous;
    
    protected Map<String, Object> additionalFields;
    
    public Long getExternalId() {
        return externalId;
    }
    
    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public boolean isAnonymous() {
        return anonymous;
    }
    
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
    
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }
    
    public void setAdditionalFields(Map<String, Object> additionalFields) {
        this.additionalFields = additionalFields;
    }
    
    public void populateOrderWithOrderCustomerDTO(Order order) {
        order.setAnonymous(this.isAnonymous());
        order.setEmailAddress(this.emailAddress);
        order.setFirstName(this.firstName);
        order.setLastName(this.lastName);
        order.setCustomerExternalId(this.externalId);
    }
    
    public static OrderCustomerDTO createOrderCustomerDtoFromOrder(Order order) {
        OrderCustomerDTO dto = new OrderCustomerDTO();
        dto.setAnonymous(order.isAnonymous());
        dto.setEmailAddress(order.getEmailAddress());
        dto.setExternalId(order.getCustomerExternalId());
        dto.setFirstName(order.getFirstName());
        dto.setLastName(order.getLastName());
        return dto;
    }
    
}
