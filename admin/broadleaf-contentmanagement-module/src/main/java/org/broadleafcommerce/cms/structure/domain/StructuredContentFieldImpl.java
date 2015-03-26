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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SC_FLD")
@EntityListeners(value = { AdminAuditableListener.class })
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class StructuredContentFieldImpl implements StructuredContentField, ProfileEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentFieldId")
    @GenericGenerator(
        name="StructuredContentFieldId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="StructuredContentFieldImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.structure.domain.StructuredContentFieldImpl")
        }
    )
    @Column(name = "SC_FLD_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @Column (name = "FLD_KEY")
    protected String fieldKey;

    @Column (name = "VALUE")
    protected String stringValue;

    @Column (name = "LOB_VALUE", length = Integer.MAX_VALUE - 1)
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    protected String lobValue;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getFieldKey() {
        return fieldKey;
    }

    @Override
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    @Override
    public String getValue() {
        if (stringValue != null && stringValue.length() > 0) {
            return stringValue;
        } else {
            return lobValue;
        }
    }

    @Override
    public void setValue(String value) {
        if (value != null) {
            if (value.length() <= 256) {
                stringValue = value;
                lobValue = null;
            } else {
                stringValue = null;
                lobValue = value;
            }
        } else {
            lobValue = null;
            stringValue = null;
        }
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
    public StructuredContentField clone() {
        StructuredContentField clone = null;

        try {
            clone = (StructuredContentField) Class.forName(this.getClass().getName()).newInstance();
            clone.setFieldKey(getFieldKey());
            clone.setValue(getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clone;
    }

    @Override
    public <G extends StructuredContentField> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        StructuredContentField cloned = createResponse.getClone();
        cloned.setFieldKey(fieldKey);
        cloned.setValue(this.getValue());
        return createResponse;
    }
}
