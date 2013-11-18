/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.domain.SystemProperty;

import java.util.List;

/**
 * To change this template use File | Settings | File Templates.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/25/12
 */
public interface SystemPropertiesService {

    public SystemProperty saveSystemProperty(SystemProperty systemProperty);

    public void deleteSystemProperty(SystemProperty systemProperty);

    public List<SystemProperty> findAllSystemProperties();

    public SystemProperty findSystemPropertyByName(String name);

    /**
     * This method should not persist anything to the database. It should simply return the correct implementation of
     * the SystemProperty interface.
     * @return
     */
    public SystemProperty createNewSystemProperty();
}
