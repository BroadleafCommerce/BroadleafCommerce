/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.web;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @deprecated
 * 
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
    boolean processURL(String key) throws IOException, ServletException;
}
