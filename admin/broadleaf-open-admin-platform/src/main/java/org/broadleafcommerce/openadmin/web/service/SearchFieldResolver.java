/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.exception.ServiceException;

/**
 * Resolves which field of an entity should be used for searching when targeting
 * an entity directly and not a specific field on that entity.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFieldResolver {

    /**
     * Returns the name of the field to use for searching for the given entity classname.
     * 
     * @param className
     * @return the field name
     * @throws ServiceException
     */
    public String resolveField(String className) throws ServiceException;

}