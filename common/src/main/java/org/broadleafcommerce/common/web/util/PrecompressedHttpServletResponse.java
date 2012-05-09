package org.broadleafcommerce.common.web.util;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Collection;

/**
 * @author Jeff Fischer
 */
public class PrecompressedHttpServletResponse extends HttpServletResponseWrapper {

    static {
        MimeUtil.registerMimeDetector(ExtensionMimeDetector.class.getName());
        MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
    }

    private final String originalPath;
    private final String contentType;

    public PrecompressedHttpServletResponse(HttpServletResponse response, String originalPath) {
        super(response);
        this.originalPath = originalPath;
        Collection mimeTypes = MimeUtil.getMimeTypes(originalPath);
        if (!mimeTypes.isEmpty()) {
            MimeType mimeType = (MimeType) mimeTypes.iterator().next();
            contentType = mimeType.toString();
        } else {
            contentType = null;
        }
    }

    @Override
    public String getContentType() {
        String contentType = super.getContentType();
        if (contentType != null) {
            if (contentType.contains("zip") && this.contentType != null) {
                return this.contentType;
            }

            return contentType;
        } else {
            return this.contentType;
        }
    }

    @Override
    public void setContentType(String type) {
        if (type.contains("zip") && this.contentType != null) {
            super.setContentType(this.contentType);
        } else {
            super.setContentType(type);
        }
    }
}
