package org.broadleafcommerce.cms.web;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This interface is intended for use by processors that will determine whether a given URL
 * requires special treatment.
 *
 * Specifically, certain URLs refer to custom managed content pages.   Others are setup as
 * SEO friendly URLs for products.
 *
 * The {@code ProcessURLFilter} will check it's internal cache to determine which URL processor
 * should be invoked for a passed in URL.  If it is unable to find a matching processor in cache,
 * then it will call each processor in turn to provide an attempt to process the URL.
 *
 * Created by bpolster.
 */
public interface URLProcessor {


    /**
     * Implementors of this interface will return true if they are able to process the
     * passed in request.
     *
     * Implementors of this method will need to rely on the BroadleafRequestContext class
     * which provides access to the current sandbox, locale, request, and response via a
     * threadlocal context
     *
     * @param key
     *
     * @return true if the passed in key can be processed by this processor.
     */
    boolean canProcessURL(String key);

    /**
     * Implementers of this interface will process the passed in request.
     *
     * Implementors of this method will need to rely on the BroadleafRequestContext class
     * which provides access to the current sandbox, locale, request, and response via a
     * threadlocal context
     *
     * @return true if the processor was able to process the passed in URL.
     * @throws IOException
     * @throws ServletException
     */
    void processURL(String key) throws IOException, ServletException;
}
