/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;

import java.util.List;

/**
 * Resolves which field of an entity should be used for searching when targeting
 * an entity directly and not a specific field on that entity.
 *
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFieldResolver {

    /**
     * Returns the name of the field to use for searching for the given entity classname.
     *
     * @param className
     * @return the field name
     * @throws ServiceException
     */
    String resolveField(String className) throws ServiceException;

    /**
     * Returns the name of the field to use for searching for the given entity classname, inspecting it with the
     * given section crumbs. Use this when the entity is being inspected as the target of a lookup field, so the
     * inspect is authorized the same way as the lookup fetch itself (through the owning entity's crumb) rather than
     * requiring direct permission on the looked-up entity.
     *
     * @param className
     * @param sectionCrumbs
     * @return the field name
     * @throws ServiceException
     */
    default String resolveField(String className, List<SectionCrumb> sectionCrumbs) throws ServiceException {
        return resolveField(className);
    }

}
