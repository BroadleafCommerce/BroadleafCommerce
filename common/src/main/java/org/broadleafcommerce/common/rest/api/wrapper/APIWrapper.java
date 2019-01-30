/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface is the super interface for all classes that will provide a JAXB wrapper
 * around classes.  Any class that will be exposed via JAXB annotations to the JAXRS API
 * may implement this as a convenience to provide a standard method to populate data objects.
 *
 * This is not a requirement as objects will not generally be passed using a reference to this
 * interface.
 * @param <T>
 */
public interface APIWrapper<T> extends Serializable {

    public void wrapDetails(T model, HttpServletRequest request);

    public void wrapSummary(T model, HttpServletRequest request);

}
