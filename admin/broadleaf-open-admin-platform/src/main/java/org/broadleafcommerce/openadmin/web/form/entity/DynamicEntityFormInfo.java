
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

package org.broadleafcommerce.openadmin.web.form.entity;

public class DynamicEntityFormInfo {

    protected String criteriaName;
    protected String propertyName;
    protected String propertyValue;
    protected String ceilingClassName;
    
    public DynamicEntityFormInfo withCriteriaName(String criteriaName) {
        setCriteriaName(criteriaName);
        return this;
    }

    public DynamicEntityFormInfo withPropertyName(String propertyName) {
        setPropertyName(propertyName);
        return this;
    }
    
    public DynamicEntityFormInfo withPropertyValue(String propertyValue) {
        setPropertyValue(propertyValue);
        return this;
    }

    public DynamicEntityFormInfo withCeilingClassName(String ceilingClassName) {
        setCeilingClassName(ceilingClassName);
        return this;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getPropertyValue() {
        return propertyValue;
    }
    
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getCeilingClassName() {
        return ceilingClassName;
    }

    public void setCeilingClassName(String ceilingClassName) {
        this.ceilingClassName = ceilingClassName;
    }

}
