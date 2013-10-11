/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.sitemap.domain;

import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation of SiteMapURLEntry that can be used to generate
 * an XML element.
 * 
 * @author bpolster
 */
@XmlRootElement(name = "url")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SiteMapURLWrapper {

    protected final SimpleDateFormat W3C_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    private static final long serialVersionUID = 1L;   

    @XmlElement
    protected String loc;

    @XmlElement
    protected String lastmod;

    @XmlElement
    protected String changefreq;

    @XmlElement
    protected String priority;

    /**
     * Populates the SiteMap object with valid W3C values for date, changeType, and
     * priority.   
     *
     * @param location
     * @param lastModDate
     * @param changeFreqType
     * @param priorityType
     */
    public void wrapConfiguration(String location, Date lastModDate,
            SiteMapChangeFreqType changeFreqType, SiteMapPriorityType priorityType) {
        assert location != null;
        loc = location;

        if (lastModDate != null) {
            lastmod = W3C_DATE_FORMAT.format(lastModDate);
        }

        if (changeFreqType != null) {
            changefreq = changeFreqType.getType();
        }

        if (priorityType != null) {
            priority = priorityType.getType();
        }
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLastmod() {
        return lastmod;
    }

    public void setLastmod(String lastmod) {
        this.lastmod = lastmod;
    }

    public String getChangefreq() {
        return changefreq;
    }

    public void setChangefreq(String changefreq) {
        this.changefreq = changefreq;
    }
    
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
    
}
