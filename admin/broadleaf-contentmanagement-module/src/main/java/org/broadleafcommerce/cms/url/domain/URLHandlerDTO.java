/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;


/**
 * A bean representation of a URLHandler
 *
 * @author bpolster
 */
public class URLHandlerDTO implements URLHandler {

    private static final long serialVersionUID = 1L;
    protected Long id = null;
    protected String incomingURL = "";
    protected String newURL;
    protected String urlRedirectType;
    protected boolean isRegex = false;

    public URLHandlerDTO(String newUrl, URLRedirectType redirectType) {
        setUrlRedirectType(redirectType);
        setNewURL(newUrl);
    }

    public URLHandlerDTO(Long id, String incomingURL, String newUrl, URLRedirectType redirectType) {
        this(newUrl, redirectType);
        setId(id);
        setIncomingURL(incomingURL);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIncomingURL() {
        return incomingURL;
    }

    public void setIncomingURL(String incomingURL) {
        this.incomingURL = incomingURL;
    }

    public String getNewURL() {
        return newURL;
    }

    public void setNewURL(String newURL) {
        this.newURL = newURL;
    }

    @Override
    public boolean isRegexHandler() {
        return isRegex;
    }

    /**
     * @Deprecated use {@link #setRegexHandler(Boolean regexHandler)}
     */
    @Deprecated
    @Override
    public void setRegexHandler(boolean regexHandler) {
        this.isRegex = regexHandler;
    }

    @Override
    public void setRegexHandler(Boolean regexHandler) {
        this.isRegex = regexHandler != null ? regexHandler : false;
    }

    @Override
    public URLRedirectType getUrlRedirectType() {
        return URLRedirectType.getInstance(urlRedirectType);
    }

    @Override
    public void setUrlRedirectType(URLRedirectType redirectType) {
        this.urlRedirectType = redirectType.getType();
    }

    @Override
    public <G extends URLHandler> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        URLHandler cloned = createResponse.getClone();
        cloned.setIncomingURL(incomingURL);
        cloned.setNewURL(newURL);
        cloned.setUrlRedirectType(URLRedirectType.getInstance(urlRedirectType));
        cloned.setRegexHandler(new Boolean(isRegex));
        return createResponse;
    }
}
