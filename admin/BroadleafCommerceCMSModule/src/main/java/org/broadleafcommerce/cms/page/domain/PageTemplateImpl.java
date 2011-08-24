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

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.field.domain.FieldGroupImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAGE_TEMPLATE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class PageTemplateImpl implements PageTemplate {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageTemplateId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PageTemplateId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PageTemplateImpl", allocationSize = 10)
    @Column(name = "PAGE_TEMPLATE_ID")
    protected Long id;

    @Column (name = "TEMPLATE_NAME")
    protected String templateName;

    @Column (name = "TEMPLATE_DESCRIPTION")
    protected String templateDescription;

    @Column (name = "TEMPLATE_PATH")
    protected String templatePath;

    @Column (name = "LANGUAGE_CODE")
    protected String languageCode="default";

    @OneToMany(targetEntity = FieldGroupImpl.class)
    @JoinTable(name = "BLC_TEMPLATE_FIELD_GROUPS", joinColumns = @JoinColumn(name = "PAGE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "FIELD_GROUP_ID", referencedColumnName = "FIELD_GROUP_ID"))
    @OrderColumn(name = "group_order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
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
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String getTemplateDescription() {
        return templateDescription;
    }

    @Override
    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }

    @Override
    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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

