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
package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 */
public class AdminExporterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String name;
    protected String friendlyName;
    protected List<Property> additionalCriteriaProperties;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFriendlyName() {
        return friendlyName;
    }
    
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }
    
    public List<Property> getAdditionalCriteriaProperties() {
        return additionalCriteriaProperties;
    }
    
    public void setAdditionalCriteriaProperties(List<Property> additionalCriteriaProperties) {
        this.additionalCriteriaProperties = additionalCriteriaProperties;
    }
    
    
}
