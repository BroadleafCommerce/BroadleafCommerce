/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.sitemap.wrapper;

import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.broadleafcommerce.common.util.FormatUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Representation of SiteMapURLEntry that can be used to generate an XML element.
 * 
 * @author bpolster
 */
@XmlRootElement(name = "url")
@XmlType(propOrder = { "loc", "lastmod", "changefreq", "priority", "siteMapImageWrappers" })
public class SiteMapURLWrapper implements Serializable {

    private static final long serialVersionUID = 1L;   

    protected String loc;

    protected String lastmod;

    protected String changefreq;

    protected String priority;

    private List<SiteMapImageWrapper> siteMapImageWrappers = new ArrayList<>();

    public List<SiteMapImageWrapper> getSiteMapImageWrappers() {
        return siteMapImageWrappers;
    }

    @XmlElement(name = "image", namespace = "http://www.google.com/schemas/sitemap-image/1.1")
    public void setSiteMapImageWrappers(List<SiteMapImageWrapper> siteMapImageWrappers) {
        this.siteMapImageWrappers = siteMapImageWrappers;
    }

    public void addImage(SiteMapImageWrapper siteMapImage) {
        getSiteMapImageWrappers().add(siteMapImage);
    }

    public void setLastModDate(Date lastModDate) {
        if (lastModDate != null) {
            lastmod = FormatUtil.formatDateUsingW3C(lastModDate);
        } else {
            lastmod = FormatUtil.formatDateUsingW3C(new Date());
        }
    }

    public void setPriorityType(SiteMapPriorityType priorityType) {
        if (priorityType != null) {
            setPriority(priorityType.getType());
        }
    }

    public void setChangeFreqType(SiteMapChangeFreqType changeFreqType) {
        if (changeFreqType != null) {
            setChangefreq(changeFreqType.getFriendlyType());
        }
    }

    public String getLoc() {
        return loc;
    }

    @XmlElement
    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLastmod() {
        return lastmod;
    }

    @XmlElement
    public void setLastmod(String lastmod) {
        this.lastmod = lastmod;
    }

    public String getChangefreq() {
        return changefreq;
    }

    @XmlElement
    public void setChangefreq(String changefreq) {
        this.changefreq = changefreq;
    }
    
    public String getPriority() {
        return priority;
    }

    @XmlElement
    public void setPriority(String priority) {
        this.priority = priority;
    }
}
