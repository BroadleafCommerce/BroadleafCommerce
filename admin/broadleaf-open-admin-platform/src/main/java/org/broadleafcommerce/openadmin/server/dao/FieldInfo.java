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
