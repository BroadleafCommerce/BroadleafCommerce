package org.broadleafcommerce.extensibility.jpa;

public class EntityClassNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntityClassNotFoundException() {
        super();
    }

    public EntityClassNotFoundException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EntityClassNotFoundException(String arg0) {
        super(arg0);
    }

    public EntityClassNotFoundException(Throwable arg0) {
        super(arg0);
    }

}
