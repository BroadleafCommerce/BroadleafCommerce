/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @param persistenceUnitName the persistence unit the white list is generated from
     * @return the fully qualified classname associated with the sectionKey, or the sectionKey itself if no match is found
     */
    String getClassNameForSection(String sectionKey, String persistenceUnitName);

    /**
     * Retrieve a list of section crumbs given a delimited string (usually harvested from the "sectionCrumbs" param on a
     * Http request). The SectionCrumb instances contains sectionKey information, and are therefore susceptible to the
     * same validation requirements as {@link #getClassNameForSection(String, String)}. If a sectionKey is found
     * to be not valid, a {@link org.broadleafcommerce.openadmin.exception.SectionKeyValidationException} instance is
     * thrown. If thrown from within a Spring MVC controller, this exception will result in an Http 404 status code
     * back to the requester.
     *
     * @param crumbList the delimited string (related to the "sectionCrumbs" param in an Http request)
     * @param persistenceUnitName the persistence unit the white list is generated from
     * @return the list of section crumbs representing the currently active admin sections for a given request
     */
    List<SectionCrumb> getSectionCrumbs(String crumbList, String persistenceUnitName);
}
