/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.extensibility.jpa.copy.NullClassTransformer;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.Jpa2PersistenceUnitInfoDecorator;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

    @Resource(name="blMergedPersistenceXmlLocations")
    protected Set<String> mergedPersistenceXmlLocations;

    @Resource(name="blMergedDataSources")
    protected Map<String, DataSource> mergedDataSources;
    
    @Resource(name="blMergedClassTransformers")
    protected Set<BroadleafClassTransformer> mergedClassTransformers;

    @PostConstruct
    public void configureMergedItems() {
        String[] tempLocations;
        try {
            Field persistenceXmlLocations = DefaultPersistenceUnitManager.class.getDeclaredField("persistenceXmlLocations");
            persistenceXmlLocations.setAccessible(true);
            tempLocations = (String[]) persistenceXmlLocations.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (String legacyLocation : tempLocations) {
            if (!legacyLocation.endsWith("/persistence.xml")) {
                //do not add the default JPA persistence location by default
                mergedPersistenceXmlLocations.add(legacyLocation);
            }
        }
        setPersistenceXmlLocations(mergedPersistenceXmlLocations.toArray(new String[mergedPersistenceXmlLocations.size()]));

        if (!mergedDataSources.isEmpty()) {
            setDataSources(mergedDataSources);
        }
    }
    
    @PostConstruct
    public void configureClassTransformers() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        classTransformers.addAll(mergedClassTransformers);
    }

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
    @SuppressWarnings({"unchecked", "ToArrayCallWithZeroLengthArrayArgument"})
    public void preparePersistenceUnitInfos() {
        //Need to use reflection to try and execute the logic in the DefaultPersistenceUnitManager
        //SpringSource added a block of code in version 3.1 to "protect" the user from having more than one PU with
        //the same name.  Of course, in our case, this happens before a merge occurs.  They have added
        //a block of code to throw an exception if more than one PU has the same name.  We want to
        //use the logic of the DefaultPersistenceUnitManager without the exception in the case of
        //a duplicate name. This will require reflection in order to do what we need.
        try {
            Set<String> persistenceUnitInfoNames = null;
            Map<String, PersistenceUnitInfo> persistenceUnitInfos = null;
            ResourcePatternResolver resourcePatternResolver = null;
            Field[] fields = getClass().getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                if ("persistenceUnitInfoNames".equals(field.getName())) {
                    field.setAccessible(true);
                    persistenceUnitInfoNames = (Set<String>)field.get(this);
                } else if ("persistenceUnitInfos".equals(field.getName())) {
                    field.setAccessible(true);
                    persistenceUnitInfos = (Map<String, PersistenceUnitInfo>)field.get(this);
                } else if ("resourcePatternResolver".equals(field.getName())) {
                    field.setAccessible(true);
                    resourcePatternResolver = (ResourcePatternResolver)field.get(this);
                }
            }

            persistenceUnitInfoNames.clear();
            persistenceUnitInfos.clear();

            Method readPersistenceUnitInfos =
                    getClass().
                            getSuperclass().
                            getDeclaredMethod("readPersistenceUnitInfos");
            readPersistenceUnitInfos.setAccessible(true);
            
            //In Spring 3.0 this returns an array
            //In Spring 3.1 this returns a List
            Object pInfosObject = readPersistenceUnitInfos.invoke(this);
            Object[] puis;
            if (pInfosObject.getClass().isArray()) {
                puis = (Object[])pInfosObject;
            } else {
                puis = ((Collection)pInfosObject).toArray();
            }

            for (Object pui : puis) {
                MutablePersistenceUnitInfo mPui = (MutablePersistenceUnitInfo)pui;
                if (mPui.getPersistenceUnitRootUrl() == null) {
                    Method determineDefaultPersistenceUnitRootUrl =
                            getClass().
                                    getSuperclass().
                                    getDeclaredMethod("determineDefaultPersistenceUnitRootUrl");
                    determineDefaultPersistenceUnitRootUrl.setAccessible(true);
                    mPui.setPersistenceUnitRootUrl((URL)determineDefaultPersistenceUnitRootUrl.invoke(this));
                }
                ConfigurationOnlyState state = ConfigurationOnlyState.getState();
                if ((state == null || !state.isConfigurationOnly()) && mPui.getNonJtaDataSource() == null) {
                    mPui.setNonJtaDataSource(getDefaultDataSource());
                }
                if (super.getLoadTimeWeaver() != null) {
                    Method puiInitMethod = mPui.getClass().getDeclaredMethod("init", LoadTimeWeaver.class);
                    puiInitMethod.setAccessible(true);
                    puiInitMethod.invoke(pui, getLoadTimeWeaver());
                }
                else {
                    Method puiInitMethod = mPui.getClass().getDeclaredMethod("init", ClassLoader.class);
                    puiInitMethod.setAccessible(true);
                    puiInitMethod.invoke(pui, resourcePatternResolver.getClassLoader());
                }
                postProcessPersistenceUnitInfo((MutablePersistenceUnitInfo)pui);
                String name = mPui.getPersistenceUnitName();
                persistenceUnitInfoNames.add(name);

                PersistenceUnitInfo puiToStore = mPui;
                if (jpa2ApiPresent) {
                    InvocationHandler jpa2PersistenceUnitInfoDecorator = null;
                    Class<?>[] classes = getClass().getSuperclass().getDeclaredClasses();
                    for (Class<?> clz : classes){
                        if ("org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager$Jpa2PersistenceUnitInfoDecorator"
                                .equals(clz.getName())) {
                            Constructor<?> constructor =
                                    clz.getConstructor(Class.forName("org.springframework.orm.jpa.persistenceunit.SpringPersistenceUnitInfo"));
                            constructor.setAccessible(true);
                            jpa2PersistenceUnitInfoDecorator = (InvocationHandler)constructor.newInstance(mPui);
                            break;
                        }
                    }

                    puiToStore = (PersistenceUnitInfo) Proxy.newProxyInstance(SmartPersistenceUnitInfo.class.getClassLoader(),
                            new Class[] {SmartPersistenceUnitInfo.class}, jpa2PersistenceUnitInfoDecorator);
                }
                persistenceUnitInfos.put(name, puiToStore);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occured reflectively invoking methods on " +
                    "class: " + getClass().getSuperclass().getName(), e);
        }

        try {
            List<String> managedClassNames = new ArrayList<String>();
            
            for (PersistenceUnitInfo pui : mergedPus.values()) {
                for (BroadleafClassTransformer transformer : classTransformers) {
                    try {
                        if (!(transformer instanceof NullClassTransformer) && pui.getPersistenceUnitName().equals("blPU")) {
                            pui.addTransformer(transformer);
                        }
                    } catch (Exception e) {
                        Exception refined = ExceptionHelper.refineException(IllegalStateException.class, RuntimeException.class, e);
                        if (refined instanceof IllegalStateException) {
                            LOG.warn("A BroadleafClassTransformer is configured for this persistence unit, but Spring " +
                                    "reported a problem (likely that a LoadTimeWeaver is not registered). As a result, " +
                                    "the Broadleaf Commerce ClassTransformer ("+transformer.getClass().getName()+") is " +
                                    "not being registered with the persistence unit.");
                        } else {
                            throw refined;
                        }
                    }
                }
            }
            
            for (PersistenceUnitInfo pui : mergedPus.values()) {
                for (String managedClassName : pui.getManagedClassNames()) {
                    if (!managedClassNames.contains(managedClassName)) {
                        // Force-load this class so that we are able to ensure our instrumentation happens globally.
                        Class.forName(managedClassName, true, getClass().getClassLoader());
                        managedClassNames.add(managedClassName);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo newPU) {
        super.postProcessPersistenceUnitInfo(newPU);
        ConfigurationOnlyState state = ConfigurationOnlyState.getState();
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
        if (temp.getProperties() == null) {
            temp.setProperties(newPU.getProperties());
        } else {
            Properties props = newPU.getProperties();
            if (props != null) {
                for (Object key : props.keySet()) {
                    temp.getProperties().put(key, props.get(key));
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
        if (state == null || !state.isConfigurationOnly()) {
            if (newPU.getJtaDataSource() != null) {
                temp.setJtaDataSource(newPU.getJtaDataSource());
            }
            if (newPU.getNonJtaDataSource() != null) {
                temp.setNonJtaDataSource(newPU.getNonJtaDataSource());
            }
        } else {
            temp.getProperties().setProperty("hibernate.hbm2ddl.auto", "none");
            temp.getProperties().setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
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
