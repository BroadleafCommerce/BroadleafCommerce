/*
 * #%L
 * broadleaf-multitenant-singleschema
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

package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.SiteDefaultsDTO;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * interface that defines what generic behavior I want from any particular EM implementation
 * (in this case, retrieve default information from a site)
 * @author gdiaz
 *
 */
public interface SiteDefaultsResolver extends ExtensionHandler {

    public ExtensionResultStatusType retrieveDefaults(SiteDefaultsDTO defautlsDTO);

}
