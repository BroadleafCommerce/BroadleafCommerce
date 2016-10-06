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

import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * For special {@link RowLevelSecurityProvider} instances, add special behavior that allows for modifying an {@link EntityForm}
 * that has been marked as read only. Presumably, the modifier would want to enable portions of the EntityForm for specialized
 * behavior.
 *
 * @author Jeff Fischer
 */
public interface ExceptionAwareRowLevelSecurityProvider {

    /**
     * Provide a modifier capable of manipulating an {@link EntityForm} that has been marked as readonly and modify its
     * state, presumably to set one or more aspects of the form as editable.
     *
     * @see EntityFormModifier
     * @return package containing the modifier implementations and any configurations for those modifiers
     */
    EntityFormModifierConfiguration getUpdateDenialExceptions();

}
