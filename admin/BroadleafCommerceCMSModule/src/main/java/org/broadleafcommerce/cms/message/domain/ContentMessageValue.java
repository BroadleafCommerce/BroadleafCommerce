package org.broadleafcommerce.cms.message.domain;

/**
 * Created by bpolster.
 */
public interface ContentMessageValue {
    public Long getId();

    public void setId(Long id);

    public ContentMessage getContentMessage();

    public void setContentMessage(ContentMessage contentMessage);

    public String getLanguageCode();

    public void setLanguageCode(String languageCode);

    public String getMessageValue();

    public void setMessageValue(String messageValue);
}
