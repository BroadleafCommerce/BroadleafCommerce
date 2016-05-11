/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.extensibility.jpa.clone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Marker annotation for any field on a sandboxable entity that should not undergo enterprise sandbox config validation
 * (if applicable). This has added benefits when marking a collection field on a sandboxable entity:
 * </p>
 * <ul>
 * <li>When fetched, the collection will behave as a standard Hibernate collection, rather than enforcing enterprise behavior</li>
 * <li>Adds and removals on the collection directly should behave as expected as long as the collection member type is itself
 * not sandboxable</li>
 *</ul>
 * Please note, it is expected when using this annotation on a collection that the collection member type is not sandboxable. Otherwise,
 * persistence of the collection member will itself engage sandbox state and will lead to unexpected results when fetching
 * the collection members.
 * </p>
 * When used according to these guidelines, this annotation can be used to allow an otherwise sandboxable entity to have
 * a non-sandboxable, ToMany relationship to another non-sandboxable entity.
 *
 * @author jfischer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface IgnoreEnterpriseBehavior {

}
