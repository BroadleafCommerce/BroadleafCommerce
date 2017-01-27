/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.openadmin.web.rulebuilder.dto;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * A temporary container object used to load the data into a RuleBuilderFieldService
 * @see org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService
 * @see org.broadleafcommerce.openadmin.web.rulebuilder.service.OrderItemFieldServiceImpl
 *
 *
 */
public class FieldData {

    protected String fieldLabel;
    protected String fieldName;
    protected String operators;
    protected String options;
    protected String selectizeSectionKey;
    protected String overrideEntityKey;
    protected String overrideDtoClassName;
    protected SupportedFieldType fieldType;
    protected SupportedFieldType secondaryFieldType;
    protected boolean skipValidation;

    private FieldData(Builder builder) {
        this.fieldLabel = builder.fieldLabel;
        this.fieldName = builder.fieldName;
        this.operators = builder.operators;
        this.options = builder.options;
        this.selectizeSectionKey = builder.selectizeSectionKey;
        this.overrideEntityKey = builder.overrideEntityKey;
        this.overrideDtoClassName = builder.overrideDtoClassName;
        this.fieldType = builder.fieldType;
        this.secondaryFieldType = builder.secondaryFieldType;
        this.skipValidation = builder.skipValidation;
    }

    public static class Builder {

        protected String fieldLabel = null;
        protected String fieldName = null;
        protected String operators = null;
        protected String options = null;
        protected String selectizeSectionKey = null;
        protected String overrideEntityKey = null;
        protected String overrideDtoClassName = null;
        protected SupportedFieldType fieldType = null;
        protected SupportedFieldType secondaryFieldType = null;
        protected boolean skipValidation;

        public FieldData build() {
            return new FieldData(this);
        }

        public Builder() {
        }

        public Builder label(String fieldLabel) {
            this.fieldLabel = fieldLabel;
            return this;
        }

        public Builder name(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder operators(String operators) {
            this.operators = operators;
            return this;
        }

        public Builder options(String options) {
            this.options = options;
            return this;
        }

        public Builder selectizeSectionKey(String selectizeSectionKey) {
            this.selectizeSectionKey = selectizeSectionKey;
            return this;
        }

        public Builder overrideEntityKey(String overrideEntityKey) {
            this.overrideEntityKey = overrideEntityKey;
            return this;
        }

        public Builder overrideDtoClassName(String overrideDtoClassName) {
            this.overrideDtoClassName = overrideDtoClassName;
            return this;
        }

        public Builder type(SupportedFieldType fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public Builder secondaryType(SupportedFieldType fieldType) {
            this.secondaryFieldType = fieldType;
            return this;
        }

        public Builder skipValidation(boolean skipValidation) {
            this.skipValidation = skipValidation;
            return this;
        }
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOperators() {
        return operators;
    }

    public String getOptions() {
        return options;
    }

    public String getSelectizeSectionKey() {
        return selectizeSectionKey;
    }

    public String getOverrideEntityKey() {
        return overrideEntityKey;
    }

    public String getOverrideDtoClassName() {
        return overrideDtoClassName;
    }

    public SupportedFieldType getFieldType() {
        return fieldType;
    }

    public SupportedFieldType getSecondaryFieldType() {
        return secondaryFieldType;
    }

    public boolean getSkipValidation() {
        return skipValidation;
    }

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

}
