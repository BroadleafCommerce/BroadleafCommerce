/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.field.domain.FieldGroupImpl;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PGTMPLT_FLDGRP_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blCMSElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class PageTemplateFieldGroupXrefImpl implements PageTemplateFieldGroupXref, ProfileEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageTemplateFieldGroupXrefId")
    @GenericGenerator(
            name = "PageTemplateFieldGroupXrefId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "PageTemplateFieldGroupXrefImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.cms.page.domain.PageTemplateFieldGroupXrefImpl")
            })
    @Column(name = "PG_TMPLT_FLD_GRP_ID")
    protected Long id;

    @ManyToOne(targetEntity = PageTemplateImpl.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PAGE_TMPLT_ID")
    @AdminPresentation(excluded = true)
    protected PageTemplate pageTemplate;

    @ManyToOne(targetEntity = FieldGroupImpl.class, cascade = { CascadeType.ALL })
    @JoinColumn(name = "FLD_GROUP_ID")
    @ClonePolicy
    protected FieldGroup fieldGroup;

    @Column(name = "GROUP_ORDER", precision = 10, scale = 6)
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected BigDecimal groupOrder;

    public PageTemplateFieldGroupXrefImpl() {
        //Default constructor for JPA
    }

    public PageTemplateFieldGroupXrefImpl(PageTemplate pageTemplate, FieldGroup fieldGroup) {
        this.pageTemplate = pageTemplate;
        this.fieldGroup = fieldGroup;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    @Override
    public PageTemplate getPageTemplate() {
        return pageTemplate;
    }

    @Override
    public void setFieldGroup(FieldGroup fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    @Override
    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    @Override
    public void setGroupOrder(BigDecimal groupOrder) {
        this.groupOrder = groupOrder;
    }

    @Override
    public BigDecimal getGroupOrder() {
        return groupOrder;
    }

    @Override
    public <G extends PageTemplateFieldGroupXref> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        PageTemplateFieldGroupXref cloned = createResponse.getClone();
        if (pageTemplate != null) {
            cloned.setPageTemplate(pageTemplate.createOrRetrieveCopyInstance(context).getClone());
        }
        if (fieldGroup != null) {
            cloned.setFieldGroup(fieldGroup.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setGroupOrder(groupOrder);
        return createResponse;
    }
}
