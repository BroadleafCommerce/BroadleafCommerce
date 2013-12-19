/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.broadleafcommerce.common.sitemap.wrapper;

import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.broadleafcommerce.common.util.FormatUtil;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Representation of SiteMapURLEntry that can be used to generate an XML element.
 * 
 * @author bpolster
 */
@XmlRootElement(name = "url")
@XmlType(propOrder = { "loc", "lastmod", "changefreq", "priority" })
public class SiteMapURLWrapper implements Serializable {

    private static final long serialVersionUID = 1L;   

    protected String loc;

    protected String lastmod;

    protected String changefreq;

    protected String priority;

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
