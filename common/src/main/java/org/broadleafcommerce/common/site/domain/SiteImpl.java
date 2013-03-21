/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.common.site.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.site.service.type.SiteResolutionType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SITE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@AdminPresentationClass(friendlyName = "baseSite")
public class SiteImpl implements Site {

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(SiteImpl.class);

    @Id
    @GeneratedValue(generator = "SiteId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SiteId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SiteImpl", allocationSize = 10)
    @Column(name = "SITE_ID")
    protected Long id;

    @Column (name = "NAME")
    @AdminPresentation(friendlyName = "SiteImpl_Site_Name", order=1, gridOrder = 1, group = "SiteImpl_Site", prominent = true, requiredOverride = RequiredOverride.REQUIRED)
    protected String name;

    @Column (name = "SITE_IDENTIFIER_TYPE")
    @AdminPresentation(friendlyName = "SiteImpl_Site_Identifier_Type", order=2, gridOrder = 2, group = "SiteImpl_Site", prominent = true, broadleafEnumeration = "org.broadleafcommerce.common.site.service.type.SiteResolutionType", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, requiredOverride = RequiredOverride.REQUIRED)
    protected String siteIdentifierType;

    @Column (name = "SITE_IDENTIFIER_VALUE")
    @AdminPresentation(friendlyName = "SiteImpl_Site_Identifier_Value", order=3, gridOrder = 3, group = "SiteImpl_Site", prominent = true, requiredOverride = RequiredOverride.REQUIRED)
    @Index(name = "BLC_SITE_ID_VAL_INDEX", columnNames = { "SITE_IDENTIFIER_VALUE" })
    protected String siteIdentifierValue;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "PRODUCTION_SANDBOX_ID")
    @AdminPresentation(friendlyName = "SiteImpl_Production_SandBox", visibility = VisibilityEnum.HIDDEN_ALL)
    protected SandBox productionSandbox;

    @ManyToMany(targetEntity = CatalogImpl.class, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "BLC_SITE_CATALOG", joinColumns = @JoinColumn(name = "SITE_ID"), inverseJoinColumns = @JoinColumn(name = "CATALOG_ID"))
    @BatchSize(size = 50)
    @AdminPresentationCollection(addType = AddMethodType.LOOKUP, friendlyName = "siteCatalogTitle", dataSourceName = "siteCatalogDS", manyToField = "sites")
    protected List<Catalog> catalogs = new ArrayList<Catalog>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSiteIdentifierType() {
        return siteIdentifierType;
    }

    @Override
    public void setSiteIdentifierType(String siteIdentifierType) {
        this.siteIdentifierType = siteIdentifierType;
    }

    @Override
    public String getSiteIdentifierValue() {
        return siteIdentifierValue;
    }

    @Override
    public void setSiteIdentifierValue(String siteIdentifierValue) {
        this.siteIdentifierValue = siteIdentifierValue;
    }

    @Override
    public SandBox getProductionSandbox() {
        return productionSandbox;
    }

    @Override
    public void setProductionSandbox(SandBox productionSandbox) {
        this.productionSandbox = productionSandbox;
    }

    @Override
    public SiteResolutionType getSiteResolutionType() {
        return SiteResolutionType.getInstance(siteIdentifierType);
    }

    @Override
    public void setSiteResolutionType(SiteResolutionType siteResolutionType) {
        this.siteIdentifierType = siteResolutionType.getType();
    }

    @Override
    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    @Override
    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    public void checkCloneable(Site site) throws CloneNotSupportedException, SecurityException, NoSuchMethodException {
        Method cloneMethod = site.getClass().getMethod("clone", new Class[]{});
        if (cloneMethod.getDeclaringClass().getName().startsWith("org.broadleafcommerce") && !site.getClass().getName().startsWith("org.broadleafcommerce")) {
            //subclass is not implementing the clone method
            throw new CloneNotSupportedException("Custom extensions and implementations should implement clone.");
        }
    }

    @Override
    public Site clone() {
        Site clone;
        try {
            clone = (Site) Class.forName(this.getClass().getName()).newInstance();
            try {
                checkCloneable(clone);
            } catch (CloneNotSupportedException e) {
                LOG.warn("Clone implementation missing in inheritance hierarchy outside of Broadleaf: " + clone.getClass().getName(), e);
            }
            clone.setId(id);
            clone.setName(name);

            for (Catalog catalog : getCatalogs()) {
                Catalog cloneCatalog = new CatalogImpl();
                cloneCatalog.setId(catalog.getId());
                cloneCatalog.setName(catalog.getName());
                clone.getCatalogs().add(cloneCatalog);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clone;
    }
}

