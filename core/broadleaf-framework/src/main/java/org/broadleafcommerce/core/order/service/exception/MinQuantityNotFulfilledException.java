package org.broadleafcommerce.core.order.service.exception;

public class MinQuantityNotFulfilledException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    protected String productId;

    public MinQuantityNotFulfilledException (String message, String productId) {
        super(message);
        setProductId(productId);
    }

    public MinQuantityNotFulfilledException(String message) {
        super(message);
    }

    public MinQuantityNotFulfilledException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
