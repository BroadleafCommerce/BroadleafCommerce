/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.rulebuilder.dto;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * An example of the Serialized JSON:
 *
 * {'fields': [
 *      {'label':'Order Item - name',
 *      'name':'name',
 *      'operators':blcOperators_Text,
 *      'options':[]},
 *      {'label':'Order Item - Retail Price',
 *      'name':'retailPrice',
 *      'operators':blcOperators_Numeric,
 *      'options':[]},
 *      {'label':'Product - is Featured Product',
 *      'name':'sku.product.isFeaturedProduct',
 *      'operators':blcOperators_Boolean,
 *      'options':[]},
 *      {'label':'Sku - Active End Date',
 *      'name':'sku.activeEndDate',
 *      'operators':blcOperators_Date,
 *      'options':[]},
 *      {'label':'Category - Fulfillment Type',
 *      'name':'category.fulfillmentType',
 *      'operators':blcOperators_Enumeration,
 *      'options':blcOptions_FulfillmentType}
 * ]}
 *
 */
public class FieldWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    protected ArrayList<FieldDTO> fields = new ArrayList<FieldDTO>();

    public ArrayList<FieldDTO> getFields() {
        return fields;
    }

    public void setFields(ArrayList<FieldDTO> fields) {
        this.fields = fields;
    }
    
    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
