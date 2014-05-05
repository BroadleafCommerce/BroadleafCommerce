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
package org.broadleafcommerce.openadmin.server.security.service;

import java.util.List;

/**
 * <p>
 * Provides row-level security to the various CRUD operations in the admin
 * 
 * <p>
 * This security service can be extended by the use of {@link RowLevelSecurityProviders}, of which this service has a list.
 * To add additional providers, add this to an applicationContext merged into the admin application:
 * 
 * {@code
 *  <bean id="blCustomRowSecurityProviders" class="org.springframework.beans.factory.config.ListFactoryBean" >
 *       <property name="sourceList">
 *          <list>
 *              <ref bean="customProvider" />
 *          </list>
 *      </property>
 *  </bean>
 *  <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor">
 *      <property name="collectionRef" value="blCustomRowSecurityProviders" />
 *      <property name="targetRef" value="blRowLevelSecurityProviders" />
 *  </bean>
 * }
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @author Brian Polster (bpolster)
 */
public interface RowLevelSecurityService extends RowLevelSecurityProvider {

    /**
     * Gets all of the registered providers
     * @return the providers configured for this service
     */
    public List<RowLevelSecurityProvider> getProviders();
}
