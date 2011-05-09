package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation;

public class IncompatibleMVELTranslationException extends Exception {

	private static final long serialVersionUID = 1L;

	public IncompatibleMVELTranslationException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IncompatibleMVELTranslationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public IncompatibleMVELTranslationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public IncompatibleMVELTranslationException(Throwable cause) {
		super(cause);
	}

}
