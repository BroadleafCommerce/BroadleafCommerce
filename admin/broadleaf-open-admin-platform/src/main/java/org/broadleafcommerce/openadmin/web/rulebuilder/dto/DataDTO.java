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

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class DataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Long id;

    protected Integer quantity;

    protected String groupOperator;

    protected ArrayList<DataDTO> groups = new ArrayList<DataDTO>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getGroupOperator() {
        return groupOperator;
    }

    public void setGroupOperator(String groupOperator) {
        this.groupOperator = groupOperator;
    }

    public ArrayList<DataDTO> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<DataDTO> groups) {
        this.groups = groups;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            DataDTO that = (DataDTO) obj;
            return new EqualsBuilder()
                .append(id, that.id)
                .append(quantity, that.quantity)
                .append(groupOperator, that.groupOperator)
                .append(groups.toArray(), that.groups.toArray())
                .build();
        }
        return false;
    }
}
