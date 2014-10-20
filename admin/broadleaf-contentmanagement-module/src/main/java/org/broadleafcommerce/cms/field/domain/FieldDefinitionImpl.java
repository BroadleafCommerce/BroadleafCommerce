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
package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationImpl;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
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
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
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
    @AdminPresentation(fieldType = SupportedFieldType.HIDDEN)
    protected String name;

    @Column (name = "FRIENDLY_NAME")
    @AdminPresentation(friendlyName = "FieldDefinitionImpl_friendlyName", order = 2000, prominent = true, gridOrder = 2000)
    protected String friendlyName;

    @Column (name = "FLD_TYPE")
    @AdminPresentation(fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration = "org.broadleafcommerce.common.presentation.client.DynamicSupportedFieldType",
        prominent = true, gridOrder = 3000, order = 1000,
        friendlyName = "FieldDefinitionImpl_fieldType")
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

    @ManyToOne (targetEntity = DataDrivenEnumerationImpl.class)
    @JoinColumn(name = "ENUM_ID")
    protected DataDrivenEnumeration dataDrivenEnumeration;

    @Column (name = "ALLOW_MULTIPLES")
    protected Boolean allowMultiples = false;

    @ManyToOne(targetEntity = FieldGroupImpl.class)
    @JoinColumn(name = "FLD_GROUP_ID")
    protected FieldGroup fieldGroup;

    @Column(name="FLD_ORDER")
    @AdminPresentation(friendlyName = "FieldDefinitionImpl_fieldOrder", order = 3000)
    protected Integer fieldOrder = 0;

    @Column (name = "TOOLTIP")
    protected String tooltip;

    @Column (name = "HELP_TEXT")
    protected String helpText;

    @Column (name = "HINT")
    protected String hint;

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
        if (fieldType == null) {
            return null;
        }
        
        if (fieldType.startsWith(SupportedFieldType.ADDITIONAL_FOREIGN_KEY.toString() + '|')) {
            return SupportedFieldType.ADDITIONAL_FOREIGN_KEY;
        }
        
        return SupportedFieldType.valueOf(fieldType);
    }
    
    @Override
    public String getAdditionalForeignKeyClass() {
        if (fieldType == null || !fieldType.startsWith(SupportedFieldType.ADDITIONAL_FOREIGN_KEY.toString() + '|')) {
            return null;
        }
        
        return fieldType.substring(fieldType.indexOf('|') + 1);
    }
    
    @Override
    public void setAdditionalForeignKeyClass(String className) {
        if (fieldType == null || !SupportedFieldType.ADDITIONAL_FOREIGN_KEY.toString().equals(fieldType)) {
            throw new IllegalArgumentException("Cannot set an additional foreign key class when the field type is not ADDITIONAL_FOREIGN_KEY");
        }
        
        this.fieldType = SupportedFieldType.ADDITIONAL_FOREIGN_KEY.toString() + '|' + className;
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
        return hiddenFlag == null ? false : hiddenFlag;
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
        if (fieldOrder == null) {
            return 0;
        }
        return fieldOrder;
    }

    @Override
    public void setFieldOrder(int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    @Override
    public DataDrivenEnumeration getDataDrivenEnumeration() {
        return dataDrivenEnumeration;
    }

    @Override
    public void setDataDrivenEnumeration(DataDrivenEnumeration dataDrivenEnumeration) {
        this.dataDrivenEnumeration = dataDrivenEnumeration;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public String getHelpText() {
        return helpText;
    }

    @Override
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    @Override
    public String getHint() {
        return hint;
    }

    @Override
    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public <G extends FieldDefinition> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context)
            throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        FieldDefinition cloned = createResponse.getClone();
        cloned.setName(name);
        cloned.setFriendlyName(friendlyName);
        cloned.setFieldType(getFieldType());
        cloned.setSecurityLevel(securityLevel);
        cloned.setHiddenFlag(hiddenFlag);
        cloned.setValidationRegEx(validationRegEx);
        cloned.setValidationErrorMesageKey(validationErrorMesageKey);
        cloned.setMaxLength(maxLength);
        cloned.setColumnWidth(columnWidth);
        cloned.setTextAreaFlag(textAreaFlag);
        cloned.setRequiredFlag(requiredFlag);
        cloned.setDataDrivenEnumeration(dataDrivenEnumeration.createOrRetrieveCopyInstance(context).getClone());
        cloned.setAllowMultiples(allowMultiples);
        //don't clone fieldGroup - it will be replaced (if applicable) on the other side of the relationship
        cloned.setFieldGroup(fieldGroup);
        cloned.setFieldOrder(fieldOrder);
        cloned.setTooltip(tooltip);
        cloned.setHelpText(helpText);
        cloned.setHint(hint);
        return createResponse;
    }

}

