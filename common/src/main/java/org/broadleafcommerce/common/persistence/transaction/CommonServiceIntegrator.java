package org.broadleafcommerce.common.persistence.transaction;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.ServiceContributingIntegrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * Support introduction of customized or additional services to the Hibernate service registry.
 *
 * @author Jeff Fischer
 */
public class CommonServiceIntegrator implements ServiceContributingIntegrator {

    @Override
    public void prepareServices(ServiceRegistryBuilder serviceRegistryBuilder) {
        serviceRegistryBuilder.addInitiator(LifecycleAwareJDBCServicesInitiator.INSTANCE);
    }

    @Override
    public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        //do nothing
    }

    @Override
    public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        //do nothing
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        //do nothing
    }
}
