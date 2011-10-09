/*
 * Copyright 2008-2011 the original author or authors.
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

import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STRUCTURED_CONTENT_FIELD")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@EntityListeners(value = { AdminAuditableListener.class })
public class StructuredContentFieldImpl implements StructuredContentField {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentFieldId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StructuredContentFieldId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StructuredContentFieldImpl", allocationSize = 10)
    @Column(name = "STRUCTURED_CONTENT_FIELD_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @Column (name = "FIELD_KEY")
    protected String fieldKey;

    @ManyToOne(targetEntity = StructuredContentImpl.class)
    @JoinColumn(name="STRUCTURED_CONTENT_ID")
    protected StructuredContent structuredContent;

    @OneToMany(targetEntity = StructuredContentFieldDataImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_STRUCT_CNTNT_FLD_XREF", joinColumns = @JoinColumn(name = "STRUCTURED_CONTENT_FIELD_ID", referencedColumnName = "STRUCTURED_CONTENT_FIELD_ID"), inverseJoinColumns = @JoinColumn(name = "STRCTRD_CNTNT_FLD_DATA_ID", referencedColumnName = "STRCTRD_CNTNT_FLD_DATA_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @OrderColumn(name = "FIELD_ORDER")
    @BatchSize(size = 20)
    private List<StructuredContentFieldData> fieldDataList = new ArrayList<StructuredContentFieldData>();

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
    public StructuredContent getStructuredContent() {
        return structuredContent;
    }

    @Override
    public void setStructuredContent(StructuredContent structuredContent) {
        this.structuredContent = structuredContent;
    }

    @Override
    public List<StructuredContentFieldData> getFieldDataList() {
        return fieldDataList;
    }

    @Override
    public void setFieldDataList(List<StructuredContentFieldData> fieldDataList) {
        this.fieldDataList = fieldDataList;
    }

    @Override
    public StructuredContentField cloneEntity() {
        StructuredContentFieldImpl newContentField = new StructuredContentFieldImpl();
        newContentField.fieldKey = fieldKey;
        newContentField.structuredContent = structuredContent;

        for (StructuredContentFieldData oldFieldData: fieldDataList) {
            StructuredContentFieldData newFieldData = oldFieldData.cloneEntity();
            newContentField.fieldDataList.add(newFieldData);
        }
        return newContentField;

    }

    @Override
    public String getValue() {
        if (fieldDataList != null && fieldDataList.size() >= 1) {
            return fieldDataList.get(0).getValue();
        }
        return null;
    }

    public AdminAuditable getAuditable() {
        return auditable;
    }

    public void setAuditable(AdminAuditable auditable) {
        this.auditable = auditable;
    }
}

