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

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.field.domain.FieldGroupImpl;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Phillip Verheyden (phillipuniverse)
 */
@Entity
@Table(name = "BLC_SC_FLDGRP_XREF")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
public class StructuredContentFieldGroupXrefImpl implements StructuredContentFieldGroupXref {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentFieldGroupXrefId")
    @GenericGenerator(
            name = "StructuredContentFieldGroupXrefId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value",
                            value = "StructuredContentFieldGroupXrefImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.cms.structure.domain.StructuredContentFieldGroupXrefImpl")
            }
    )
    @Column(name = "BLC_SC_FLDGRP_XREF_ID")
    protected Long id;

    @Column(name = "GROUP_ORDER")
    protected Integer groupOrder;

    @ManyToOne(targetEntity = StructuredContentFieldTemplateImpl.class)
    @JoinColumn(name = "SC_FLD_TMPLT_ID")
    protected StructuredContentFieldTemplate template;

    @ManyToOne(targetEntity = FieldGroupImpl.class)
    @JoinColumn(name = "FLD_GROUP_ID")
    protected FieldGroup fieldGroup;

    @Override
    public Integer getGroupOrder() {
        return groupOrder;
    }

    @Override
    public void setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
    }

    @Override
    public StructuredContentFieldTemplate getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(StructuredContentFieldTemplate template) {
        this.template = template;
    }

    @Override
    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    @Override
    public void setFieldGroup(FieldGroup fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    @Override
    public <G extends StructuredContentFieldGroupXref> CreateResponse<G> createOrRetrieveCopyInstance(
            MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }

        StructuredContentFieldGroupXref cloned = createResponse.getClone();
        cloned.setGroupOrder(groupOrder);

        CreateResponse<StructuredContentFieldTemplate> clonedTemplate =
                template.createOrRetrieveCopyInstance(context);
        cloned.setTemplate(clonedTemplate.getClone());

        CreateResponse<FieldGroup> clonedFieldGroup =
                fieldGroup.createOrRetrieveCopyInstance(context);
        cloned.setFieldGroup(clonedFieldGroup.getClone());

        return createResponse;
    }

}
