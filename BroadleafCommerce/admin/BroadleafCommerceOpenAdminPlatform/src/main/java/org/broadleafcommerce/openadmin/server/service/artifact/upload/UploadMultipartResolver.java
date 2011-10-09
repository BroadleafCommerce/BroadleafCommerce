package org.broadleafcommerce.openadmin.server.service.artifact.upload;

import org.apache.commons.fileupload.FileUpload;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/2/11
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadMultipartResolver extends CommonsMultipartResolver {

    private static ThreadLocal<UploadProgressListener> progressListener = new ThreadLocal<UploadProgressListener>();

    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        progressListener.get().setPercentDone(100D);
        super.cleanupMultipart(request);
    }

    @Override
    protected FileUpload prepareFileUpload(String encoding) {
        FileUpload response = super.prepareFileUpload(encoding);
        response.setProgressListener(progressListener.get());
        return response;
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        progressListener.set(new UploadProgressListener(request));
        return super.resolveMultipart(request);
    }

}
