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
package org.broadleafcommerce.common.site.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.site.service.type.SiteResolutionType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SITE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "baseSite")
@SQLDelete(sql="UPDATE BLC_SITE SET ARCHIVED = 'Y' WHERE SITE_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITEMARKER)
})
public class SiteImpl implements Site, AdminMainEntity {

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(SiteImpl.class);

    @Id
    @GeneratedValue(generator = "SiteId")
    @GenericGenerator(
        name="SiteId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SiteImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.site.domain.SiteImpl")
        }
    )
    @Column(name = "SITE_ID")
    protected Long id;

    @Column (name = "NAME")
    @AdminPresentation(friendlyName = "SiteImpl_Site_Name", order = 1000,
            gridOrder = 1, prominent = true, requiredOverride = RequiredOverride.REQUIRED, translatable = true)
    protected String name;

    @Column (name = "SITE_IDENTIFIER_TYPE")
    @AdminPresentation(friendlyName = "SiteImpl_Site_Identifier_Type", order = 2000,
            gridOrder = 2, prominent = true,
            broadleafEnumeration = "org.broadleafcommerce.common.site.service.type.SiteResolutionType", requiredOverride=RequiredOverride.REQUIRED,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION)
    protected String siteIdentifierType;

    @Column (name = "SITE_IDENTIFIER_VALUE")
    @AdminPresentation(friendlyName = "SiteImpl_Site_Identifier_Value", order = 3000,
            gridOrder = 3, prominent = true, requiredOverride=RequiredOverride.REQUIRED)
    @Index(name = "BLC_SITE_ID_VAL_INDEX", columnNames = { "SITE_IDENTIFIER_VALUE" })
    protected String siteIdentifierValue;

    @Column(name = "DEACTIVATED")
    @AdminPresentation(friendlyName = "SiteImpl_Deactivated", order = 4000,
            gridOrder = 4, excluded = false,
            defaultValue = "false")
    protected Boolean deactivated = false;
    
    @ManyToMany(targetEntity = CatalogImpl.class, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "BLC_SITE_CATALOG", joinColumns = @JoinColumn(name = "SITE_ID"), inverseJoinColumns = @JoinColumn(name = "CATALOG_ID"))
    @BatchSize(size = 50)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @AdminPresentation(excluded = true)
    protected List<Catalog> catalogs = new ArrayList<Catalog>();

    /**************************************************/
    /**
     * Adding additional properties to this class or dynamically weaving in properties will have to contribute to the extension
     * manager for {@link SiteServiceImpl}, {@link SiteServiceExtensionHandler}.
     */
    /**************************************************/

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();
    
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
        return DynamicTranslationProvider.getValue(this, "name", name);
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
    public SiteResolutionType getSiteResolutionType() {
        return siteIdentifierType == null ? null : SiteResolutionType.getInstance(siteIdentifierType);
    }

    @Override
    public void setSiteResolutionType(SiteResolutionType siteResolutionType) {
        this.siteIdentifierType = siteResolutionType == null ? null : siteResolutionType.getType();
    }

    @Override
    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    @Override
    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }
    
    @Override
    public Character getArchived() {
       ArchiveStatus temp;
       if (archiveStatus == null) {
           temp = new ArchiveStatus();
       } else {
           temp = archiveStatus;
       }
       return temp.getArchived();
    }

    @Override
    public void setArchived(Character archived) {
       if (archiveStatus == null) {
           archiveStatus = new ArchiveStatus();
       }
       archiveStatus.setArchived(archived);
    }
    
    @Override
    public ArchiveStatus getArchiveStatus() {
        return archiveStatus;
    }

    @Override
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (isDeactivated()) {
                LOG.debug("site, " + id + ", inactive due to deactivated property");
            }
            if ('Y'==getArchived()) {
                LOG.debug("site, " + id + ", inactive due to archived status");
            }
        }
        return !isDeactivated() && 'Y'!=getArchived();
    }

    @Override
    public boolean isDeactivated() {
        if (deactivated == null) {
            return false;
        } else {
            return deactivated;
        }
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
    
    @Override
    public boolean isTemplateSite() {
        return false;
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
            clone.setDeactivated(isDeactivated());
            clone.setSiteResolutionType(getSiteResolutionType());
            clone.setSiteIdentifierValue(getSiteIdentifierValue());
            ((Status) clone).setArchived(getArchived());

            if (getCatalogs() != null) {
                for (Catalog catalog : getCatalogs()) {
                    if (catalog != null) {
                        Catalog cloneCatalog = new CatalogImpl();

                        cloneCatalog.setId(catalog.getId());
                        cloneCatalog.setName(catalog.getName());
                        if (clone.getCatalogs() != null) {
                            clone.getCatalogs().add(cloneCatalog);
                        } else {
                            List<Catalog> cloneCatalogs = new ArrayList<Catalog>();
                            cloneCatalogs.add(cloneCatalog);
                            clone.setCatalogs(cloneCatalogs);
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clone;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }
}

