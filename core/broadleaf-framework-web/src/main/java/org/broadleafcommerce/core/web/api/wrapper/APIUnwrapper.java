package org.broadleafcommerce.core.web.api.wrapper;

import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface is the super interface for all classes that will provide a JAXB unwrapper
 * around classes.  Any class that will be exposed via JAXB annotations to the JAXRS API
 * may implement this as a convenience to provide a standard method to unwrap data objects.
 *
 * This is not a requirement as objects will not generally be passed using a reference to this
 * interface.
 * @param <T>
 */
public interface APIUnwrapper<T> {
    public T unwrap(HttpServletRequest request, ApplicationContext context);
}
