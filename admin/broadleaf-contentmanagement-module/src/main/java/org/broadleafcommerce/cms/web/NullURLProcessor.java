/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
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
