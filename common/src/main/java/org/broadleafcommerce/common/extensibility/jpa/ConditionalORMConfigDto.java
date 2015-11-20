/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

import java.io.Serializable;

public class ConditionalORMConfigDto implements Serializable {

    protected String puName;
    protected String conditionalProperty;

    public String getPuName() {
        return puName;
    }

    public void setPuName(String puName) {
        this.puName = puName;
    }

    public String getConditionalProperty() {
        return conditionalProperty;
    }

    public void setConditionalProperty(String conditionalProperty) {
        this.conditionalProperty = conditionalProperty;
    }
}
