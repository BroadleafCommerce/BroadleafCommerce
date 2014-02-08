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

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * An example of the Serialized JSON:
 * (This is an example of a complex Item Rule)
 *
 * {'data': [
 *      {'id':'100',
 *      'quantity':'1',
 *      'groupOperator':'AND',
 *      'groups':[
 *          {'id':null,
 *          'quantity':null,
 *          'groupOperator':null,
 *          'groups':null,
 *          'name':'name',
 *          'operator':'IEQUALS',
 *          'value':'merchandise'}]},
 *      {'id':'200',
 *      'quantity':'2',
 *      'groupOperator':'AND',
 *      'groups':[
 *          {'id':null,
 *          'quantity':null,
 *          'groupOperator':null,
 *          'groups':null,
 *          'name':'retailPrice',
 *          'operator':'GREATER_THAN',
 *          'value':'20.00'}]}
 * ]}
 *
 */
public class DataWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    protected ArrayList<DataDTO> data = new ArrayList<DataDTO>();

    protected String error;
    protected String rawMvel;

    public ArrayList<DataDTO> getData() {
        return data;
    }

    public void setData(ArrayList<DataDTO> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRawMvel() {
        return rawMvel;
    }

    public void setRawMvel(String rawMvel) {
        this.rawMvel = rawMvel;
    }

    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
