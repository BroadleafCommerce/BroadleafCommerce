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
