package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes additional filter parameters used to refine the list of items returned from a query for
 * a DataDrivenEnumeration
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OptionFilterParam {

    /**
     * The field name in the target entity class that should be used to refine the query (i.e. sql where clause). The
     * param can be "." delimited in standard bean property fashion. For example, the preferred way of referring to
     * DataDrivenEnumerationValueImpl instances belonging to a particular instance of DataDrivenEnumerationImpl is by
     * specifying the param value as follows:
     *
     * param="type.key"
     *
     * @see org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValueImpl
     * @return the field name with which to refine the query
     */
    String param();

    /**
     * The field value that should match for any items returned from the query
     *
     * @return the field match value
     */
    String value();

}
