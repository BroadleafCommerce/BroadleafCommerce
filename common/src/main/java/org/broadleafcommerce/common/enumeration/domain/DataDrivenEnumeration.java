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

package org.broadleafcommerce.common.enumeration.domain;

import java.io.Serializable;
import java.util.List;

public interface DataDrivenEnumeration extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    public String getKey();

    public void setKey(String key);

    public Boolean getModifiable();

    public void setModifiable(Boolean modifiable);

    public List<DataDrivenEnumerationValue> getEnumValues();

    public void setEnumValues(List<DataDrivenEnumerationValue> enumValues);

    @Deprecated
    public List<DataDrivenEnumerationValue> getOrderItems();

    @Deprecated
    public void setOrderItems(List<DataDrivenEnumerationValue> orderItems);

}
