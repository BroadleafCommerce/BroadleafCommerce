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
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

/**
 * Marker interface for any validator that contains field names as part of its configuration. This interface is checked
 * during property building and will be used to modify the field name configuration item. Take the following example,
 * AdminUserImpl declares a password match on the field passwordConfirm. However, if the AdminUserImpl instance
 * is associated to another entity, then the password match field should actually become [another entity].[admin user field].passwordConfirm.
 *
 * @author Jeff Fischer
 */
public interface FieldNamePropertyValidator {
}
