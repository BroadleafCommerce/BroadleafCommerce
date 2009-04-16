package org.broadleafcommerce.order.domain;

public interface PersonalMessage {

    public Long getId();

    public void setId(Long id);

    public String getMessageTo();

    public void setMessageTo(String messageTo);

    public String getMessageFrom();

    public void setMessageFrom(String messageFrom);

    public String getMessage();

    public void setMessage(String message);
}
