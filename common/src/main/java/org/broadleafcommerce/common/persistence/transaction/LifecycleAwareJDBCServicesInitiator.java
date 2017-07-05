/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence.transaction;

import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.spi.BasicServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

import java.util.Map;

/**
 * {@link BasicServiceInitiator} implementation for introducing the custom {@link JdbcServices} implementation
 * to the Hibernate service registry.
 *
 * @author Jeff Fischer
 */
public class LifecycleAwareJDBCServicesInitiator implements BasicServiceInitiator<JdbcServices> {

    public static final LifecycleAwareJDBCServicesInitiator INSTANCE = new LifecycleAwareJDBCServicesInitiator();

    @Override
    public JdbcServices initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new LifecycleAwareJDBCServices();
    }

    @Override
    public Class<JdbcServices> getServiceInitiated() {
        return JdbcServices.class;
    }
}
