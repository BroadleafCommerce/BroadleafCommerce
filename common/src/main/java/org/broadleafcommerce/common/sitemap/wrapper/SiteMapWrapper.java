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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Representation the sitemap element defined in the schema definition at
 * http://www.sitemaps.org/schemas/sitemap/0.9.
 * 
 * @author bpolster
 */
@XmlRootElement(name = "sitemap")
@XmlType(propOrder = { "loc", "lastmod" })
public class SiteMapWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String loc;

    protected String lastmod;


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
}
