/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.security;

import org.broadleafcommerce.openadmin.dto.SectionCrumb;

import java.util.List;
import java.util.Map;

/**
 * Validation service for reviewing any fully qualified classname data that is part of a Http request. If detected, this
 * data is validated against a whitelist of qualified classes. This is a security measure to protect against any
 * subsequent class initialization of unexpected classes via Class.forName(..).
 *
 * @author Jeff Fischer
 */
public interface ClassNameRequestParamValidationService {

    /**
     * Compare a map of request params to fully qualified classname values against the whitelist.
     *
     * @param requestParamToClassName a map of request params and associated fully qualified classnames to confirm against the whitelist
     * @param persistenceUnitName the persistence unit the white list is generated from
     * @return whether or not the map of values is valid
     */
    boolean validateClassNameParams(Map<String, String> requestParamToClassName, String persistenceUnitName);

    /**
     * Retrieve a fully qualified classname using a sectionKey. Will return the passed in sectionKey if not classname
     * is registered for it in the datastore. Since it's possible for no classname to be registered and for the
     * key to be a fully qualified classname itself, any unmatched sectionKey is confirmed against the whitelist. If found
     * to be not valid, a {@link org.broadleafcommerce.openadmin.exception.SectionKeyValidationException} instance is
     * thrown. If thrown from within a Spring MVC controller, this exception will result in an Http 404 status code
     * back to the requester.
     *
     * @param sectionKey the sectionKey used to retrieve the fully qualified classname
     * @return the fully qualified classname associated with the sectionKey, or the sectionKey itself if no match is found
     */
    String getClassNameForSection(String sectionKey);

    /**
     * Retrieve a list of section crumbs given a delimited string (usually harvested from the "sectionCrumbs" param on a
     * Http request). The SectionCrumb instances contains sectionKey information, and are therefore susceptible to the
     * same validation requirements as {@link #getClassNameForSection(String)}. If a sectionKey is found
     * to be not valid, a {@link org.broadleafcommerce.openadmin.exception.SectionKeyValidationException} instance is
     * thrown. If thrown from within a Spring MVC controller, this exception will result in an Http 404 status code
     * back to the requester.
     *
     * @param crumbList the delimited string (related to the "sectionCrumbs" param in an Http request)
     * @return the list of section crumbs representing the currently active admin sections for a given request
     */
    List<SectionCrumb> getSectionCrumbs(String crumbList);
}
