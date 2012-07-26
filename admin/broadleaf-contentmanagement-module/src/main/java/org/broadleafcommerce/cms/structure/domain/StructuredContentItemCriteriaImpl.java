/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.structure.domain;

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

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Table(name = "BLC_SC_ITEM_CRITERIA")
@Inheritance(strategy=InheritanceType.JOINED)
@AdminPresentationClass(friendlyName = "StructuredContentItemCriteriaImpl_baseStructuredContentItemCriteria")
public class StructuredContentItemCriteriaImpl implements StructuredContentItemCriteria {
	
	public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "SCItemCriteriaId")
    @GenericGenerator(
        name="SCItemCriteriaId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="StructuredContentItemCriteriaImpl"),
            @Parameter(name="increment_size", value="50"),
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
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "ORDER_ITEM_MATCH_RULE")
    @AdminPresentation(friendlyName = "StructuredContentItemCriteriaImpl_Order_Item_Match_Rule", group = "StructuredContentItemCriteriaImpl_Description", visibility = VisibilityEnum.HIDDEN_ALL)
	protected String orderItemMatchRule;
    
    @ManyToOne(targetEntity = StructuredContentImpl.class)
    @JoinTable(name = "BLC_QUAL_CRIT_SC_XREF", joinColumns = @JoinColumn(name = "SC_ITEM_CRITERIA_ID"), inverseJoinColumns = @JoinColumn(name = "SC_ID"))
    protected StructuredContent structuredContent;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.StructuredContentItemCriteria#getId()
	 */
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.StructuredContentItemCriteria#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.StructuredContentItemCriteria#getReceiveQuantity()
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.StructuredContentItemCriteria#setReceiveQuantity(java.lang.Integer)
	 */
	public void setQuantity(Integer receiveQuantity) {
		this.quantity = receiveQuantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.StructuredContentItemCriteria#getOrderItemMatchRule()
	 */
	public String getOrderItemMatchRule() {
		return orderItemMatchRule;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.StructuredContentItemCriteria#setOrderItemMatchRule(java.lang.String)
	 */
	public void setOrderItemMatchRule(String orderItemMatchRule) {
		this.orderItemMatchRule = orderItemMatchRule;
	}

    public StructuredContent getStructuredContent() {
        return structuredContent;
    }

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
		if (getClass() != obj.getClass())
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

    public StructuredContentItemCriteria cloneEntity() {
        StructuredContentItemCriteriaImpl newField = new StructuredContentItemCriteriaImpl();
        newField.quantity = quantity;
        newField.orderItemMatchRule = orderItemMatchRule;

        return newField;
    }

}
