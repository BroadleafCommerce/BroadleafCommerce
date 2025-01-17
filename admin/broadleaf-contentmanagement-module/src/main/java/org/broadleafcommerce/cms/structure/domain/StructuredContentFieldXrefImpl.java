/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SC_FLD_MAP")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX,
                skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class StructuredContentFieldXrefImpl
        implements StructuredContentFieldXref, Serializable, ProfileEntity {

    /**
     * The Constant serialVersionUID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentFieldXrefId")
    @GenericGenerator(
            name = "StructuredContentFieldXrefId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "StructuredContentFieldXrefImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.cms.structure.domain.StructuredContentFieldXrefImpl")
            })
    @Column(name = "BLC_SC_SC_FIELD_ID")
    protected Long id;

    @ManyToOne(targetEntity = StructuredContentImpl.class, optional = false,
            cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SC_ID")
    @AdminPresentation(excluded = true)
    protected StructuredContent structuredContent;

    @ManyToOne(targetEntity = StructuredContentFieldImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "SC_FLD_ID")
    @ClonePolicy
    @AdminPresentation(fieldType = SupportedFieldType.FOREIGN_KEY)
    protected StructuredContentField structuredContentField;

    @Column(name = "MAP_KEY", nullable = false)
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String key;

    public StructuredContentFieldXrefImpl() {
        //Default constructor for JPA...
    }

    public StructuredContentFieldXrefImpl(StructuredContent sc,
                                          StructuredContentField scField,
                                          String key) {
        this.structuredContent = sc;
        this.structuredContentField = scField;
        this.key = key;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public StructuredContent getStructuredContent() {
        return structuredContent;
    }

    @Override
    public void setStructuredContent(StructuredContent sc) {
        this.structuredContent = sc;
    }

    @Override
    public void setStrucuturedContentField(StructuredContentField scField) {
        this.structuredContentField = scField;
    }

    @Override
    public StructuredContentField getStructuredContentField() {
        return structuredContentField;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public <G extends StructuredContentFieldXref> CreateResponse<G> createOrRetrieveCopyInstance(
            MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        StructuredContentFieldXref cloned = createResponse.getClone();
        cloned.setKey(key);
        if (structuredContent != null) {
            cloned.setStructuredContent(
                    structuredContent.createOrRetrieveCopyInstance(context).getClone());
        }
        if (structuredContentField != null) {
            CreateResponse<StructuredContentField> clonedFieldRsp = structuredContentField
                    .createOrRetrieveCopyInstance(context);
            cloned.setStrucuturedContentField(clonedFieldRsp.getClone());
        }
        return createResponse;
    }
}
