/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Provide a helper component for assigning custom hints during query execution. These hints may be inspected by one or more
 * components during query execution in order to influence behavior.
 *
 * @author Jeff Fischer
 */
public interface QueryHelper {

    <X> List<X> getResultListWithHint(TypedQuery<X> query, String hintKey, Object hintValue);

    <X> X getSingleResultWithHint(TypedQuery<X> query, String hintKey, Object hintValue);

    List getResultListWithHint(Query query, String hintKey, Object hintValue);

    Object getSingleResultWithHint(Query query, String hintKey, Object hintValue);

    Object getQueryHint(String hintKey);

}
