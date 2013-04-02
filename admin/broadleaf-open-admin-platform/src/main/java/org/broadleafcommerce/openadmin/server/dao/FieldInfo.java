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
}
