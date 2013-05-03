
package org.broadleafcommerce.common.i18n.service;

import java.util.Locale;

public interface I18NService {

    /**
     * Returns the translated value of the property for the given entity. For example, if entity is an instance of 
     * Product and property is equal to name, this method might return "Hoppin' Hot Sauce" if we are in an English 
     * locale and "Salsa de la Muerte Saltante" if we are in a Spanish locale.
     * 
     * @param entity
     * @param property
     * @param locale
     * @return the translated value of the property for the given entity
     */
    public String getTranslatedValue(Object entity, String property, Locale locale);

}