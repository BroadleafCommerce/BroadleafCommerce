/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager.annotation;

import org.broadleafcommerce.openadmin.server.service.persistence.entitymanager.BroadleafEntityManagerInvocationHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.entitymanager.DualEntityManager;
import org.broadleafcommerce.openadmin.server.service.persistence.entitymanager.HibernateCleaner;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.orm.jpa.ExtendedEntityManagerCreator;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Modification of behavior defined in PersistenceAnnotationBeanPostProcessor to support the Broadleaf
 * concept of sand boxes. Change sets are entity alterations created through the Broadleaf admin
 * application that have not been finalized in the standard schema. This provides administrators with
 * the core functionality to preview their changes without impacting the site as it's seen by regular
 * users. This also provides the basis for approval workflows and scheduling of change set deployments.
 * This class keeps track of two persistence units instead of one: the regular persistence unit and
 * the sand box persistence unit.
 * 
 * Based on {@link org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor} by Rod Johnson and Juergen Hoeller
 * 
 * @author jfischer
 *
 */
public class BroadleafPersistenceAnnotationBeanPostProcessor extends PersistenceAnnotationBeanPostProcessor {

    private static final long serialVersionUID = 1L;
    
    private transient final Map<Class<?>, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<Class<?>, InjectionMetadata>();
    
    @Override
    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

