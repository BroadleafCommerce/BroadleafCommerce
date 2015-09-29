/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.breadcrumbs.service;


/**
 * Contains a list of static priority fields for commonly used Breadcrumb elements.
 * 
 * ***** NOTE *****
 * These handlers run in reverse priority order to allow for the later breadcrumbs to modify
 * the URL by removing relevant pieces.
 * 
 * Individual handlers reference these priorities but can be overridden by custom implementations.
 * 
 * @author bpolster
 */
public class BreadcrumbHandlerDefaultPriorities {

    public static final int HOME_CRUMB = -1000;
    public static final int CATEGORY_CRUMB = 2000;
    public static final int SEARCH_CRUMB = 3000;
    public static final int PRODUCT_CRUMB = 4000;
    public static final int PAGE_CRUMB = 5000;
}
