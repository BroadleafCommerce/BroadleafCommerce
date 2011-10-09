package org.broadleafcommerce.common;

/**
 * Created by bpolster.
 */
public interface RequestDTO {

    /**
     * @return  returns the request not including the protocol, domain, or query string
     */
    public String getRequestURI();

    /**
     * @return Returns the URL and parameters.
     */
    public String getFullUrLWithQueryString();

    /**
     * @return true if this request came in through HTTPS
     */
    public Boolean isSecure();
}
