/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.config;

import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyIgnorePattern;
import org.broadleafcommerce.common.extensibility.jpa.hibernate.BroadleafHibernateEnhancingClassTransformerImpl;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceProvider;
import jakarta.persistence.spi.PersistenceUnitInfo;

/**
 * Main configuration class for the broadleaf-common module
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
public class BroadleafCommonConfig {

    @Resource(name = "blDirectCopyIgnorePatterns")
    protected List<DirectCopyIgnorePattern> ignorePatterns = new ArrayList<>();

    /**
     * Other enterprise/mulititenant modules override this adapter to provide one that supports dynamic filtration
     */
    @Bean
    @ConditionalOnMissingBean(name = "blJpaVendorAdapter")
    public JpaVendorAdapter blJpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
            @Override
            public PersistenceProvider getPersistenceProvider() {
                PersistenceProvider persistenceProvider = new HibernatePersistenceProvider() {
                    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
                        final List<String> mergedClassesAndPackages = new ArrayList(info.getManagedClassNames());
                        if (info instanceof SmartPersistenceUnitInfo smartInfo) {
                            mergedClassesAndPackages.addAll(smartInfo.getManagedPackages());
                        }

                        return (new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(info) {
                            public List<String> getManagedClassNames() {
                                return mergedClassesAndPackages;
                            }

                            public void pushClassTransformer(EnhancementContext enhancementContext) {
                                BroadleafHibernateEnhancingClassTransformerImpl classTransformer = new BroadleafHibernateEnhancingClassTransformerImpl(enhancementContext);
                                classTransformer.setIgnorePatterns(ignorePatterns);
                                info.addTransformer(classTransformer);
                            }
                        }, properties)).build();
                    }
                };
                return persistenceProvider;
            }
        };

        return vendorAdapter;
    }

}
