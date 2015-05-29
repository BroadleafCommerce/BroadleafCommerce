/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STATIC_ASSET_DESC")
@EntityListeners(value = { AdminAuditableListener.class })
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class StaticAssetDescriptionImpl implements StaticAssetDescription {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StaticAssetDescriptionId")
    @GenericGenerator(
        name="StaticAssetDescriptionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="StaticAssetDescriptionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.file.domain.StaticAssetDescriptionImpl")
        }
    )
    @Column(name = "STATIC_ASSET_DESC_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @Column (name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "StaticAssetDescriptionImpl_Description", prominent = true)
    protected String description;

    @Column (name = "LONG_DESCRIPTION")
    @AdminPresentation(friendlyName = "StaticAssetDescriptionImpl_Long_Description", largeEntry = true, visibility = VisibilityEnum.GRID_HIDDEN)
    protected String longDescription;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public StaticAssetDescription cloneEntity() {
        StaticAssetDescriptionImpl newAssetDescription = new StaticAssetDescriptionImpl();
        newAssetDescription.description = description;
        newAssetDescription.longDescription = longDescription;

        return newAssetDescription;
    }

    @Override
    public AdminAuditable getAuditable() {
        return auditable;
    }

    @Override
    public void setAuditable(AdminAuditable auditable) {
        this.auditable = auditable;
    }

    @Override
    public <G extends StaticAssetDescription> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        StaticAssetDescription cloned = createResponse.getClone();
        cloned.setDescription(description);
        cloned.setLongDescription(longDescription);
        return createResponse;
    }
}

