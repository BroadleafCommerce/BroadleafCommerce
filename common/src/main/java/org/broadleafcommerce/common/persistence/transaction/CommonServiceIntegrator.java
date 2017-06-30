/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
