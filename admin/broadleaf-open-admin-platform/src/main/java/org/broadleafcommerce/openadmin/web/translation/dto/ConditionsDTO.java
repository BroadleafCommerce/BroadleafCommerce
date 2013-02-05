/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.translation.dto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * An example of the Serialized JSON:
 *  fields: [
 *      {label: "Name", name: "nameField", operators: [
 *      {label: "is present", name: "present", fieldType: "none"},
 *      {label: "is blank", name: "blank", fieldType: "none"},
 *      {label: "is equal to", name: "equalTo", fieldType: "text"},
 *      {label: "is not equal to", name: "notEqualTo", fieldType: "text"},
 *      {label: "includes", name: "includes", fieldType: "text"},
 *      {label: "matches regex", name: "matchesRegex", fieldType: "text"}
 *  ]},
 *      {label: "Age", name: "ageField", operators: [
 *      {label: "is present", name: "present", fieldType: "none"},
 *      {label: "is blank", name: "blank", fieldType: "none"},
 *      {label: "is equal to", name: "equalTo", fieldType: "text"},
 *      {label: "is not equal to", name: "notEqualTo", fieldType: "text"},
 *      {label: "is greater than", name: "greaterThan", fieldType: "text"},
 *      {label: "is greater than or equal to", name: "greaterThanEqual", fieldType: "text"},
 *      {label: "is less than", name: "lessThan", fieldType: "text"},
 *      {label: "is less than or equal to", name: "lessThanEqual", fieldType: "text"},
 *  ]},
 *      {label: "Occupation", name: "occupationField", options: occupationOptions, operators: [
 *      {label: "is present", name: "present", fieldType: "none"},
 *      {label: "is blank", name: "blank", fieldType: "none"},
 *      {label: "is equal to", name: "equalTo", fieldType: "select"},
 *      {label: "is not equal to", name: "notEqualTo", fieldType: "select"},
 *  ]}
 *],
 *  data: {"all": [
 *      {name: "nameField", operator: "equalTo", value: "Godzilla"},
 *      {name: "ageField", operator: "greaterThanEqual", value: "21"}
 *]}
 *
 */
public class ConditionsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected ArrayList<FieldDTO> fields = new ArrayList<FieldDTO>();

    protected DataDTO data;

    public ArrayList<FieldDTO> getFields() {
        return fields;
    }

    public void setFields(ArrayList<FieldDTO> fields) {
        this.fields = fields;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }
}
