package org.broadleafcommerce.core.order.service.exception;

public class MinQuantityNotFulfilledException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    protected Long productId;

    public MinQuantityNotFulfilledException (String message, Long productId) {
        super(message);
        setProductId(productId);
    }

    public MinQuantityNotFulfilledException(String message) {
        super(message);
    }

    public MinQuantityNotFulfilledException(String message, Throwable cause) {
        super(message, cause);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
