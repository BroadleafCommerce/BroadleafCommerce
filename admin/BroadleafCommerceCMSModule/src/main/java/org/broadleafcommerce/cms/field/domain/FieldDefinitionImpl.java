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
package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FIELD_DEFINITION")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class FieldDefinitionImpl implements FieldDefinition {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FieldDefinitionId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FieldDefinitionId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FieldDefinitionImpl", allocationSize = 10)
    @Column(name = "FIELD_DEFINITION_ID")
    protected Long id;

    @Column (name = "NAME")
    protected String name;

    @Column (name = "FIELD_TYPE")
    protected String fieldType;

    @Column (name = "SECURITY_LEVEL")
    protected String securityLevel;

    @Column (name = "HIDDEN_FLAG")
    protected Boolean hiddenFlag = false;

    @Column (name = "VALIDATION_REGEX")
    protected String validationRegEx;

    @Column (name = "MAX_LENGTH")
    protected Integer maxLength;

    @Column (name = "COLUMN_WIDTH")
    protected Integer columnWidth;

    @Column (name = "TEXT_AREA_FLAG")
    protected Boolean textAreaFlag = false;

    @Column (name = "ENUMERATION_NAME")
    protected String enumerationName;

    @Column (name = "ALLOW_MULTIPLES")
    protected Boolean allowMultiples = false;

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
    public SupportedFieldType getFieldType() {
        return fieldType!=null?SupportedFieldType.valueOf(fieldType):null;
    }

    @Override
    public void setFieldType(SupportedFieldType fieldType) {
        this.fieldType = fieldType!=null?fieldType.toString():null;
    }

    @Override
    public String getSecurityLevel() {
        return securityLevel;
    }

    @Override
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    @Override
    public Boolean getHiddenFlag() {
        return hiddenFlag;
    }

    @Override
    public void setHiddenFlag(Boolean hiddenFlag) {
        this.hiddenFlag = hiddenFlag;
    }

    @Override
    public String getValidationRegEx() {
        return validationRegEx;
    }

    @Override
    public void setValidationRegEx(String validationRegEx) {
        this.validationRegEx = validationRegEx;
    }

    @Override
    public Integer getMaxLength() {
        return maxLength;
    }

    @Override
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public Integer getColumnWidth() {
        return columnWidth;
    }

    @Override
    public void setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
    }

    @Override
    public Boolean getTextAreaFlag() {
        return textAreaFlag;
    }

    @Override
    public void setTextAreaFlag(Boolean textAreaFlag) {
        this.textAreaFlag = textAreaFlag;
    }

    @Override
    public String getEnumerationName() {
        return enumerationName;
    }

    @Override
    public void setEnumerationName(String enumerationName) {
        this.enumerationName = enumerationName;
    }

    @Override
    public Boolean getAllowMultiples() {
        return allowMultiples;
    }

    @Override
    public void setAllowMultiples(Boolean allowMultiples) {
        this.allowMultiples = allowMultiples;
    }
}

