/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.cms.url.domain;

/**
 * A Null instance of a URLHandler.   Used by the default URLHandlerServiceImpl implementation to
 * cache misses (e.g. urls  that are not being handled by forwards and redirects.
 *
 * @author bpolster
 */
public class NullURLHandler extends URLHandlerImpl {

    private static final long serialVersionUID = 1L;

}
