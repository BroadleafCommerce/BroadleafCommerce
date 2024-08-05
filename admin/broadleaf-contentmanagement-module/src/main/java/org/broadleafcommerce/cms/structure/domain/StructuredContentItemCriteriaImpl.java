/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Table(name = "BLC_SC_ITEM_CRITERIA")
@Inheritance(strategy=InheritanceType.JOINED)
@AdminPresentationClass(friendlyName = "StructuredContentItemCriteriaImpl_baseStructuredContentItemCriteria")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class StructuredContentItemCriteriaImpl implements StructuredContentItemCriteria, ProfileEntity {
    
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "SCItemCriteriaId")
    @GenericGenerator(
        name="SCItemCriteriaId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="StructuredContentItemCriteriaImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.page.domain.StructuredContentItemCriteriaImpl")
        }
    )
    @Column(name = "SC_ITEM_CRITERIA_ID")
    @AdminPresentation(friendlyName = "StructuredContentItemCriteriaImpl_Item_Criteria_Id", group = "StructuredContentItemCriteriaImpl_Description", visibility =VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "StructuredContentItemCriteriaImpl_Quantity", group = "StructuredContentItemCriteriaImpl_Description", visibility =VisibilityEnum.HIDDEN_ALL)
    protected Integer quantity;
    
    @Lob
    @Type(type = "org.hibernate.type.MaterializedClobType")
    @Column(name = "ORDER_ITEM_MATCH_RULE", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "StructuredContentItemCriteriaImpl_Order_Item_Match_Rule", group = "StructuredContentItemCriteriaImpl_Description", visibility = VisibilityEnum.HIDDEN_ALL)
    protected String orderItemMatchRule;
    
    @ManyToOne(targetEntity = StructuredContentImpl.class)
    @JoinColumn(name = "SC_ID")
    protected StructuredContent structuredContent;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer receiveQuantity) {
        this.quantity = receiveQuantity;
    }

    @Override
    public String getMatchRule() {
        return orderItemMatchRule;
    }

    @Override
    public void setMatchRule(String matchRule) {
        this.orderItemMatchRule = matchRule;
    }

    @Override
    public StructuredContent getStructuredContent() {
        return structuredContent;
    }

    @Override
    public void setStructuredContent(StructuredContent structuredContent) {
        this.structuredContent = structuredContent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((orderItemMatchRule == null) ? 0 : orderItemMatchRule.hashCode());
        result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        StructuredContentItemCriteriaImpl other = (StructuredContentItemCriteriaImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (orderItemMatchRule == null) {
            if (other.orderItemMatchRule != null)
                return false;
        } else if (!orderItemMatchRule.equals(other.orderItemMatchRule))
            return false;
        if (quantity == null) {
            if (other.quantity != null)
                return false;
        } else if (!quantity.equals(other.quantity))
            return false;
        return true;
    }

    @Override
    public StructuredContentItemCriteria cloneEntity() {
        StructuredContentItemCriteriaImpl newField = new StructuredContentItemCriteriaImpl();
        newField.quantity = quantity;
        newField.orderItemMatchRule = orderItemMatchRule;

        return newField;
    }

    @Override
    public <G extends StructuredContentItemCriteria> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        StructuredContentItemCriteria cloned = createResponse.getClone();
        if (structuredContent != null) {
            cloned.setStructuredContent(structuredContent.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setMatchRule(orderItemMatchRule);
        cloned.setQuantity(quantity);
        return createResponse;
    }
}
