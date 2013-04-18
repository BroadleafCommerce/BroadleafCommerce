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

package org.broadleafcommerce.openadmin.web.rulebuilder.service;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;

import java.util.ArrayList;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class AbstractRuleBuilderFieldService implements RuleBuilderFieldService{

    protected ArrayList<FieldData> fields = new ArrayList<FieldData>();

    @Override
    public FieldWrapper buildFields() {
        FieldWrapper wrapper = new FieldWrapper();

        for (FieldData field : getFields()) {
            FieldDTO fieldDTO = new FieldDTO();
            fieldDTO.setLabel(field.getFieldLabel());
            fieldDTO.setName(field.getFieldName());
            fieldDTO.setOperators(field.getOperators());
            fieldDTO.setOptions(field.getOptions());
            wrapper.getFields().add(fieldDTO);
        }

        return wrapper;
    }

    @Override
    public SupportedFieldType getSupportedFieldType(String fieldName) {
        SupportedFieldType type = null;
        if (fieldName != null) {
            for (FieldData field : getFields()) {
                if (fieldName.equals(field.getFieldName())){
                    return field.getFieldType();
                }
            }
        }
        return type;
    }

    @Override
    public SupportedFieldType getSecondaryFieldType(String fieldName) {
        SupportedFieldType type = null;
        if (fieldName != null) {
            for (FieldData field : getFields()) {
                if (fieldName.equals(field.getFieldName())){
                    return field.getSecondaryFieldType();
                }
            }
        }
        return type;
    }

    @Override
    public FieldDTO getField(String fieldName) {
        for (FieldData field : getFields()) {
            if (field.getFieldName().equals(fieldName)) {
                FieldDTO fieldDTO = new FieldDTO();
                fieldDTO.setLabel(field.getFieldLabel());
                fieldDTO.setName(field.getFieldName());
                fieldDTO.setOperators(field.getOperators());
                fieldDTO.setOptions(field.getOptions());
                return fieldDTO;
            }
        }
        return null;
    }

    @Override
    public ArrayList<FieldData> getFields() {
        return fields;
    }

    @Override
    public void setFields(ArrayList <FieldData> fields) {
        this.fields = fields;
    }

    public RuleBuilderFieldService clone() throws CloneNotSupportedException {
        try {
            RuleBuilderFieldService clone = this.getClass().newInstance();
            clone.setFields(this.fields);

            return clone;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
