package org.broadleafcommerce.cms.site.domain;

import org.broadleafcommerce.cms.message.domain.ContentMessage;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface Site {
    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getSiteIdentifierType();

    public void setSiteIdentifierType(String siteIdentifierType);

    public String getSiteIdentifierValue();

    public void setSiteIdentifierValue(String siteIdentifierValue);

    public String getSandboxName();

    public void setSandboxName(String sandboxName);

    public List<ContentMessage> getContentMessages();

    public void setContentMessages(List<ContentMessage> contentMessages);
}
