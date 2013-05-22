package org.broadleafcommerce.openadmin.web.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jeff Fischer
 */
public abstract class AbstractBroadleafAdminRequestFilter extends OncePerRequestFilter {

    private final Log LOG = LogFactory.getLog(AbstractBroadleafAdminRequestFilter.class);

    private Set<String> ignoreSuffixes;

    @Value("${asset.server.url.prefix.internal}")
    private String assetPrefix;

    /**
     * Determines if the passed in URL should be processed by the content management system.
     * <p/>
     * By default, this method returns false for any BLC-Admin URLs and service calls and for all
     * common image/digital mime-types (as determined by an internal call to {@code getIgnoreSuffixes}.
     * <p/>
     * This check is called with the {@code doFilterInternal} method to short-circuit the content
     * processing which can be expensive for requests that do not require it.
     *
     * @param requestURI - the HttpServletRequest.getRequestURI
     * @return true if the {@code HttpServletRequest} should be processed
     */
    protected boolean shouldProcessURL(HttpServletRequest request, String requestURI) {
        int pos = requestURI.lastIndexOf(".");
        if (pos > 0 && !requestURI.contains(assetPrefix)) {
            String suffix = requestURI.substring(pos);
            if (getIgnoreSuffixes().contains(suffix.toLowerCase())) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("BroadleafProcessURLFilter ignoring request due to suffix " + requestURI);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a set of suffixes that can be ignored by content processing.   The following
     * are returned:
     * <p/>
     * <B>List of suffixes ignored:</B>
     *
     * ".aif", ".aiff", ".asf", ".avi", ".bin", ".bmp", ".doc", ".eps", ".gif", ".hqx", ".jpg", ".jpeg", ".mid", ".midi", ".mov", ".mp3", ".mpg", ".mpeg", ".p65", ".pdf", ".pic", ".pict", ".png", ".ppt", ".psd", ".qxd", ".ram", ".ra", ".rm", ".sea", ".sit", ".stk", ".swf", ".tif", ".tiff", ".txt", ".rtf", ".vob", ".wav", ".wmf", ".xls", ".zip";
     *
     * @return set of suffixes to ignore.
     */
    @SuppressWarnings("rawtypes")
    protected Set getIgnoreSuffixes() {
        if (ignoreSuffixes == null || ignoreSuffixes.isEmpty()) {
            String[] ignoreSuffixList = {".aif", ".aiff", ".asf", ".avi", ".bin", ".bmp", ".css", ".doc", ".eps", ".gif", ".hqx", ".js", ".jpg", ".jpeg", ".mid", ".midi", ".mov", ".mp3", ".mpg", ".mpeg", ".p65", ".pdf", ".pic", ".pict", ".png", ".ppt", ".psd", ".qxd", ".ram", ".ra", ".rm", ".sea", ".sit", ".stk", ".swf", ".tif", ".tiff", ".txt", ".rtf", ".vob", ".wav", ".wmf", ".xls", ".zip"};
            ignoreSuffixes = new HashSet<String>(Arrays.asList(ignoreSuffixList));
        }
        return ignoreSuffixes;
    }
}
