/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FLD_DEF")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class FieldDefinitionImpl implements FieldDefinition {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FieldDefinitionId")
    @GenericGenerator(
        name="FieldDefinitionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FieldDefinitionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.field.domain.FieldDefinitionImpl")
        }
    )
    @Column(name = "FLD_DEF_ID")
    protected Long id;

    @Column (name = "NAME")
    protected String name;

    @Column (name = "FRIENDLY_NAME")
    protected String friendlyName;

    @Column (name = "FLD_TYPE")
    protected String fieldType;

    @Column (name = "SECURITY_LEVEL")
    protected String securityLevel;

    @Column (name = "HIDDEN_FLAG")
    protected Boolean hiddenFlag = false;

    @Column (name = "VLDTN_REGEX")
    protected String validationRegEx;

    @Column (name = "VLDTN_ERROR_MSSG_KEY")
    protected String validationErrorMesageKey;

    @Column (name = "MAX_LENGTH")
    protected Integer maxLength;

    @Column (name = "COLUMN_WIDTH")
    protected String columnWidth;

    @Column (name = "TEXT_AREA_FLAG")
    protected Boolean textAreaFlag = false;
    
    @Column(name = "REQUIRED_FLAG")
    protected Boolean requiredFlag = false;

    @ManyToOne (targetEntity = FieldEnumerationImpl.class)
    @JoinColumn(name = "FLD_ENUM_ID")
    protected FieldEnumeration fieldEnumeration;

    @Column (name = "ALLOW_MULTIPLES")
    protected Boolean allowMultiples = false;

    @ManyToOne(targetEntity = FieldGroupImpl.class)
    @JoinColumn(name = "FLD_GROUP_ID")
    protected FieldGroup fieldGroup;

    @Column(name="FLD_ORDER")
    protected int fieldOrder;

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
    public String getColumnWidth() {
        return columnWidth;
    }

    @Override
    public void setColumnWidth(String columnWidth) {
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
    public Boolean getRequiredFlag() {
        return requiredFlag;
    }

    @Override
    public void setRequiredFlag(Boolean requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    @Override
    public Boolean getAllowMultiples() {
        return allowMultiples;
    }

    @Override
    public void setAllowMultiples(Boolean allowMultiples) {
        this.allowMultiples = allowMultiples;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String getValidationErrorMesageKey() {
        return validationErrorMesageKey;
    }

    @Override
    public void setValidationErrorMesageKey(String validationErrorMesageKey) {
        this.validationErrorMesageKey = validationErrorMesageKey;
    }

    @Override
    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    @Override
    public void setFieldGroup(FieldGroup fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    @Override
    public int getFieldOrder() {
        return fieldOrder;
    }

    @Override
    public void setFieldOrder(int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    @Override
    public FieldEnumeration getFieldEnumeration() {
        return fieldEnumeration;
    }

    @Override
    public void setFieldEnumeration(FieldEnumeration fieldEnumeration) {
        this.fieldEnumeration = fieldEnumeration;
    }
}

