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
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATALOG")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blSiteElements")
@AdminPresentationClass(friendlyName = "CatalogImpl")
@SQLDelete(sql = "UPDATE BLC_CATALOG SET ARCHIVED = 'Y' WHERE CATALOG_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITEMARKER)
})
public class CatalogImpl implements Catalog, AdminMainEntity {

    private static final Log LOG = LogFactory.getLog(CatalogImpl.class);

    @Id
    @GeneratedValue(generator = "CatalogId")
    @GenericGenerator(
            name = "CatalogId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "CatalogImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.common.site.domain.CatalogImpl")
            })
    @Column(name = "CATALOG_ID")
    protected Long id;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "Catalog_Name", gridOrder = 1, order = 1, prominent = true, requiredOverride = RequiredOverride.REQUIRED, translatable = true)
    protected String name;

    @OneToMany(targetEntity = SiteCatalogXrefImpl.class, mappedBy = "catalog", orphanRemoval = true)
    @Cascade(value = { org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blSiteElements")
    @BatchSize(size = 50)
    protected List<SiteCatalogXref> siteXrefs = new ArrayList<SiteCatalogXref>();

    @Transient
    protected List<Site> sites = new ArrayList<Site>();

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
    public List<Site> getSites() {
        if (sites.isEmpty()) {
            for (SiteCatalogXref xref : siteXrefs) {
                sites.add(xref.getSite());
            }
        }
        return Collections.unmodifiableList(sites);
    }

    @Override
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    @Override
    public List<SiteCatalogXref> getSiteXrefs() {
        return siteXrefs;
    }

    @Override
    public void setSiteXrefs(List<SiteCatalogXref> siteXrefs) {
        this.siteXrefs = siteXrefs;
    }

    public void checkCloneable(Catalog catalog) throws CloneNotSupportedException, SecurityException, NoSuchMethodException {
        Method cloneMethod = catalog.getClass().getMethod("clone", new Class[] {});
        if (cloneMethod.getDeclaringClass().getName().startsWith("org.broadleafcommerce") && !catalog.getClass().getName().startsWith("org.broadleafcommerce")) {
            //subclass is not implementing the clone method
            throw new CloneNotSupportedException("Custom extensions and implementations should implement clone.");
        }
    }

    @Override
    public Catalog clone() {
        Catalog clone;
        try {
            clone = (Catalog) Class.forName(this.getClass().getName()).newInstance();
            try {
                checkCloneable(clone);
            } catch (CloneNotSupportedException e) {
                LOG.warn("Clone implementation missing in inheritance hierarchy outside of Broadleaf: " + clone.getClass().getName(), e);
            }
            clone.setId(id);
            clone.setName(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clone;
    }

    @Override
    public int compareTo(Catalog other){
        return name.compareTo(other.getName());
    }

    @Override
    public String getMainEntityName() {
        return getName();
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
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if ('Y' == getArchived()) {
                LOG.debug("catalog, " + id + ", inactive due to archived status");
            }
        }
        return 'Y' != getArchived();
    }
}
