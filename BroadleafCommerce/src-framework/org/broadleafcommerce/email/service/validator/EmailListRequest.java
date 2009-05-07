package org.broadleafcommerce.email.service.validator;

import org.broadleafcommerce.email.domain.EmailListType;

public class EmailListRequest {
    private String emailAddress;
    private EmailListType emailListType = EmailListType.MASTER;
    private boolean sendConfirmationEmail = false;
    private String comment;

    public EmailListRequest(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public EmailListRequest(String emailAddress, EmailListType emailListType) {
        this.emailListType = emailListType;
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public EmailListType getEmailListType() {
        return emailListType;
    }
    public void setEmailListType(EmailListType emailListType) {
        this.emailListType = emailListType;
    }
    public boolean isSendConfirmationEmail() {
        return sendConfirmationEmail;
    }
    public void setSendConfirmationEmail(boolean sendConfirmationEmail) {
        this.sendConfirmationEmail = sendConfirmationEmail;
    }
    public String getComment() {
        return comment;
    }

    public void generateComment(String remoteHost, String userAgent) {
        StringBuffer comment = new StringBuffer();
        comment.append("Host:").append(remoteHost).append(
        "|Browser:").append(userAgent);
        this.comment = comment.toString();
    }

}
