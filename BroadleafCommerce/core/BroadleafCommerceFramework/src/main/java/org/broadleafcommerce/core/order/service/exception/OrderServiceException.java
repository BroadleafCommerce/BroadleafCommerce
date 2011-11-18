package org.broadleafcommerce.core.order.service.exception;

public class OrderServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	

	public OrderServiceException() {
		super();
	}

	public OrderServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public OrderServiceException(String message) {
		super(message);
	}

	public OrderServiceException(Throwable cause) {
		super(cause);
	}

}
