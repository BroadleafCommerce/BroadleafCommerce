/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.media.domain.Media;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *  JAXB wrapper class for Media.
 */
@XmlRootElement(name = "media")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class MediaWrapper extends BaseWrapper implements APIWrapper<Media> {

    /**
     * This allows us to control whether the URL should / can be overwritten, for example by the static asset service.
     */
    @XmlTransient
    protected boolean allowOverrideUrl = true;

    @XmlElement
    protected Long id;

    @XmlElement
    protected String title;

    @XmlElement
    protected String url;

    @XmlElement
    protected String altText;
    
    @XmlElement
    protected String tags;
    
    @Override
    public void wrapDetails(Media media, HttpServletRequest request) {
        this.id = media.getId();
        this.title = media.getTitle();
        this.altText = media.getAltText();
        this.tags = media.getTags();
        this.url = media.getUrl();
    }

    @Override
    public void wrapSummary(Media media, HttpServletRequest request) {
        wrapDetails(media, request);
    }

    public boolean isAllowOverrideUrl() {
        return allowOverrideUrl;
    }

    public void setAllowOverrideUrl(boolean allow) {
        this.allowOverrideUrl = allow;
    }
    
    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Call this only if allowOverrideUrl is true, and only AFTER you call wrap.
     * @param url
     */
    public void setUrl(String url) {
        if (allowOverrideUrl) {
            this.url = url;
        }
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    
    /**
     * @return the altText
     */
    public String getAltText() {
        return altText;
    }

    
    /**
     * @param altText the altText to set
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    
    /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    
    /**
     * @param tags the tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
    }
}