        InjectionMetadata metadata = findPersistenceMetadata(bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of persistence dependencies failed", ex);
        }
        return pvs;
    }
    
    private InjectionMetadata findPersistenceMetadata(final Class<?> clazz) {
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(clazz);
        if (metadata == null) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(clazz);
                if (metadata == null) {
                    LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
                    Class<?> targetClass = clazz;

                    do {
                        LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
                        for (Field field : targetClass.getDeclaredFields()) {
                            BroadleafPersistenceContext bpc = field.getAnnotation(BroadleafPersistenceContext.class);
                            PersistenceContext pc = field.getAnnotation(PersistenceContext.class);
                            PersistenceUnit pu = field.getAnnotation(PersistenceUnit.class);
                            if (pc != null || pu != null || bpc != null) {
                                if (Modifier.isStatic(field.getModifiers())) {
                                    throw new IllegalStateException("Persistence annotations are not supported on static fields");
                                }
                                currElements.add(new PersistenceElement(field, null));
                            }
                        }
                        for (Method method : targetClass.getDeclaredMethods()) {
                            BroadleafPersistenceContext bpc = method.getAnnotation(BroadleafPersistenceContext.class);
                            PersistenceContext pc = method.getAnnotation(PersistenceContext.class);
                            PersistenceUnit pu = method.getAnnotation(PersistenceUnit.class);
                            if (pc != null || pu != null || bpc != null &&
                                    method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                                if (Modifier.isStatic(method.getModifiers())) {
                                    throw new IllegalStateException("Persistence annotations are not supported on static methods");
                                }
                                if (method.getParameterTypes().length != 1) {
                                    throw new IllegalStateException("Persistence annotation requires a single-arg method: " + method);
                                }
                                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                                currElements.add(new PersistenceElement(method, pd));
                            }
                        }
                        elements.addAll(0, currElements);
                        targetClass = targetClass.getSuperclass();
                    }
                    while (targetClass != null && targetClass != Object.class);

                    metadata = new InjectionMetadata(clazz, elements);
                    this.injectionMetadataCache.put(clazz, metadata);
                }
            }
        }
        return metadata;
    }
    
    /**
     * Class representing injection information about an annotated field
     * or setter method.
     */
    private class PersistenceElement extends InjectionMetadata.InjectedElement {

        private final String unitName;
        private String sandBoxUnitName;
        private PersistenceContextType type;
        private final PersistenceContextFlavor flavor;
        private Properties properties;

        @SuppressWarnings("rawtypes")
        public PersistenceElement(Member member, PropertyDescriptor pd) {
            super(member, pd);
            AnnotatedElement ae = (AnnotatedElement) member;
            PersistenceContext pc = ae.getAnnotation(PersistenceContext.class);
            PersistenceUnit pu = ae.getAnnotation(PersistenceUnit.class);
            BroadleafPersistenceContext bpc = ae.getAnnotation(BroadleafPersistenceContext.class);
            Class resourceType = EntityManager.class;
            if (pc != null) {
                if (pu != null) {
                    throw new IllegalStateException("Member may only be annotated with either " +
                            "@PersistenceContext or @PersistenceUnit, not both: " + member);
                }
                Properties properties = null;
                PersistenceProperty[] pps = pc.properties();
                if (!ObjectUtils.isEmpty(pps)) {
                    properties = new Properties();
                    for (PersistenceProperty pp : pps) {
                        properties.setProperty(pp.name(), pp.value());
                    }
                }
                this.unitName = pc.unitName();
                this.type = pc.type();
                this.properties = properties;
                this.flavor = PersistenceContextFlavor.JPA;
            } else if (bpc != null) {
                if (pu != null) {
                    throw new IllegalStateException("Member may only be annotated with either " +
                            "@PersistenceContext or @PersistenceUnit, not both: " + member);
                }
                Properties properties = null;
                PersistenceProperty[] pps = bpc.properties();
                if (!ObjectUtils.isEmpty(pps)) {
                    properties = new Properties();
                    for (PersistenceProperty pp : pps) {
                        properties.setProperty(pp.name(), pp.value());
                    }
                }
                this.unitName = bpc.unitName();
                this.sandBoxUnitName = bpc.sandBoxUnitName();
                this.type = PersistenceContextType.TRANSACTION;
                this.properties = properties;
                this.flavor = PersistenceContextFlavor.BROADLEAF;
            } else {
                resourceType = EntityManagerFactory.class;
                this.unitName = pu.unitName();
                this.flavor = PersistenceContextFlavor.JPA;
            }
            checkResourceType(resourceType);
        }

        /**
         * Resolve the object against the application context.
         */
        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            // Resolves to EntityManagerFactory or EntityManager.
            if (this.type != null) {
                if (this.flavor == PersistenceContextFlavor.JPA) {
                    return (this.type == PersistenceContextType.EXTENDED ?
                            resolveExtendedEntityManager(target, requestingBeanName) :
                            resolveEntityManager(requestingBeanName, this.unitName));
                } else {
                    EntityManager standardManager = resolveEntityManager(requestingBeanName, this.unitName);
                    EntityManager sandboxManager = resolveEntityManager(requestingBeanName, this.sandBoxUnitName);
                    BroadleafEntityManagerInvocationHandler handler = new BroadleafEntityManagerInvocationHandler((HibernateEntityManager) standardManager, (HibernateEntityManager) sandboxManager, (HibernateCleaner) getBeanFactory().getBean("blHibernateCleaner"));
                    HibernateEntityManager proxy = (HibernateEntityManager) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HibernateEntityManager.class, DualEntityManager.class}, handler);
                    return proxy;
                }
            }
            else {
                // OK, so we need an EntityManagerFactory...
                return resolveEntityManagerFactory(requestingBeanName);
            }
        }

        private EntityManagerFactory resolveEntityManagerFactory(String requestingBeanName) {
            // Obtain EntityManagerFactory from JNDI?
            EntityManagerFactory emf = getPersistenceUnit(this.unitName);
            if (emf == null) {
                // Need to search for EntityManagerFactory beans.
                emf = findEntityManagerFactory(this.unitName, requestingBeanName);
            }
            return emf;
        }

        private EntityManager resolveEntityManager(String requestingBeanName, String unitName) {
            // Obtain EntityManager reference from JNDI?
            EntityManager em = getPersistenceContext(unitName, false);
            if (em == null) {
                // No pre-built EntityManager found -> build one based on factory.
                // Obtain EntityManagerFactory from JNDI?
                EntityManagerFactory emf = getPersistenceUnit(unitName);
                if (emf == null) {
                    // Need to search for EntityManagerFactory beans.
                    emf = findEntityManagerFactory(unitName, requestingBeanName);
                }
                // Inject a shared transactional EntityManager proxy.
                if (emf instanceof EntityManagerFactoryInfo &&
                        ((EntityManagerFactoryInfo) emf).getEntityManagerInterface() != null) {
                    // Create EntityManager based on the info's vendor-specific type
                    // (which might be more specific than the field's type).
                    em = SharedEntityManagerCreator.createSharedEntityManager(emf, this.properties);
                }
                else {
                    // Create EntityManager based on the field's type.
                    em = SharedEntityManagerCreator.createSharedEntityManager(emf, this.properties, getResourceType());
                }
            }
            return em;
        }

        private EntityManager resolveExtendedEntityManager(Object target, String requestingBeanName) {
            // Obtain EntityManager reference from JNDI?
            EntityManager em = getPersistenceContext(this.unitName, true);
            if (em == null) {
                // No pre-built EntityManager found -> build one based on factory.
                // Obtain EntityManagerFactory from JNDI?
                EntityManagerFactory emf = getPersistenceUnit(this.unitName);
                if (emf == null) {
                    // Need to search for EntityManagerFactory beans.
                    emf = findEntityManagerFactory(this.unitName, requestingBeanName);
                }
                // Inject a container-managed extended EntityManager.
                em = ExtendedEntityManagerCreator.createContainerManagedEntityManager(emf, this.properties);
            }
            if (em instanceof EntityManagerProxy &&
                    getBeanFactory() != null && !getBeanFactory().isPrototype(requestingBeanName)) {
                getExtendedEntityManagersToClose().put(target, ((EntityManagerProxy) em).getTargetEntityManager());
            }
            return em;
        }
    }
    
    private BeanFactory getBeanFactory() {
        try {
            Field field = (Field) getClass().getSuperclass().getDeclaredField("beanFactory");
            ReflectionUtils.makeAccessible(field);
            return (BeanFactory) field.get(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<Object, EntityManager> getExtendedEntityManagersToClose() {
        try {
            Field field = (Field) getClass().getSuperclass().getDeclaredField("extendedEntityManagersToClose");
            ReflectionUtils.makeAccessible(field);
            return (Map<Object, EntityManager>) field.get(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
}
