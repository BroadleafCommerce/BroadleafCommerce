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
package org.broadleafcommerce.openadmin.server.dao;

import java.lang.reflect.Type;

/**
 * @author Jeff Fischer
 */
public class FieldInfo {

    protected String name;
    protected Type genericType;
    protected String manyToManyMappedBy;
    protected String manyToManyTargetEntity;
    protected String oneToManyMappedBy;
    protected String oneToManyTargetEntity;
    protected String mapKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getGenericType() {
        return genericType;
    }

    public void setGenericType(Type genericType) {
        this.genericType = genericType;
    }

    public String getManyToManyMappedBy() {
        return manyToManyMappedBy;
    }

    public void setManyToManyMappedBy(String manyToManyMappedBy) {
        this.manyToManyMappedBy = manyToManyMappedBy;
    }

    public String getManyToManyTargetEntity() {
        return manyToManyTargetEntity;
    }

    public void setManyToManyTargetEntity(String manyToManyTargetEntity) {
        this.manyToManyTargetEntity = manyToManyTargetEntity;
    }

    public String getOneToManyMappedBy() {
        return oneToManyMappedBy;
    }

    public void setOneToManyMappedBy(String oneToManyMappedBy) {
        this.oneToManyMappedBy = oneToManyMappedBy;
    }

    public String getOneToManyTargetEntity() {
        return oneToManyTargetEntity;
    }

    public void setOneToManyTargetEntity(String oneToManyTargetEntity) {
        this.oneToManyTargetEntity = oneToManyTargetEntity;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

}
