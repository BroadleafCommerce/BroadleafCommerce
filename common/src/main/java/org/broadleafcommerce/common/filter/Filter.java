/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.filter;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration bean that represents a Hibernate filter.
 *
 * @author Jeff Fischer
 */
public class Filter {

    String name;
    String condition;
    String entityImplementationClassName;
    List<String> indexColumnNames;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityImplementationClassName() {
        return entityImplementationClassName;
    }

    public void setEntityImplementationClassName(String entityImplementationClassName) {
        this.entityImplementationClassName = entityImplementationClassName;
    }

    public List<String> getIndexColumnNames() {
        return indexColumnNames;
    }

    public void setIndexColumnNames(List<String> indexColumnNames) {
        this.indexColumnNames = indexColumnNames;
    }

    public Filter copy() {
        Filter copy = new Filter();
        copy.setName(name);
        copy.setCondition(condition);
        copy.setEntityImplementationClassName(entityImplementationClassName);
        if (!CollectionUtils.isEmpty(indexColumnNames)) {
            copy.setIndexColumnNames(new ArrayList<String>(indexColumnNames));
        }
        return copy;
    }
}
