/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafPersistenceUnitDeclaringClassTransformer;
import org.broadleafcommerce.common.extensibility.jpa.convert.EntityMarkerClassTransformer;
import org.broadleafcommerce.common.extensibility.jpa.copy.NullClassTransformer;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.ejb.instrument.InterceptFieldClassFileTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.management.ObjectName;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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

    protected HashMap<String, PersistenceUnitInfo> mergedPus = new HashMap<>();
    protected List<BroadleafClassTransformer> classTransformers = new ArrayList<>();

    @Resource(name="blMergedPersistenceXmlLocations")
    protected Set<String> mergedPersistenceXmlLocations;

    @Resource(name="blMergedDataSources")
    protected Map<String, DataSource> mergedDataSources;
    
    @Resource(name="blMergedClassTransformers")
    protected Set<BroadleafClassTransformer> mergedClassTransformers;

    @Resource(name="blEntityMarkerClassTransformer")
    protected EntityMarkerClassTransformer entityMarkerClassTransformer;
    
    @Autowired(required = false)
    @Qualifier("blAutoDDLStatusExporter")
    protected MBeanExporter mBeanExporter;
    
    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected Environment environment;

    @Autowired(required = false)
    protected List<QueryConfiguration> queryConfigurations = new ArrayList<>();
    
    /**
     * This should only be used in a test context to deal with the Spring ApplicationContext refreshing between different
     * test classes but not needing to do a new transformation of classes every time. This bean will get
     * re-initialized but all the classes have already been transformed
     */
    protected static boolean transformed = false;

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
        super.preparePersistenceUnitInfos();
        try {
            boolean weaverRegistered = addTransformersToPersistenceUnits();
            
            // Only validate transformation results if there was a LoadTimeWeaver registered in the first place
            if (weaverRegistered && !transformed) {
                exceptionIfEntityMarkerNotFound();
                
                triggerClassLoadForManagedClasses();
                
                List<String> nonTransformedClasses = detectNonTransformedClasses();
                if (CollectionUtils.isNotEmpty(nonTransformedClasses)) {
                    exceptionWithNonTransformed(nonTransformedClasses);
                }
                
                transformed = true;
            }
            if (transformed) {
                LOG.info("Did not recycle through class transformation since this has already occurred");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds all of the configured {@link #classTransformers} to all of the persistence units
     * @return whether or not there was a LoadTimeWeaver registered
     * @throws Exception if there was an undetectable problem during transformer addition
     */
    protected boolean addTransformersToPersistenceUnits() throws Exception {
        boolean weaverRegistered = true;
        for (PersistenceUnitInfo pui : mergedPus.values()) {
            if (pui.getProperties().containsKey(AvailableSettings.USE_CLASS_ENHANCER) && "true".equalsIgnoreCase(pui.getProperties().getProperty(AvailableSettings.USE_CLASS_ENHANCER))) {
                pui.addTransformer(new InterceptFieldClassFileTransformer(pui.getManagedClassNames()));
            }
            for (BroadleafClassTransformer transformer : classTransformers) {
                try {
                    boolean isTransformerQualified = !(transformer instanceof NullClassTransformer) &&
                        (
                            pui.getPersistenceUnitName().equals("blPU") &&
                            !(transformer instanceof BroadleafPersistenceUnitDeclaringClassTransformer)
                        ) ||
                        (
                            (transformer instanceof BroadleafPersistenceUnitDeclaringClassTransformer) &&
                            pui.getPersistenceUnitName().equals(((BroadleafPersistenceUnitDeclaringClassTransformer) transformer).getPersistenceUnitName())
                        );
                    if (isTransformerQualified) {
                        pui.addTransformer(transformer);
                    }
                } catch (Exception e) {
                    weaverRegistered = handleClassTransformerRegistrationProblem(transformer, e);
                }
            }
        }
        weaverRegistered = addNamedQueriesToPersistenceUnits(weaverRegistered);


        return weaverRegistered;
    }

    protected boolean addNamedQueriesToPersistenceUnits(boolean weaverRegistered) throws Exception {
        //Do this last in case any of the query config classes happens to cause an entity class to be loaded - they will
        // still be transformed by the previous registered transformers
        for (PersistenceUnitInfo pui : mergedPus.values()) {
            //Add annotated named query support from QueryConfiguration beans
            List<NamedQuery> namedQueries = new ArrayList<>();
            List<NamedNativeQuery> nativeQueries = new ArrayList<>();
            for (QueryConfiguration config : queryConfigurations) {
                if (pui.getPersistenceUnitName().equals(config.getPersistenceUnit())) {
                    NamedQueries annotation = config.getClass().getAnnotation(NamedQueries.class);
                    if (annotation != null) {
                        namedQueries.addAll(Arrays.asList(annotation.value()));
                    }
                    NamedNativeQueries annotation2 = config.getClass().getAnnotation(NamedNativeQueries.class);
                    if (annotation2 != null) {
                        nativeQueries.addAll(Arrays.asList(annotation2.value()));
                    }
                }
            }
            if (!namedQueries.isEmpty() || !nativeQueries.isEmpty()) {
                QueryConfigurationClassTransformer transformer = new QueryConfigurationClassTransformer(namedQueries, nativeQueries, pui.getManagedClassNames());
                try {
                    pui.addTransformer(transformer);
                } catch (Exception e) {
                    weaverRegistered = handleClassTransformerRegistrationProblem(transformer, e);
                }
            }
        }
        return weaverRegistered;
    }

    protected boolean handleClassTransformerRegistrationProblem(BroadleafClassTransformer transformer, Exception e) throws Exception {
        boolean weaverRegistered;
        Exception refined = ExceptionHelper.refineException(IllegalStateException.class, RuntimeException.class, e);
        if (refined instanceof IllegalStateException) {
            LOG.warn("A BroadleafClassTransformer is configured for this persistence unit, but Spring " +
                    "reported a problem (likely that a LoadTimeWeaver is not registered). As a result, " +
                    "the Broadleaf Commerce ClassTransformer ("+transformer.getClass().getName()+") is " +
                    "not being registered with the persistence unit. To resove this add a -javaagent:/path/to/spring-instrument.jar to the JVM args of the server");
            weaverRegistered = false;
        } else {
            throw refined;
        }
        return weaverRegistered;
    }

    /**
     * 
     * @param nonTransformedClasses the classes that were detected as having not been transformed
     * @throws ClassNotFoundException
     */
    protected void exceptionWithNonTransformed(List<String> nonTransformedClasses) throws ClassNotFoundException {
        exceptionIfRootBeanDefinition(nonTransformedClasses);
        boolean devtoolsFound = detectSpringBootDevtools();
        
        String msg = "The classes\n" + Arrays.toString(nonTransformedClasses.toArray()) + "\nare managed classes within the MergePersistenceUnitManager"
                + "\nbut were not detected as being transformed by the EntityMarkerClassTransformer. There can be multiple causes for this:"
                + "\n1. Session persistence is enabled in your servlet container (like Tomcat) and an entity object has been loaded by the container before"
                + " being loaded by the application's classloader. Ensure that session persistence is disabled; in Tomcat ensure that a <Manager pathname=\"\" /> element exists in your context.xml."
                + "\n2. You are inadvertently using class scanning to find a ServletContainerInitializer class, and your servlet container is loading all classes before transformers have been registered."
                + " If you are using a web.xml, ensure that there is an <absolute-ordering /> element somewhere in that file. If you are not using a web.xml and are using Spring Boot,"
                + " then you likely need to add one. See https://www.broadleafcommerce.com/docs/core/5.2/broadleaf-concepts/key-aspects-and-configuration/app-server-configuration/tomcat for the example web.xml"
                + "\n3. The classes are being used as apart of an @Bean method or in some other runtime capacity that is initialized prior to persistence manager startup";
        if (devtoolsFound) {
            msg += "\n4. Spring Boot Devtools is on the classpath and the Restarter capabilities are interfering. Spring Boot Devtools restarter functionality works by creating multiple ClassLoaders"
                + " and there is a check in InstrumentationLoadTimeWeaver to ensure that the ClassLoader for that class is the same as the ClassLoader for the entity class before"
                + " performing transformation. These ClassLoaders are different with Spring Devtools. You can attempt to disable just the Devtools restarter functionality while still utilizing"
                + " the other Devtools features by setting a JVM argument for spring.devtools.restart.enabled=false. See http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-devtools-restart-disable"
                + "\n for more information";
        }
        throw new IllegalStateException(msg);
    }
    
    /**
     * Detects whether or not Spring Devtools is on the classpath
     */
    protected boolean detectSpringBootDevtools() {
        String devtoolsClassname = "org.springframework.boot.devtools.restart.Restarter";
        return ClassUtils.isPresent(devtoolsClassname, getClass().getClassLoader());
    }
    
    /**
     * Validates whether or not the given <b>nonTransformedClasses</b> are contained in the root ApplicationContext and throws an IllegalStateException if so, else this does nothing
     * @param nonTransformedClasses classes that were not detected as having undergone class transformation
     * @throws IllegalStateException if any of the <b>nonTransformedClasses</b> are in the root ApplicationContext
     */
    protected void exceptionIfRootBeanDefinition(List<String> nonTransformedClasses) throws ClassNotFoundException {
        // figure out any bean definitions in the root application context that are also entity classes
        List<BeanDefinition> incorrectEntityBeanDefs = new ArrayList<>();
        for (String className : nonTransformedClasses) {
            String[] beanIds = applicationContext.getBeanNamesForType(Class.forName(className));
            for (String beanId : beanIds) {
                incorrectEntityBeanDefs.add(((BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory()).getBeanDefinition(beanId));
            }
        }
        if (CollectionUtils.isNotEmpty(incorrectEntityBeanDefs)) {
            String msg = "The following bean definitions for entity classes were detected in the Spring root ApplicationContext which prevents them from being correctly transformed. Ensure that bean definitions"
                + " for entity classes used for overriding are only in applicationContext-entity.xml-like files configured with the blMergedEntityContexts bean and do not undergo component scanning or any other"
                + " Spring ApplicationContext configuration."
                + "\n" + Arrays.toString(incorrectEntityBeanDefs.toArray());
            throw new IllegalStateException(msg);
        }
    }
    
    /**
     * If a class happened to be loaded by the ClassLoader before we had a chance to set up our instrumentation,
     * it may not be in a consistent state. This verifies with the EntityMarkerClassTransformer that it
     * actually saw the classes loaded by the above process
     * @return the list of classes that were detected as not transformed representing an error state
     */
    protected List<String> detectNonTransformedClasses() {
        List<String> nonTransformedClasses = new ArrayList<>();
        for (PersistenceUnitInfo pui : mergedPus.values()) {
            for (String managedClassName : pui.getManagedClassNames()) {
                // We came across a class that is not a real persistence class (doesn't have the right annotations)
                // but is still being transformed/loaded by
                // the persistence unit. This might have unexpected results downstream, but it could also be benign
                // so just output a warning
                if (entityMarkerClassTransformer.getTransformedNonEntityClassNames().contains(managedClassName)) {
                    LOG.warn("The class " + managedClassName + " is marked as a managed class within the MergePersistenceUnitManager"
                            + " but is not annotated with @Entity, @MappedSuperclass or @Embeddable."
                            + " This class is still referenced in a persistence.xml and is being transformed by"
                            + " PersistenceUnit ClassTransformers which may result in problems downstream"
                            + " and represents a potential misconfiguration. This class should be removed from"
                            + " your persistence.xml");
                } else if (!entityMarkerClassTransformer.getTransformedEntityClassNames().contains(managedClassName)) {
                    // This means the class not in the 'warning' list, but it is also not in the list that we would
                    // expect it to be in of valid entity classes that were transformed. This means that we
                    // never got the chance to transform the class AT ALL even though it is a valid entity class
                    nonTransformedClasses.add(managedClassName);
                }
            }
        }
        return nonTransformedClasses;
    }

    /**
     * Triggers a class load via this class's {@link ClassLoader} for all of the classes in all of the persistence units
     * 
     * @throws ClassNotFoundException if there was a problem in the class load
     * @return all of the classes that were loaded via this process
     */
    protected List<String> triggerClassLoadForManagedClasses() throws ClassNotFoundException {
        List<String> managedClassNames = new ArrayList<>();
        for (PersistenceUnitInfo pui : mergedPus.values()) {
            for (String managedClassName : pui.getManagedClassNames()) {
                if (!managedClassNames.contains(managedClassName)) {
                    // Force-load this class so that we are able to ensure our instrumentation happens globally.
                    // If transformation is happening, it should be tracked in EntityMarkerClassTransformer
                    Class.forName(managedClassName, true, getClass().getClassLoader());
                    managedClassNames.add(managedClassName);
                }
            }
        }
        return managedClassNames;
    }
    
    /**
     * Detects the presence of the {@link EntityMarkerClassTransformer} and throws an exception if this is misconfigured. If there
     * are no class transformes within {@link #mergedClassTransformers} then this does nothing
     */
    protected void exceptionIfEntityMarkerNotFound() {
        if (CollectionUtils.isNotEmpty(mergedClassTransformers)) {
            boolean foundEntityMarkerTransformer = IterableUtils.find(mergedClassTransformers, new Predicate<BroadleafClassTransformer>(){
    
                @Override
                public boolean evaluate(BroadleafClassTransformer object) {
                    return EntityMarkerClassTransformer.class.isAssignableFrom(object.getClass());
                }
                
            }) != null;
            
            if (!foundEntityMarkerTransformer) {
                BeanDefinition transformersBeanDef = ((BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory()).getBeanDefinition("blMergedClassTransformers");
                String msg = "The EntityMarkerClassTransformer was not detected as registered in the the list of blMergedClassTransformers. This is"
                    + " usually caused the blMergedClassTransformers being overridden in a different configuration. Without this transformer Broadleaf"
                    + " is unable to validate whether or not class transformation happened as expected. This bean was registered as " + transformersBeanDef
                    + " but it should have been detected as registerd in bl-common-applicationContext.xml. Change the definition in " + transformersBeanDef.getResourceDescription()
                    + " to instead utilize the EarlyStageMergeBeanPostProcessor in XML or an @Merge(targetRef=\"blMergedClassTransformers\" early = true) in a Java configuration class";
                throw new IllegalStateException(msg);
            }
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
        disableSchemaCreateIfApplicable(persistenceUnitName, pui);
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

    protected void disableSchemaCreateIfApplicable(String persistenceUnitName, MutablePersistenceUnitInfo pui) {
        String autoDDLStatus = pui.getProperties().getProperty("hibernate.hbm2ddl.auto");
        boolean isCreate = autoDDLStatus != null && (autoDDLStatus.equals("create") || autoDDLStatus.equals("create-drop"));
        boolean detectedCreate = false;
        if (isCreate && mBeanExporter != null) {
            try {
                if (mBeanExporter.getServer().isRegistered(ObjectName.getInstance("bean:name=autoDDLCreateStatusTestBean"))) {
                    Boolean response = (Boolean) mBeanExporter.getServer().invoke(ObjectName.getInstance("bean:name=autoDDLCreateStatusTestBean"), "getStartedWithCreate",
                            new Object[]{persistenceUnitName}, new String[]{String.class.getName()});
                    if (response == null) {
                        mBeanExporter.getServer().invoke(ObjectName.getInstance("bean:name=autoDDLCreateStatusTestBean"), "setStartedWithCreate",
                            new Object[]{persistenceUnitName, true}, new String[]{String.class.getName(), Boolean.class.getName()});
                    } else {
                        detectedCreate = true;
                    }
                }
            } catch (Exception e) {
                LOG.warn("Unable to query the mbean server for previous auto.ddl status", e);
            }
        }
        if (detectedCreate) {
            pui.getProperties().setProperty("hibernate.hbm2ddl.auto", "none");
        }
    }
}
