package org.broadleafcommerce.common.presentation.override;

import org.broadleafcommerce.common.presentation.AdminPresentationCollection;

/**
 * @author Jeff Fischer
 */
public @interface AdminPresentationCollectionOverride {

    /**
     * The name of the property whose AdminPresentation annotation should be overwritten
     *
     * @return the name of the property that should be overwritten
     */
    String name();

    /**
     * The AdminPresentation to overwrite the property with
     *
     * @return the AdminPresentation being mapped to the attribute
     */
    AdminPresentationCollection value();

}
