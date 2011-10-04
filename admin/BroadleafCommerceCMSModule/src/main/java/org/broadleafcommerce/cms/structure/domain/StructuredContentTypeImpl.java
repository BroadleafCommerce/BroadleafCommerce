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

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.field.domain.FieldGroupImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.AdminPresentationClass;
import org.broadleafcommerce.presentation.PopulateToOneFieldsEnum;
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
@Table(name = "BLC_STRUCTURED_CONTENT_TYPE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class StructuredContentTypeImpl implements StructuredContentType {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentTypeId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StructuredContentTypeId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StructuredContentTypeImpl", allocationSize = 10)
    @Column(name = "STRUCTURED_CONTENT_TYPE_ID")
    protected Long id;

    @Column (name = "NAME")
    @AdminPresentation(friendlyName="Name", order=1, group="Details", prominent=true)
    protected String name;

    @Column (name = "DESCRIPTION")
    @AdminPresentation(friendlyName="Description", order=2, group="Details", prominent=true)
    protected String description;

    @ManyToMany(targetEntity = FieldGroupImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_STRCTRDCNTNT_FLDGRP_XREF", joinColumns = @JoinColumn(name = "STRUCTURED_CONTENT_TYPE_ID", referencedColumnName = "STRUCTURED_CONTENT_TYPE_ID"), inverseJoinColumns = @JoinColumn(name = "FIELD_GROUP_ID", referencedColumnName = "FIELD_GROUP_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @OrderColumn(name = "GROUP_ORDER")
    @BatchSize(size = 20)
    protected List<FieldGroup> fieldGroups;

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
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<FieldGroup> getFieldGroups() {
        return fieldGroups;
    }

    @Override
    public void setFieldGroups(List<FieldGroup> fieldGroups) {
        this.fieldGroups = fieldGroups;
    }
}

