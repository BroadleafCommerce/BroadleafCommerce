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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation the sitemapindex element defined in the schema definition at
 * http://www.sitemaps.org/schemas/sitemap/0.9.
 * 
 * @author bpolster
 */
@XmlRootElement(name = "sitemapindex")
public class SiteMapIndexWrapper {

    private static final long serialVersionUID = 1L;

    private List<SiteMapWrapper> siteMapWrappers = new ArrayList<SiteMapWrapper>();
    
    public List<SiteMapWrapper> getSiteMapWrappers() {
        return siteMapWrappers;
    }
    
    @XmlElement(name = "sitemap")
    public void setSiteMapWrappers(List<SiteMapWrapper> siteMapWrappers) {
        this.siteMapWrappers = siteMapWrappers;
    }
}
