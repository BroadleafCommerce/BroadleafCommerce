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
 * @deprecated in favor of Spring MVC mechanisms (@see PageHandlerMapping)
 * Enum that indicates the action to take when processing a URL.
 * <ul>
 *     <li>PAGE - indicates that the URL will be handled as a CMS managed page</li>
 *     <li>PRODUCT - indicates that the URL is an SEO manged product page</li>
 *     <li>CATEGORY - indicate that the URL is an SEO managed category URL</li>
 *     <li>PROCEED - indicates that the URL should be passed through and is not handled by BLC custom filters</li>
 *     <li>REDIRECT - indicates that the URL should be redirected to another URL</li>
 *     <li>UNKNOWN - indicates that it has not yet been determined how to process the URL</li>
 * </ul>
 *
 * Created by bpolster.
 */
public enum ProcessURLAction {
    PAGE,
    PRODUCT,
    CATEGORY,
    PROCEED,
    REDIRECT,
    UNKNOWN
}
