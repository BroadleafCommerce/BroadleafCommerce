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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration;
import org.broadleafcommerce.common.config.domain.AbstractModuleConfigurationAdminPresentation;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminatable;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminatableType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author Joshua Skorton (jskorton)
 */
@Entity
@Table(name = "BLC_SITE_MAP_CFG")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blConfigurationModuleElements")
//@AdminPresentationClass(friendlyName = "SiteMapConfigurationImpl")
public class SiteMapConfigurationImpl extends AbstractModuleConfiguration implements SiteMapConfiguration, AbstractModuleConfigurationAdminPresentation {

    private static final long serialVersionUID = 1L;
    private static Integer DEFAULT_MAX_URL_ENTRIES = 50000;

    private static String DEFAULT_SITE_MAP_FILE_NAME = "sitemap.xml";

    @Column(name = "MAX_URL_ENTRIES_PER_FILE")
    @AdminPresentation(excluded = true)
    protected Integer maximumURLEntriesPerFile;

    @OneToMany(mappedBy = "siteMapConfiguration", targetEntity = SiteMapGeneratorConfigurationImpl.class,
            cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    @AdminPresentationCollection(friendlyName = "SiteMapConfigurationImpl_Generator_Configurations", tab = TabName.General)
    @SiteDiscriminatable(type = SiteDiscriminatableType.SITE)
    protected List<SiteMapGeneratorConfiguration> siteMapGeneratorConfigurations = new ArrayList<SiteMapGeneratorConfiguration>();

    @Column(name = "SITE_MAP_FILE_NAME")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String siteMapFileName;

    @Column(name = "INDEXED_SITE_MAP_FILE_NAME")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String indexedSiteMapFileName;

    @Column(name = "INDEXED_SITE_MAP_FILE_PATTERN")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String indexedSiteMapFilePattern;

    public SiteMapConfigurationImpl() {
        super();
        super.setModuleConfigurationType(ModuleConfigurationType.SITE_MAP);
    }

    @Override
    public List<SiteMapGeneratorConfiguration> getSiteMapGeneratorConfigurations() {
        return siteMapGeneratorConfigurations;
    }

    @Override
    public void setSiteMapGeneratorConfigurations(List<SiteMapGeneratorConfiguration> siteMapGeneratorConfigurations) {
        this.siteMapGeneratorConfigurations = siteMapGeneratorConfigurations;
    }

    @Override
    public Integer getMaximumUrlEntriesPerFile() {
        if (maximumURLEntriesPerFile == null) {
            return DEFAULT_MAX_URL_ENTRIES;
        } else {
            return maximumURLEntriesPerFile.intValue();
        }
    }

    @Override
    public void setMaximumUrlEntriesPerFile(Integer maximumSiteMapURLEntriesPerFile) {
        this.maximumURLEntriesPerFile = maximumSiteMapURLEntriesPerFile;
    }

    @Override
    public String fixSiteUrlPath(String siteUrlPath) {
        if (siteUrlPath == null) {
            return siteUrlPath;
        }
        if (siteUrlPath.endsWith("/")) {
            return siteUrlPath.substring(0, siteUrlPath.length() - 1);
        }
        return siteUrlPath;
    }

    @Override
    public String getSiteMapFileName() {
        if (StringUtils.isEmpty(siteMapFileName)) {
            return DEFAULT_SITE_MAP_FILE_NAME;
        } else {
            return siteMapFileName;
        }
    }

    @Override
    public void setSiteMapFileName(String siteMapFileName) {
        this.siteMapFileName = siteMapFileName;
    }

    @Override
    public String getIndexedSiteMapFileName() {
        if (StringUtils.isEmpty(indexedSiteMapFileName)) {
            return getSiteMapFileName();
        }
        return indexedSiteMapFileName;
    }

    @Override
    public void setIndexedSiteMapFileName(String fileName) {
        this.indexedSiteMapFileName = fileName;
    }

    @Override
    public String getSiteMapIndexFilePattern() {
        if (!StringUtils.isEmpty(indexedSiteMapFilePattern) &&
                indexedSiteMapFilePattern.contains("###")) {
            return indexedSiteMapFilePattern;
        }

        String siteMapFileName = getSiteMapFileName();
        int pos = siteMapFileName.indexOf(".xml");
        if (pos > 0) {
            return siteMapFileName.substring(0, pos) + "###" + siteMapFileName.substring(pos);
        } else {
            return "sitemap###.xml";
        }
    }

    @Override
    public void setIndexedSiteMapFilePattern(String indexedSiteMapFilePattern) {
        this.indexedSiteMapFilePattern = indexedSiteMapFilePattern;
    }
}
