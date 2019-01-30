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
package org.broadleafcommerce.common.site.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "BLC_SITE_CATALOG")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSiteElements")
@AdminPresentationClass(friendlyName = "SiteCatalogXrefImpl")
public class SiteCatalogXrefImpl implements SiteCatalogXref, AdminMainEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Use a separate primary key (rather than a composite key with @Embeddedid) to prevent
     * ConcurrentModificationException from Ehcache
     */
    @Id
    @GeneratedValue(generator = "SiteCatalogXrefId")
    @GenericGenerator(
        name="SiteCatalogXrefId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SiteCatalogXrefImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.site.domain.SiteCatalogXrefImpl")
        }
    )
    @Column(name = "SITE_CATALOG_XREF_ID")
    protected Long id;

    @ManyToOne(targetEntity = SiteImpl.class, optional = false)
    @JoinColumn(name = "SITE_ID")
    protected Site site = new SiteImpl();

    @ManyToOne(targetEntity = CatalogImpl.class, optional = false)
    @JoinColumn(name = "CATALOG_ID")
    protected Catalog catalog = new CatalogImpl();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public Catalog getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getMainEntityName() {
        return getCatalog().getName();
    }
}
