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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation the urlset element defined in the schema definition at
 * http://www.sitemaps.org/schemas/sitemap/0.9.
 * 
 * @author bpolster
 */
@XmlRootElement(name = "urlset")
public class SiteMapURLSetWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SiteMapURLWrapper> siteMapUrlWrappers = new ArrayList<SiteMapURLWrapper>();
    
    public List<SiteMapURLWrapper> getSiteMapUrlWrappers() {
        return siteMapUrlWrappers;
    }
    
    @XmlElement(name = "url")
    public void setSiteMapUrlWrappers(List<SiteMapURLWrapper> siteMapUrlWrappers) {
        this.siteMapUrlWrappers = siteMapUrlWrappers;
    }
}
