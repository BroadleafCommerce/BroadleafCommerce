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
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FIELD_GROUP")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class FieldGroupImpl implements FieldGroup {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FieldGroupId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FieldGroupId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FieldGroupImpl", allocationSize = 10)
    @Column(name = "FIELD_GROUP_ID")
    protected Long id;

    @Column (name = "NAME")
    protected String name;

    @Column (name = "INIT_COLLAPSED_FLAG")
    protected Boolean initCollapsedFlag = false;

    @OneToMany(mappedBy = "fieldGroup", targetEntity = FieldDefinitionImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @OrderColumn(name = "FIELD_ORDER")
    @BatchSize(size = 20)
    protected List<FieldDefinition> fieldDefinitions;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean getInitCollapsedFlag() {
        return initCollapsedFlag;
    }

    @Override
    public void setInitCollapsedFlag(Boolean initCollapsedFlag) {
        this.initCollapsedFlag = initCollapsedFlag;
    }

    @Override
    public List<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions;
    }

    @Override
    public void setFieldDefinitions(List<FieldDefinition> fieldDefinitions) {
        this.fieldDefinitions = fieldDefinitions;
    }

}

