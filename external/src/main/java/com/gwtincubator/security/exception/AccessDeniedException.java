/**
 * 
 */
package com.gwtincubator.security.exception;

/**
 * A more specific exception for access denied error type.
 * @author David MARTIN
 *
 */
public class AccessDeniedException extends ApplicationSecurityException {

	/**	serialVersionUID */
	private static final long serialVersionUID = -276267759288796263L;

	/**
	 * 
	 */
	public AccessDeniedException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AccessDeniedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AccessDeniedException(Throwable cause) {
		super(cause);
	}
	
}
