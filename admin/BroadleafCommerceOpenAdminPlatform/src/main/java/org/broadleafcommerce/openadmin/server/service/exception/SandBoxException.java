package org.broadleafcommerce.openadmin.server.service.exception;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/1/11
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class SandBoxException extends Exception {

    public SandBoxException() {
    }

    public SandBoxException(String s) {
        super(s);
    }

    public SandBoxException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SandBoxException(Throwable throwable) {
        super(throwable);
    }
}
