/**
 * 
 */
package com.gwtincubator.security.exception;

/**
 * A more specific exception for authentication error type.
 * @author David MARTIN
 *
 */
public class AuthenticationException extends ApplicationSecurityException {

	/**	serialVersionUID */
	private static final long serialVersionUID = -6437864268780034827L;

	/**
	 * 
	 */
	public AuthenticationException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AuthenticationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AuthenticationException(Throwable cause) {
		super(cause);
	}
	
}
