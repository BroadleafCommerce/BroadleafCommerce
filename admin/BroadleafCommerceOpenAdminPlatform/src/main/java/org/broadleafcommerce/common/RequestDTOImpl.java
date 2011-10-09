package org.broadleafcommerce.common;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Created by bpolster.
 */
public class RequestDTOImpl implements RequestDTO, Serializable {

    private static final long serialVersionUID = 1L;

    private String requestURI;
    private String fullUrlWithQueryString;
    private Boolean secure;

    public RequestDTOImpl() {
            // no arg constructor - used by rule builder
    }

    public RequestDTOImpl(HttpServletRequest request) {
        requestURI = request.getRequestURI();
        fullUrlWithQueryString = request.getRequestURL().toString();
        secure = "HTTPS".equalsIgnoreCase(request.getScheme());
    }

    /**
     * @return  returns the request not including the protocol, domain, or query string
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * @return Returns the URL and parameters.
     */
    public String getFullUrLWithQueryString() {
        return fullUrlWithQueryString;
    }

    /**
     * @return true if this request came in through HTTPS
     */
    public Boolean isSecure() {
        return secure;
    }

}
