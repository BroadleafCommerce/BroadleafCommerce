/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Chad Harchar (charchar)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_INDEX_FIELD")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.ARCHIVE_ONLY)
})
public class IndexFieldImpl implements IndexField, Serializable, IndexFieldAdminPresentation, AdminMainEntity {

    private static final long serialVersionUID = 2915813511754425605L;

    @Id
    @GeneratedValue(generator = "IndexFieldId")
    @GenericGenerator(
            name="IndexFieldId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="IndexFieldImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.IndexFieldImpl")
            }
    )
    @Column(name = "INDEX_FIELD_ID")
    @AdminPresentation(friendlyName = "IndexFieldImpl_ID", group = "IndexFieldImpl_description",
            visibility= VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "SEARCHABLE")
    @AdminPresentation(friendlyName = "IndexFieldImpl_searchable",
            defaultValue = "false",
            prominent = true,
            group = GroupName.General,
            tooltip = "IndexFieldImpl_searchable_tooltip")
    @Index(name="INDEX_FIELD_SEARCHABLE_INDEX", columnNames={"SEARCHABLE"})
    protected Boolean searchable;

    @ManyToOne(optional=false, targetEntity = FieldImpl.class)
    @JoinColumn(name = "FIELD_ID")
    @AdminPresentation(friendlyName = "IndexFieldImpl_field", order = 1000, group = GroupName.General,
            prominent = true, gridOrder = 1000)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "friendlyName", customCriteria = { "fieldImplOnly" })
    protected Field field;

    @OneToMany(mappedBy = "indexField", targetEntity = IndexFieldTypeImpl.class, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @Where(clause = "(ARCHIVED != 'Y' OR ARCHIVED IS NULL)")
    @AdminPresentationCollection(friendlyName = "IndexFieldImpl_fieldTypes", order = 1000)
    protected List<IndexFieldType> fieldTypes = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Boolean getSearchable() {
        return searchable;
    }

    @Override
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
    
    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public List<IndexFieldType> getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public void setFieldTypes(List<IndexFieldType> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    @Override
    public <G extends IndexField> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        IndexField cloned = createResponse.getClone();
        cloned.setSearchable(searchable);
        cloned.setField(field);
        for(IndexFieldType entry : fieldTypes){
            cloned.getFieldTypes().add(entry.createOrRetrieveCopyInstance(context).getClone());
        }

        return createResponse;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            IndexFieldImpl other = (IndexFieldImpl) obj;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(field, other.field)
                    .build();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31)
                .append(id)
                .append(field)
                .toHashCode();
    }

    @Override
    public String getMainEntityName() {
        return getField().getFriendlyName();
    }
}
