package org.broadleafcommerce.openadmin.server.service.artifact.upload;

import org.apache.commons.fileupload.ProgressListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by jfischer
 */
public class UploadProgressListener implements ProgressListener {

    private double percentDone;

    public UploadProgressListener(HttpServletRequest request) {
        final HttpSession session = request.getSession();
        session.setAttribute(request.getParameter("callbackName"), this);
    }

    /*
    * (non-Javadoc)
    * @see org.apache.commons.fileupload.ProgressListener#update(long, long, int)
    */
    @Override
    public void update(long bytesRead, long contentLength, int pItems) {
        percentDone = (100 * bytesRead) / contentLength;
    }

    /**
     * Get the percent done
     *
     * @return the percent done
     */
    public double getPercentDone() {
        return percentDone;
    }

    public void setPercentDone(double percentDone) {
        this.percentDone = percentDone;
    }

}
