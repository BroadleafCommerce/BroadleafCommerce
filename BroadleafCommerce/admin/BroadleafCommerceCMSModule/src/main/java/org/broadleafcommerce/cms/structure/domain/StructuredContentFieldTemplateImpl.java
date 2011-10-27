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

import javax.persistence.*;

import java.util.List;

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.field.domain.FieldGroupImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.AdminPresentationClass;
import org.broadleafcommerce.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SC_FIELD_TEMPLATE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class StructuredContentFieldTemplateImpl implements StructuredContentFieldTemplate {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentFieldTemplateImpl", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StructuredContentFieldTemplateImpl", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StructuredContentFieldTemplateImpl", allocationSize = 10)
    @Column(name = "SC_FIELD_TEMPLATE_ID")
    protected Long id;

    @Column (name = "NAME")
    @AdminPresentation(friendlyName="Field Template Name", order=1, group="Details",prominent=true)
    protected String name;

    @ManyToMany(targetEntity = FieldGroupImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_SC_FLDGRP_XREF", joinColumns = @JoinColumn(name = "SC_FIELD_TEMPLATE_ID", referencedColumnName = "SC_FIELD_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "FIELD_GROUP_ID", referencedColumnName = "FIELD_GROUP_ID"))
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
    public List<FieldGroup> getFieldGroups() {
        return fieldGroups;
    }

    @Override
    public void setFieldGroups(List<FieldGroup> fieldGroups) {
        this.fieldGroups = fieldGroups;
    }
}

