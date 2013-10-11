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

package org.broadleafcommerce.common.sitemap.wrapper;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation the sitemap element defined in the schema definition at
 * http://www.sitemaps.org/schemas/sitemap/0.9.
 * 
 * @author bpolster
 */
@XmlRootElement(name = "sitemap")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SiteMapWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    protected String loc;

    @XmlElement
    protected String lastmod;


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
}
