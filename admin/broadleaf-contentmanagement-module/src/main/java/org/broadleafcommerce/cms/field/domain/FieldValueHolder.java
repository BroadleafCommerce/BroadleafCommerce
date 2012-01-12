package org.broadleafcommerce.cms.field.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FieldValueHolder is a set of methods to access and modify a generic value.   The interface allows
 * for shared processing of PageField and StructuredContentField objects.
 *
 * Both of these objects have String values that require processing after they are retrieved from the
 * DB.
 *
 * Created by bpolster.
 */
public interface FieldValueHolder {
    
    public String getValue();

    /**
     * The value within an SC or Page Field may require processing.   To allow for efficient caching within the client,
     * the post-processed value can be stored on the item itself.
     *
     * @return the processed value or null if no processed value exists for this item
     */
    @Nullable
    public String getProcessedValue();


    /**
     * The value within an SC or Page may require processing.   To allow for efficient caching within the client,
     * the post-processed value can be stored on the item itself.
     *
     * @param value The post-processed value to be stored on the item.
     * @param clearExistingValue If set to true will null out the underlying value.   This makes since for most
     *                           implementations of this class because this data is read-only in most contexts and
     *                           clearing the existing value will allow it to be garbage collected.    Since SC items
     *                           can hold CLOB values this is an important memory concern.
     */
    public void setProcessedValue(@Nonnull String value, boolean clearExistingValue);
}
