/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

import java.util.Set;

/**
 * Implementors may specify their own dirty properties to Hibernate in order to force a persistence
 * flush. Note, this only becomes active as a fallback in case Hibernate does not already detect
 * dirty state through its normal detection mechanism. This is useful in order to force persistence
 * related events (e.g. on an order), even when the only part that changed about an order was
 * a member of a child collection, or the like.
 *
 * @author Jeff Fischer
 */
public interface DirtyFallback {

    Set<String> getDirtyProperties();

    void clearDirtyState();

    boolean isDirty();

    void setDirty(boolean dirty);

}
