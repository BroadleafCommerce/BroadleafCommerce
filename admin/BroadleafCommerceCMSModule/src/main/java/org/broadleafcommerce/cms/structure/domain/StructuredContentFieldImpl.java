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

import org.broadleafcommerce.cms.field.domain.FieldData;
import org.broadleafcommerce.cms.field.domain.FieldDataImpl;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
public class StructuredContentFieldImpl implements StructuredContentField {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentFieldId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StructuredContentFieldId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StructuredContentFieldImpl", allocationSize = 10)
    @Column(name = "STRUCTURED_CONTENT_FIELD_ID")
    protected Long id;

    @Column (name = "FIELD_KEY")
    protected String fieldKey;

    @ManyToOne(targetEntity = StructuredContentImpl.class)
    @JoinColumn(name="STRUCTURED_CONTENT_ID")
    protected StructuredContent structuredContent;

    @OneToMany(targetEntity = FieldDataImpl.class)
    @JoinTable(name = "BLC_STRUCT_CONTENT_FIELD_DATA", joinColumns = @JoinColumn(name = "STRUCTURED_CONTENT_FIELD_ID"), inverseJoinColumns = @JoinColumn(name = "FIELD_DATA_ID"))
    @OrderColumn(name = "FIELD_ORDER")
    private List<FieldData> fieldDataList = new ArrayList<FieldData>();

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
    public List<FieldData> getFieldDataList() {
        return fieldDataList;
    }

    @Override
    public void setFieldDataList(List<FieldData> fieldDataList) {
        this.fieldDataList = fieldDataList;
    }
}

