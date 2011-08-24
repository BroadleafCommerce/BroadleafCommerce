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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.cms.field.domain.FieldData;
import org.broadleafcommerce.cms.field.domain.FieldDataImpl;
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
@Table(name = "BLC_PAGE_FIELD")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class PageFieldImpl implements PageField {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageFieldId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PageFieldId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PageFieldImpl", allocationSize = 10)
    @Column(name = "PAGE_FIELD_ID")
    protected Long id;

    @Column (name = "FIELD_KEY")
    protected String fieldKey;

    @ManyToOne(targetEntity = PageImpl.class)
    @JoinColumn(name="PAGE_ID")
    protected Page page;

    @OneToMany(targetEntity = FieldDataImpl.class)
    @JoinTable(name = "BLC_PAGE_FIELD_DATA", joinColumns = @JoinColumn(name = "PAGE_FIELD_ID"), inverseJoinColumns = @JoinColumn(name = "FIELD_DATA_ID"))
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
    public Page getPage() {
        return page;
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public List<FieldData> getFieldDataList() {
        return fieldDataList;
    }

    @Override
    public void addFieldData(FieldData fieldData) {
        if (! fieldDataList.contains(fieldData)) {
              fieldDataList.add(fieldData);
        }
    }

    @Override
    public void setFieldDataList(List<FieldData> fieldDataList) {
        this.fieldDataList = fieldDataList;
    }

    @Override
    public PageField cloneEntity() {
        PageFieldImpl newPageField = new PageFieldImpl();
        newPageField.fieldKey = fieldKey;
        newPageField.page = page;

        for (FieldData oldPageData: fieldDataList) {
            FieldData newFieldData = oldPageData.cloneEntity();
            newPageField.fieldDataList.add(newFieldData);
        }
        return newPageField;
    }

}

