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
import org.broadleafcommerce.common.extensibility.jpa.convert.EntityMarkerClassTransformer;
import org.broadleafcommerce.common.extensibility.jpa.copy.NullClassTransformer;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

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
    protected List<BroadleafClassTransformer> classTransformers = new ArrayList<BroadleafClassTransformer>();

    @Resource(name="blMergedPersistenceXmlLocations")
    protected Set<String> mergedPersistenceXmlLocations;

    @Resource(name="blMergedDataSources")
    protected Map<String, DataSource> mergedDataSources;
    
    @Resource(name="blMergedClassTransformers")
    protected Set<BroadleafClassTransformer> mergedClassTransformers;

    @Resource(name="blEntityMarkerClassTransformer")
    protected EntityMarkerClassTransformer entityMarkerClassTransformer;

    @Override
    protected boolean isPersistenceUnitOverrideAllowed() {
        return true;
    }
    
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

    protected MutablePersistenceUnitInfo getMergedUnit(String persistenceUnitName, MutablePersistenceUnitInfo newPU) {
        if (!mergedPus.containsKey(persistenceUnitName)) {
            mergedPus.put(persistenceUnitName, newPU);
        }
        return (MutablePersistenceUnitInfo) mergedPus.get(persistenceUnitName);
    }

    @Override
    @SuppressWarnings({ "unchecked", "ToArrayCallWithZeroLengthArrayArgument" })
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
                    persistenceUnitInfoNames = (Set<String>) field.get(this);
                } else if ("persistenceUnitInfos".equals(field.getName())) {
                    field.setAccessible(true);
                    persistenceUnitInfos = (Map<String, PersistenceUnitInfo>) field.get(this);
                } else if ("resourcePatternResolver".equals(field.getName())) {
                    field.setAccessible(true);
                    resourcePatternResolver = (ResourcePatternResolver) field.get(this);
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
                puis = (Object[]) pInfosObject;
            } else {
                puis = ((Collection) pInfosObject).toArray();
            }

            for (Object pui : puis) {
                MutablePersistenceUnitInfo mPui = (MutablePersistenceUnitInfo) pui;
                if (mPui.getPersistenceUnitRootUrl() == null) {
                    Method determineDefaultPersistenceUnitRootUrl =
                            getClass().
                                    getSuperclass().
                                    getDeclaredMethod("determineDefaultPersistenceUnitRootUrl");
                    determineDefaultPersistenceUnitRootUrl.setAccessible(true);
                    mPui.setPersistenceUnitRootUrl((URL) determineDefaultPersistenceUnitRootUrl.invoke(this));
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
                postProcessPersistenceUnitInfo((MutablePersistenceUnitInfo) pui);
                String name = mPui.getPersistenceUnitName();
                persistenceUnitInfoNames.add(name);

                persistenceUnitInfos.put(name, mPui);
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
            
            // If a class happened to be loaded by the ClassLoader before we had a chance to set up our instrumentation,
            // it may not be in a consistent state. We'll detect that here and force the class to be reloaded
            for (PersistenceUnitInfo pui : mergedPus.values()) {
                for (String managedClassName : pui.getManagedClassNames()) {
                    if (!entityMarkerClassTransformer.getTransformedClassNames().contains(managedClassName)) {
                        LOG.error("Should have transformed " + managedClassName + " but didn't");
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
        String persistenceUnitName = newPU.getPersistenceUnitName();
        MutablePersistenceUnitInfo pui = getMergedUnit(persistenceUnitName, newPU);

        List<String> managedClassNames = newPU.getManagedClassNames();
        for (String managedClassName : managedClassNames){
            if (!pui.getManagedClassNames().contains(managedClassName)) {
                pui.addManagedClassName(managedClassName);
            }
        }
        List<String> mappingFileNames = newPU.getMappingFileNames();
        for (String mappingFileName : mappingFileNames) {
            if (!pui.getMappingFileNames().contains(mappingFileName)) {
                pui.addMappingFileName(mappingFileName);
            }
        }
        pui.setExcludeUnlistedClasses(newPU.excludeUnlistedClasses());
        for (URL url : newPU.getJarFileUrls()) {
            // Avoid duplicate class scanning by Ejb3Configuration. Do not re-add the URL to the list of jars for this
            // persistence unit or duplicate the persistence unit root URL location (both types of locations are scanned)
            if (!pui.getJarFileUrls().contains(url) && !pui.getPersistenceUnitRootUrl().equals(url)) {
                pui.addJarFileUrl(url);
            }
        }
        if (pui.getProperties() == null) {
            pui.setProperties(newPU.getProperties());
        } else {
            Properties props = newPU.getProperties();
            if (props != null) {
                for (Object key : props.keySet()) {
                    pui.getProperties().put(key, props.get(key));
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
                pui.setJtaDataSource(newPU.getJtaDataSource());
            }
            if (newPU.getNonJtaDataSource() != null) {
                pui.setNonJtaDataSource(newPU.getNonJtaDataSource());
            }
        } else {
            pui.getProperties().setProperty("hibernate.hbm2ddl.auto", "none");
            pui.getProperties().setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        }
        pui.setTransactionType(newPU.getTransactionType());
        if (newPU.getPersistenceProviderClassName() != null) {
            pui.setPersistenceProviderClassName(newPU.getPersistenceProviderClassName());
        }
        if (newPU.getPersistenceProviderPackageName() != null) {
            pui.setPersistenceProviderPackageName(newPU.getPersistenceProviderPackageName());
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
