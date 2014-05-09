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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author bpolster
 *
 */
@Entity
@Table(name = "BLC_PAGE_ITEM_CRITERIA")
@Inheritance(strategy=InheritanceType.JOINED)
@AdminPresentationClass(friendlyName = "PageItemCriteriaImpl_basePageItemCriteria")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true)
})
public class PageItemCriteriaImpl implements PageItemCriteria {
    
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "PageItemCriteriaId")
    @GenericGenerator(
        name="PageItemCriteriaId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="PageItemCriteriaImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.page.domain.PageItemCriteriaImpl")
        }
    )
    @Column(name = "PAGE_ITEM_CRITERIA_ID")
    @AdminPresentation(friendlyName = "PageItemCriteriaImpl_Item_Criteria_Id", group = "PageItemCriteriaImpl_Description", visibility =VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "PageItemCriteriaImpl_Quantity", group = "PageItemCriteriaImpl_Description", visibility =VisibilityEnum.HIDDEN_ALL)
    protected Integer quantity;
    
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "ORDER_ITEM_MATCH_RULE", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "PageItemCriteriaImpl_Order_Item_Match_Rule", group = "PageItemCriteriaImpl_Description", visibility = VisibilityEnum.HIDDEN_ALL)
    protected String orderItemMatchRule;
    
    @ManyToOne(targetEntity = PageImpl.class)
    @JoinTable(name = "BLC_QUAL_CRIT_PAGE_XREF", joinColumns = @JoinColumn(name = "PAGE_ITEM_CRITERIA_ID"), inverseJoinColumns = @JoinColumn(name = "PAGE_ID"))
    protected Page page;
    
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
    public Page getPage() {
        return page;
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
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
        PageItemCriteriaImpl other = (PageItemCriteriaImpl) obj;
        
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
    public PageItemCriteria cloneEntity() {
        PageItemCriteriaImpl newField = new PageItemCriteriaImpl();
        newField.quantity = quantity;
        newField.orderItemMatchRule = orderItemMatchRule;

        return newField;
    }

}
