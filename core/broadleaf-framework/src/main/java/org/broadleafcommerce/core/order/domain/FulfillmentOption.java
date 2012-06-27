/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.core.order.service.type.FulfillmentType;

import java.io.Serializable;

/**
 * 
 * TODO: document
 * @author Phillip Verheyden
 *
 */
public interface FulfillmentOption extends Serializable {
    
    public Long getId();
    
    public void setId(Long id);
    
    public String getName();

    public void setName(String name);

    public String getLongDescription();

    public void setLongDescription(String longDescription);

    public Boolean getUseFlatRates();

    public void setUseFlatRates(Boolean useFlatRates);
    
    public Boolean getAddFulfillmentFees();
    
    public void setAddFulfillmentFees(Boolean addFulfillmentFees);
    
    public FulfillmentType getFulfillmentType();

    public void setFulfillmentType(FulfillmentType fulfillmentType);
    
}
