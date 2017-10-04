package org.broadleafcommerce.common.sms.service.type;

/**
 * @author Nick Crum ncrum
 */
public class SMSMessage {

    protected String to;
    protected String from;
    protected String body;
    protected String mediaUrl;

    public SMSMessage() {
    }

    public SMSMessage(String to, String from, String body) {
        this.to = to;
        this.from = from;
        this.body = body;
    }

    public SMSMessage(String to, String from, String body, String mediaUrl) {
        this.to = to;
        this.from = from;
        this.body = body;
        this.mediaUrl = mediaUrl;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
