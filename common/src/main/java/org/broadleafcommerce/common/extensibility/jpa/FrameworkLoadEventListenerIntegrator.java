/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

import org.hibernate.HibernateException;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.event.internal.jpa.CallbackBuilderLegacyImpl;
import org.hibernate.jpa.event.internal.jpa.CallbackRegistryImpl;
import org.hibernate.jpa.event.spi.jpa.CallbackBuilder;
import org.hibernate.jpa.event.spi.jpa.CallbackRegistryConsumer;
import org.hibernate.jpa.event.spi.jpa.ListenerFactory;
import org.hibernate.jpa.event.spi.jpa.ListenerFactoryBuilder;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class FrameworkLoadEventListenerIntegrator implements Integrator {

    protected ListenerFactory jpaListenerFactory;
    protected CallbackBuilder callbackBuilder;
    protected CallbackRegistryImpl callbackRegistry;

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        //do nothing
    }

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        try {
            Class.forName("com.broadleafcommerce.enterprise.workflow.persistence.event.listener.SandBoxLoadEventListenerIntegrator");
        } catch (ClassNotFoundException e) {
            //Only run if enterprise is not on the classpath
            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
            eventListenerRegistry.setListeners(EventType.FLUSH_ENTITY, FrameworkFlushEntityEventListener.class);

            configure(metadata, sessionFactory, serviceRegistry, eventListenerRegistry);
        }
    }

    protected void configure(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry, EventListenerRegistry eventListenerRegistry) {
        final ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );

        for ( Map.Entry entry : ( (Map<?, ?>) cfgService.getSettings() ).entrySet() ) {
            if ( !String.class.isInstance( entry.getKey() ) ) {
                continue;
            }
            final String propertyName = (String) entry.getKey();
            if ( !propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
                continue;
            }
            final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
            final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
            final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
            for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
                eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
            }
        }

        // handle JPA "entity listener classes"...
        final ReflectionManager reflectionManager = ( (MetadataImpl) metadata ).getMetadataBuildingOptions()
                .getReflectionManager();

        this.callbackRegistry = new CallbackRegistryImpl();
        this.jpaListenerFactory = ListenerFactoryBuilder.buildListenerFactory( sessionFactory.getSessionFactoryOptions() );
        this.callbackBuilder = new CallbackBuilderLegacyImpl( jpaListenerFactory, reflectionManager );
        for ( PersistentClass persistentClass : metadata.getEntityBindings() ) {
            if ( persistentClass.getClassName() == null ) {
                // we can have non java class persisted by hibernate
                continue;
            }
            callbackBuilder.buildCallbacksForEntity( persistentClass.getClassName(), callbackRegistry );
        }

        for ( EventType eventType : EventType.values() ) {
            final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
            for ( Object listener : eventListenerGroup.listeners() ) {
                if ( CallbackRegistryConsumer.class.isInstance( listener ) ) {
                    ( (CallbackRegistryConsumer) listener ).injectCallbackRegistry( callbackRegistry );
                }
            }
        }
    }

    protected Object instantiate(String listenerImpl, ServiceRegistryImplementor serviceRegistry) {
        try {
            return serviceRegistry.getService( ClassLoaderService.class ).classForName( listenerImpl ).newInstance();
        }
        catch (Exception e) {
            throw new HibernateException( "Could not instantiate requested listener [" + listenerImpl + "]", e );
        }
    }

}
