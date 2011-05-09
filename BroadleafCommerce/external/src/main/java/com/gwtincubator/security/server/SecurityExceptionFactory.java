package com.gwtincubator.security.server;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import com.gwtincubator.security.exception.ApplicationSecurityException;

/**
 * 
 * @author David MARTIN
 * 
 * BroadleafCommerce
 * Changed to make compatible with Spring 3
 * @author jfischer
 */
public class SecurityExceptionFactory {

	public static ApplicationSecurityException get(final Throwable springException) {
		ApplicationSecurityException gwtException = null;
		if (springException instanceof AccessDeniedException) {
			gwtException = new com.gwtincubator.security.exception.AccessDeniedException(springException.getMessage(), springException);
		} else if (springException instanceof AuthenticationException) {
			gwtException = new com.gwtincubator.security.exception.AuthenticationException(springException.getMessage(), springException);
		} else {
			gwtException = new com.gwtincubator.security.exception.ApplicationSecurityException(springException.getMessage(), springException);
		}
		return gwtException;
	}

}
