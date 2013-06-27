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

/**
 * Implementation of {@code URLProcessor} that indicates the URL was not able to be
 * processed by any of the configured processors.
 *
 * This is a valid state used to indicate a URL that although not processed by
 * the URL processors will likely be processed by other mechanisms (e.g. Spring-MVC)
 * within the web application.
 *
 * Created by bpolster.
 */
public class NullURLProcessor implements URLProcessor {
    private static NullURLProcessor _instance = new NullURLProcessor();


    public static NullURLProcessor getInstance() {
        return _instance;
    }

    /**
     * Always returns true.
     *
     * @param requestURI
     *
     * @return true if this URLProcessor is able to process the passed in request
     */
    @Override
    public boolean canProcessURL(String requestURI) {
        return true;
    }

    /**
     *  The processURL method should not be called on the NullURLProcessor.   This class provides a cacheable
     *  instance of URLProcessor that indicates to the controlling program (@see BroadleafProcessURLFilter)
     *  that the current URL cannot be processed.
     *
     * @param requestURI The requestURI with the context path trimmed off
     * @return true if the processor was able to process the passed in URL.
     * @throws UnsupportedOperationException
     */
    public boolean processURL(String requestURI) {
        throw new UnsupportedOperationException();
    }
}
