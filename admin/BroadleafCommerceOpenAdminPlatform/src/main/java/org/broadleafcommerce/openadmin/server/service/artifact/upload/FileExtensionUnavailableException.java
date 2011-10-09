package org.broadleafcommerce.openadmin.server.service.artifact.upload;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/13/11
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileExtensionUnavailableException extends Exception {

    public FileExtensionUnavailableException() {
    }

    public FileExtensionUnavailableException(String s) {
        super(s);
    }

    public FileExtensionUnavailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FileExtensionUnavailableException(Throwable throwable) {
        super(throwable);
    }

}
