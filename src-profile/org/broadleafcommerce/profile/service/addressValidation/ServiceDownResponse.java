package org.broadleafcommerce.profile.service.addressValidation;



/**
* The purpose of this interface is to allow services to define a behavior when they are down.  It is used in conjuntion with ServiceMonitorAdvice and provides
* a way for a service to return a valid "I'm down" response even when it  encounters a ServiceMonitorException.
 */
public interface ServiceDownResponse {

    public Object getDownResponse(String method, Object[] args)
                           throws Exception;
}
