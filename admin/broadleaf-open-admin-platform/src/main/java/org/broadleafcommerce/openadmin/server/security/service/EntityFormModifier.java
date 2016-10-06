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
package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * Qualified instances are expected to manipulate the {@link EntityForm} instance passed in the request.
 *
 * @author Jeff Fischer
 */
public interface EntityFormModifier {

    /**
     * Modify the {@link EntityForm} in the request. The request contains other relevant information that should be
     * useful during the modification.
     *
     * @see EntityFormModifierRequest
     * @param request the EntityForm, and other supporting objects
     */
    void modifyEntityForm(EntityFormModifierRequest request);

    /**
     * Modify the {@link ListGrid} in the request. The request contains other relevant information that should be
     * useful during the modification.
     *
     * @param request The ListGrid, and other supporting objects
     */
    void modifyListGrid(EntityFormModifierRequest request);

    /**
     * Whether or not this EntityFormModifier is qualified to modify based on the modifierType.
     *
     * @param modifierType and identifier for the class of modification being requested
     * @return Whether or not qualified
     */
    boolean isQualified(String modifierType);

}
