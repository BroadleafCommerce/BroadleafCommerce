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
@Table(name = "BLC_TEMPLATE_FIELD_GROUPS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class PageTemplateFieldGroupXrefImpl implements PageTemplateFieldGroupXref {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    PageTemplateFieldGroupXrefPk xref = new PageTemplateFieldGroupXrefPk();

    @Override
    public PageTemplateFieldGroupXrefPk getXref() {
		return xref;
	}
    
    @Override
    public void setXref(PageTemplateFieldGroupXrefPk xref) {
		this.xref = xref;
	}

	/** The display order. */
    @Column(name = "GROUP_ORDER")
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
    public PageTemplate getPageTemplate() {
		return xref.getPageTemplate();
	}

	@Override
    public void setPageTemplate(PageTemplate category) {
		xref.setPageTemplate(category);
	}

	@Override
    public FieldGroup getFieldGroup() {
		return xref.getFieldGroup();
	}

	@Override
    public void setFieldGroup(FieldGroup fieldGroup) {
		xref.setFieldGroup(fieldGroup);
	}

	public static class PageTemplateFieldGroupXrefPk implements Serializable{
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;
        
        @ManyToOne(targetEntity = PageTemplateImpl.class, optional=false)
        @JoinColumn(name = "PAGE_TEMPLATE_ID")
        protected PageTemplate pageTemplate = new PageTemplateImpl();
        
        /** The product. */
        @ManyToOne(targetEntity = FieldGroupImpl.class, optional=false)
        @JoinColumn(name = "FIELD_GROUP_ID")
        protected FieldGroup fieldGroup = new FieldGroupImpl();

		public PageTemplate getPageTemplate() {
			return pageTemplate;
		}

		public void setPageTemplate(PageTemplate pageTemplate) {
			this.pageTemplate = pageTemplate;
		}

		public FieldGroup getFieldGroup() {
			return fieldGroup;
		}

		public void setFieldGroup(FieldGroup fieldGroup) {
			this.fieldGroup = fieldGroup;
		}

		@Override
		public int hashCode() {
			return pageTemplate.hashCode() + fieldGroup.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
	        if (obj == null) return false;
	        else if (!(obj instanceof PageTemplateFieldGroupXrefPk)) return false;

	        return pageTemplate.getId().equals(((PageTemplateFieldGroupXrefPk) obj).pageTemplate.getId())
	        && fieldGroup.getId().equals(((PageTemplateFieldGroupXrefPk) obj).fieldGroup.getId());
		}
        
    }
    
}
