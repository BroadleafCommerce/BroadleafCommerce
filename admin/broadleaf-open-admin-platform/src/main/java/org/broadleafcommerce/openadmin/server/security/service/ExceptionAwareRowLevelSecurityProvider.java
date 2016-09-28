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
