package org.broadleafcommerce.email.domain;

public class EmailAProduct {
    private String emailMessage;
    private String emailSubject;
    private String productUrl;
    private String recipientEmail;
    private String senderEmail;
    private boolean copySender;
    private boolean signUpForEmail;
    private long categoryId;
    private long productId;

    public long getCategoryId() {
        return categoryId;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public long getProductId() {
        return productId;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public boolean isCopySender() {
        return copySender;
    }

    public boolean isSignUpForEmail() {
        return signUpForEmail;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCopySender(boolean copySender) {
        this.copySender = copySender;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setSignUpForEmail(boolean signUpForEmail) {
        this.signUpForEmail = signUpForEmail;
    }
}
