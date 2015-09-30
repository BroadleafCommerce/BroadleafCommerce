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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SC_FLD_TMPLT")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "StructuredContentFieldTemplateImpl_baseStructuredContentFieldTemplate")
public class StructuredContentFieldTemplateImpl implements StructuredContentFieldTemplate {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentFieldTemplateId")
    @GenericGenerator(
        name="StructuredContentFieldTemplateId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="StructuredContentFieldTemplateImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.structure.domain.StructuredContentFieldTemplateImpl")
        }
    )
    @Column(name = "SC_FLD_TMPLT_ID")
    protected Long id;

    @Column (name = "NAME")
    @AdminPresentation(friendlyName = "StructuredContentFieldTemplateImpl_Field_Template_Name", order = 1, gridOrder = 2, group = "StructuredContentFieldTemplateImpl_Details", prominent = true)
    protected String name;

    @OneToMany(targetEntity = StructuredContentFieldGroupXrefImpl.class, mappedBy = "template", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @OrderBy("groupOrder")
    @BatchSize(size = 20)
    protected List<StructuredContentFieldGroupXref> fieldGroupXrefs = new ArrayList<StructuredContentFieldGroupXref>();

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
        Collection<FieldGroup> transformed = CollectionUtils.collect(fieldGroupXrefs, new Transformer<StructuredContentFieldGroupXref, FieldGroup>() {

            @Override
            public FieldGroup transform(StructuredContentFieldGroupXref input) {
                return input.getFieldGroup();
            }
        });
        
        return Collections.unmodifiableList(new ArrayList<FieldGroup>(transformed));
    }

    @Override
    public void setFieldGroups(List<FieldGroup> fieldGroups) {
        throw new UnsupportedOperationException("Not Supported - Use setAllFieldGroupXrefs()");
    }
    
    @Override
    public List<StructuredContentFieldGroupXref> getFieldGroupXrefs() {
        return fieldGroupXrefs;
    }

    @Override
    public void setFieldGroupXrefs(List<StructuredContentFieldGroupXref> fieldGroupXrefs) {
        this.fieldGroupXrefs = fieldGroupXrefs;
    }

    @Override
    public <G extends StructuredContentFieldTemplate> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        StructuredContentFieldTemplate cloned = createResponse.getClone();
        cloned.setName(name);
        for (StructuredContentFieldGroupXref entry : fieldGroupXrefs) {
            CreateResponse<StructuredContentFieldGroupXref> clonedGroupRsp = entry.createOrRetrieveCopyInstance(context);
            cloned.getFieldGroupXrefs().add(clonedGroupRsp.getClone());
        }

        return createResponse;
    }

}

