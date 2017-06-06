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
