package org.broadleafcommerce.common.file;

import org.broadleafcommerce.common.file.service.BroadleafFileService;

/**
 * Marker exception that just extends RuntimeException to be used by the {@link BroadleafFileService}
 * @author bpolster
 *
 */
public class FileServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FileServiceException() {
        super();
    }

    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(Throwable cause) {
        super(cause);
    }
}
