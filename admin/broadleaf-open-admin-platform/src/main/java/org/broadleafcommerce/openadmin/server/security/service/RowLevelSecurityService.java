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
