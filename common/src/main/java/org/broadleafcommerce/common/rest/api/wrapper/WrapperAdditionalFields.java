/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

package org.broadleafcommerce.common.rest.api.wrapper;

import java.util.List;

/**
 * Wrapper classes implementing this interface expose their additionalFields, and they can
 * make use of the utility methods in BaseWrapper to transfer said fields between the wrapper and 
 * the corresponding domain object
 * @author gdiaz
 *
 */
public interface WrapperAdditionalFields {

    List<MapElementWrapper> getAdditionalFields();

    void setAdditionalFields(List<MapElementWrapper> afs);
}
