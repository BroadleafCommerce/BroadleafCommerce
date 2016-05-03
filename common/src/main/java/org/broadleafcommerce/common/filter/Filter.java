/*
 * #%L
 * BroadleafCommerce Common Libraries
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
