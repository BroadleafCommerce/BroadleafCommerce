package org.broadleafcommerce.common.presentation;

/**
 * Represents a single key value presented to a user in a selectable
 * list when editing a map value in the admin tool
 *
 * @author Jeff Fischer
 */
public @interface AdminPresentationMapKey {

    /**
     * A simple name for this key
     *
     * @return the simple name
     */
    String keyName();

    /**
     * The friendly name to present to a user for this value field title in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N.
     *
     * @return The friendly name
     */
    String friendlyKeyName();
}
