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

package org.broadleafcommerce.profile.extensibility.jpa;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.persistence.spi.PersistenceUnitInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.extensibility.jpa.convert.BroadleafClassTransformer;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.Jpa2PersistenceUnitInfoDecorator;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;
import org.springframework.util.ClassUtils;

/**
 * Merges jars, class names and mapping file names from several persistence.xml files. The
 * MergePersistenceUnitManager will continue to keep track of individual persistence unit
 * names (including individual data sources). When a specific PersistenceUnitInfo is requested
 * by unit name, the appropriate PersistenceUnitInfo is returned with modified jar files
 * urls, class names and mapping file names that include the comprehensive collection of these
 * values from all persistence.xml files.
 * 
 * 
 * @author jfischer, jjacobs
 */
public class MergePersistenceUnitManager extends DefaultPersistenceUnitManager {

    private static final Log LOG = LogFactory.getLog(MergePersistenceUnitManager.class);
    
    protected HashMap<String, PersistenceUnitInfo> mergedPus = new HashMap<String, PersistenceUnitInfo>();
    protected final boolean jpa2ApiPresent = ClassUtils.hasMethod(PersistenceUnitInfo.class, "getSharedCacheMode");
    protected List<BroadleafClassTransformer> classTransformers = new ArrayList<BroadleafClassTransformer>();

    protected PersistenceUnitInfo getMergedUnit(String persistenceUnitName, MutablePersistenceUnitInfo newPU) {
        if (!mergedPus.containsKey(persistenceUnitName)) {
            PersistenceUnitInfo puiToStore = newPU;
            if (jpa2ApiPresent) {
                puiToStore = (PersistenceUnitInfo) Proxy.newProxyInstance(SmartPersistenceUnitInfo.class.getClassLoader(),
                        new Class[] {SmartPersistenceUnitInfo.class}, new Jpa2PersistenceUnitInfoDecorator(newPU));
            }
            mergedPus.put(persistenceUnitName, puiToStore);
        }
        return mergedPus.get(persistenceUnitName);
    }
    
    @Override
    public void preparePersistenceUnitInfos() {
        super.preparePersistenceUnitInfos();
        try {
            List<Class<?>> managedClassList = new ArrayList<Class<?>>();
            for (PersistenceUnitInfo pui : mergedPus.values()) {
                for (BroadleafClassTransformer transformer : classTransformers) {
                    try {
                        pui.addTransformer(transformer);
                    } catch (IllegalStateException e) {
                        LOG.warn("A BroadleafClassTransformer is configured for this persistence unit, but Spring reported a problem (likely that a LoadTimeWeaver is not registered). As a result, the Broadleaf Commerce ClassTransformer ("+transformer.getClass().getName()+") is not being registered with the persistence unit.", e);
                    }
                }
            }
            for (PersistenceUnitInfo pui : mergedPus.values()) {
                for (String managedClassName : pui.getManagedClassNames()) {
                    Class<?> temp = Class.forName(managedClassName, true, getClass().getClassLoader());
                    if (!managedClassList.contains(temp)) {
                        managedClassList.add(temp);
                    }
                }
            }
            
            /*
             * Re-transform any classes that were possibly loaded previously before our class transformers were registered
             */
            if (BroadleafLoadTimeWeaver.isInstrumentationAvailable()) {
                if (BroadleafLoadTimeWeaver.getInstrumentation().isRetransformClassesSupported()) {
                    List<Class<?>> filteredClasses = new ArrayList<Class<?>>();
                    for (Class<?> clazz : managedClassList) {
                        if (BroadleafLoadTimeWeaver.getInstrumentation().isModifiableClass(clazz)) {
                            filteredClasses.add(clazz);
                        }
                    }
                    BroadleafLoadTimeWeaver.getInstrumentation().retransformClasses(filteredClasses.toArray(new Class[]{}));
                } else {
                    LOG.warn("The JVM instrumentation is reporting that retransformation of classes is not supported. This feature is required for dependable class transformation with Broadleaf. Have you made sure to specify the broadleaf-instrument jar as a javaagent parameter for your JVM?");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo newPU) {
        super.postProcessPersistenceUnitInfo(newPU);
        newPU.addJarFileUrl(newPU.getPersistenceUnitRootUrl());
        String persistenceUnitName = newPU.getPersistenceUnitName();
        MutablePersistenceUnitInfo temp;
        PersistenceUnitInfo pui = getMergedUnit(persistenceUnitName, newPU);
        if (pui != null && Proxy.isProxyClass(pui.getClass())) {
            // JPA 2.0 PersistenceUnitInfo decorator with a SpringPersistenceUnitInfo as target
            Jpa2PersistenceUnitInfoDecorator dec = (Jpa2PersistenceUnitInfoDecorator) Proxy.getInvocationHandler(pui);
            temp = (MutablePersistenceUnitInfo) dec.getTarget();
        }
        else {
            // Must be a raw JPA 1.0 SpringPersistenceUnitInfo instance
            temp = (MutablePersistenceUnitInfo) pui;
        }
        //final URL persistenceUnitRootUrl = newPU.getPersistenceUnitRootUrl();
        temp.setPersistenceUnitRootUrl(null);
        List<String> managedClassNames = newPU.getManagedClassNames();
        for (String managedClassName : managedClassNames){
            if (!temp.getManagedClassNames().contains(managedClassName)) {
                temp.addManagedClassName(managedClassName);
            }
        }
        List<String> mappingFileNames = newPU.getMappingFileNames();
        for (String mappingFileName : mappingFileNames) {
            if (!temp.getMappingFileNames().contains(mappingFileName)) {
                temp.addMappingFileName(mappingFileName);
            }
        }
        temp.setExcludeUnlistedClasses(newPU.excludeUnlistedClasses());
        for (URL url : newPU.getJarFileUrls()) {
            if (!temp.getJarFileUrls().contains(url)) {
                temp.addJarFileUrl(url);
            }
        }
        temp.setJtaDataSource(newPU.getJtaDataSource());
        temp.setNonJtaDataSource(newPU.getNonJtaDataSource());
        if (temp.getProperties() == null) {
            temp.setProperties(newPU.getProperties());
        } else {
            Properties props = newPU.getProperties();
            if (props != null) {
                for (Object key : props.keySet()) {
                    temp.getProperties().setProperty((String) key, props.getProperty((String) key)); 
                    for (BroadleafClassTransformer transformer : classTransformers) {
                        try {
                            transformer.compileJPAProperties(props, key);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        temp.setTransactionType(newPU.getTransactionType());
        if (newPU.getPersistenceProviderClassName() != null) {
            temp.setPersistenceProviderClassName(newPU.getPersistenceProviderClassName());
        }
        if (newPU.getPersistenceProviderPackageName() != null) {
            temp.setPersistenceProviderPackageName(newPU.getPersistenceProviderPackageName());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#obtainPersistenceUnitInfo(java.lang.String)
     */
    @Override
    public PersistenceUnitInfo obtainPersistenceUnitInfo(String persistenceUnitName) {
        return mergedPus.get(persistenceUnitName);
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#obtainDefaultPersistenceUnitInfo()
     */
    @Override
    public PersistenceUnitInfo obtainDefaultPersistenceUnitInfo() {
        throw new IllegalStateException("Default Persistence Unit is not supported. The persistence unit name must be specified at the entity manager factory.");
    }

    public List<BroadleafClassTransformer> getClassTransformers() {
        return classTransformers;
    }

    public void setClassTransformers(List<BroadleafClassTransformer> classTransformers) {
        this.classTransformers = classTransformers;
    }
    
}
