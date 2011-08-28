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
package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author jfischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FIELD_GROUP_FIELDS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class FieldGroupFieldDefinitionXrefImpl implements FieldGroupFieldDefinitionXref {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    FieldGroupFieldDefinitionXrefPk xref = new FieldGroupFieldDefinitionXrefPk();

    @Override
    public FieldGroupFieldDefinitionXrefPk getXref() {
		return xref;
	}
    
    @Override
    public void setXref(FieldGroupFieldDefinitionXrefPk xref) {
		this.xref = xref;
	}

	/** The display order. */
    @Column(name = "FIELD_ORDER")
    protected Long displayOrder;

    @Override
    public Long getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

	@Override
    public FieldGroup getFieldGroup() {
		return xref.getFieldGroup();
	}

	@Override
    public void setFieldGroup(FieldGroup fieldGroup) {
		xref.setFieldGroup(fieldGroup);
	}

	@Override
    public FieldDefinition getFieldDefinition() {
		return xref.getFieldDefinition();
	}

	@Override
    public void setFieldDefinition(FieldDefinition fieldDefinition) {
		xref.setFieldDefinition(fieldDefinition);
	}

	public static class FieldGroupFieldDefinitionXrefPk implements Serializable{
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;
        
        @ManyToOne(targetEntity = FieldGroupImpl.class, optional=false)
        @JoinColumn(name = "FIELD_GROUP_ID")
        protected FieldGroup fieldGroup = new FieldGroupImpl();
        
        /** The product. */
        @ManyToOne(targetEntity = FieldDefinitionImpl.class, optional=false)
        @JoinColumn(name = "FIELD_DEFINITION_ID")
        protected FieldDefinition fieldDefinition = new FieldDefinitionImpl();

		public FieldGroup getFieldGroup() {
			return fieldGroup;
		}

		public void setFieldGroup(FieldGroup fieldGroup) {
			this.fieldGroup = fieldGroup;
		}

		public FieldDefinition getFieldDefinition() {
			return fieldDefinition;
		}

		public void setFieldDefinition(FieldDefinition fieldDefinition) {
			this.fieldDefinition = fieldDefinition;
		}

		@Override
		public int hashCode() {
			return fieldGroup.hashCode() + fieldDefinition.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
	        if (obj == null) return false;
	        else if (!(obj instanceof FieldGroupFieldDefinitionXrefPk)) return false;

	        return fieldGroup.getId().equals(((FieldGroupFieldDefinitionXrefPk) obj).fieldGroup.getId())
	        && fieldDefinition.getId().equals(((FieldGroupFieldDefinitionXrefPk) obj).fieldDefinition.getId());
		}
        
    }
    
}
