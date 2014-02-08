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

package org.broadleafcommerce.common.sitemap.domain;

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * CustomSiteMapGenerator is controlled by this configuration.
 * 
 * @author bpolster
 */
@Entity
@Table(name = "BLC_CUST_SITE_MAP_GEN_CFG")
@AdminPresentationClass(friendlyName = "CustomUrlSiteMapGeneratorConfigurationImpl")
public class CustomUrlSiteMapGeneratorConfigurationImpl extends SiteMapGeneratorConfigurationImpl implements CustomUrlSiteMapGeneratorConfiguration {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "customUrlSiteMapGeneratorConfiguration", targetEntity = SiteMapUrlEntryImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @AdminPresentationCollection(friendlyName = "CustomUrlSiteMapGeneratorConfigurationImpl_Custom_URL_Entries")
    protected List<SiteMapUrlEntry> customURLEntries = new ArrayList<SiteMapUrlEntry>();

    @Override
    public List<SiteMapUrlEntry> getCustomURLEntries() {
        return customURLEntries;
    }

    @Override
    public void setCustomURLEntries(List<SiteMapUrlEntry> customURLEntries) {
        this.customURLEntries = customURLEntries;
    }

}
