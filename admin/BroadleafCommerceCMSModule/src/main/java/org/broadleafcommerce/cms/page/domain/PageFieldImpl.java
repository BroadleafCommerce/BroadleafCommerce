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

import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.broadleafcommerce.openadmin.audit.Auditable;
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
@Table(name = "BLC_PAGE_FIELD")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@EntityListeners(value = { AdminAuditableListener.class })
public class PageFieldImpl implements PageField {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageFieldId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PageFieldId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PageFieldImpl", allocationSize = 10)
    @Column(name = "PAGE_FIELD_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @Column (name = "FIELD_KEY")
    protected String fieldKey;

    @ManyToOne(targetEntity = PageImpl.class)
    @JoinColumn(name="PAGE_ID")
    protected Page page;

    @OneToMany(targetEntity = PageFieldDataImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_PGFLD_FLD_XREF", joinColumns = @JoinColumn(name = "PAGE_FIELD_ID", referencedColumnName = "PAGE_FIELD_ID"), inverseJoinColumns = @JoinColumn(name = "PAGE_FIELD_DATA_ID", referencedColumnName = "PAGE_FIELD_DATA_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @OrderColumn(name = "FIELD_ORDER")
    @BatchSize(size = 20)
    private List<PageFieldData> fieldDataList = new ArrayList<PageFieldData>();

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
    public List<PageFieldData> getFieldDataList() {
        return fieldDataList;
    }

    @Override
    public void addFieldData(PageFieldData fieldData) {
        if (! fieldDataList.contains(fieldData)) {
              fieldDataList.add(fieldData);
        }
    }

    @Override
    public void setFieldDataList(List<PageFieldData> fieldDataList) {
        this.fieldDataList = fieldDataList;
    }

    @Override
    public PageField cloneEntity() {
        PageFieldImpl newPageField = new PageFieldImpl();
        newPageField.fieldKey = fieldKey;
        newPageField.page = page;

        for (PageFieldData oldPageData: fieldDataList) {
            PageFieldData newFieldData = oldPageData.cloneEntity();
            newPageField.fieldDataList.add(newFieldData);
        }
        return newPageField;
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

