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
    protected SupportedFieldType fieldType;
    protected SupportedFieldType secondaryFieldType;

    private FieldData(Builder builder) {
        this.fieldLabel = builder.fieldLabel;
        this.fieldName = builder.fieldName;
        this.operators = builder.operators;
        this.options = builder.options;
        this.fieldType = builder.fieldType;
        this.secondaryFieldType = builder.secondaryFieldType;
    }

    public static class Builder {
        protected String fieldLabel = null;
        protected String fieldName = null;
        protected String operators = null;
        protected String options = null;
        protected SupportedFieldType fieldType = null;
        protected SupportedFieldType secondaryFieldType = null;

        public FieldData build() {
            return new FieldData(this);
        }

        public Builder() {}

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

        public Builder type(SupportedFieldType fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public Builder secondaryType(SupportedFieldType fieldType) {
            this.secondaryFieldType = fieldType;
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

    public SupportedFieldType getFieldType() {
        return fieldType;
    }

    public SupportedFieldType getSecondaryFieldType() {
        return secondaryFieldType;
    }
}
