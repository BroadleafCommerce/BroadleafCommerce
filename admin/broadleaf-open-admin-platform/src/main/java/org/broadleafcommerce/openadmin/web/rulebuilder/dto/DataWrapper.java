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

import org.apache.commons.lang3.builder.EqualsBuilder;

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
 * (This is an example of a complex Item Rule)
 *
 * {'data': [
 *      {'pk':'100',
 *      'quantity':'1',
 *      'condition':'AND',
 *      'rules':[
 *          {'pk':null,
 *          'quantity':null,
 *          'condition':null,
 *          'rules':null,
 *          'id':'name',
 *          'operator':'IEQUALS',
 *          'value':'merchandise'}]},
 *      {'pk':'200',
 *      'quantity':'2',
 *      'condition':'AND',
 *      'rules':[
 *          {'pk':null,
 *          'quantity':null,
 *          'condition':null,
 *          'rules':null,
 *          'id':'retailPrice',
 *          'operator':'GREATER_THAN',
 *          'value':'20.00'}]}
 *      ],
 * "error":null,
 * "rawMvel":null}
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            DataWrapper that = (DataWrapper) obj;
            return new EqualsBuilder()
                .append(error, that.error)
                .append(rawMvel, that.rawMvel)
                .append(data.toArray(), that.data.toArray())
                .build();
        }
        return false;
    }
}
