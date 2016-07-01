package org.broadleafcommerce.common.admin.domain;

/**
 * @author Jon Fleschler (jfleschler)
 */
public interface TypedEntity<T> {

    /**
     * Returns the type of the Entity
     * @return type
     */
    public T getType();

    /**
     * Sets the type of the Entity
     * @param type
     */
    public void setType(T type);

    /**
     * Returns the persisted type field name
     * @return fieldName
     */
    public String getTypeFieldName();

    /**
     * Returns the default type to be used for this entity
     * @return defaultType
     */
    public String getDefaultType();
}
