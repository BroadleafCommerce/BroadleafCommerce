
package org.broadleafcommerce.profile.core.domain;

import java.util.Map;

/**
 * this interface indicates if the domain objects use additional fields
 * It is not mandatory,  but it is useful in order to process those fields in a generic way when generating JAXB Wrappers
 * @author gdiaz
 *
 */
public interface AdditionalFields {

    public Map<String, String> getAdditionalFields();

    public void setAdditionalFields(Map<String, String> additionalFields);

}
