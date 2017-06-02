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
package org.broadleafcommerce.common.config;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Adds {@code META-INF/spring.factories} entries of type {@link FrameworkCommonClasspathPropertySource} and {@link BroadleafSharedOverrideProfileAwarePropertySource}
 * to the current {@link Environment}. All property sources that this initializer adds are added as composite sources to the {@link Environment} with
 * different priorities, and are added relative to each other via the {@link AnnotationAwareOrderComparator}. The {@link PropertySource}s are in the {@link Environment}
 * in the following order, all as {@link CompositePropertySource}s:
 * 
 * <ol>
 *  <li>{@link #OVERRIDE_SOURCES_NAME} - An external property file given with {@code -Dproperty-override}</li>
 *  <li>{@link #PROFILE_AWARE_SOURCES_NAME} - All {@link BroadleafSharedOverrideProfileAwarePropertySource} entries from {@code META-INF/spring.factories}</li>
 *  <li>{@link #FRAMEWORK_SOURCES_NAME} - All {@link FrameworkCommonClasspathPropertySource} entries from {@code META-IF/spring.factories}</li>
 * </ol>
 * 
 * <p>
 * If no Spring profile is active, this will default to {@code "development"} and add that profile to {@link ConfigurableEnvironment#getActiveProfiles()} in order
 * to maintain backwards compatibility with Broadleaf versions prior to 5.2.
 * 
 * <p>
 * This is by default added into {@code META-INF/spring.factories} with the {@code org.springframework.context.ApplicationContextInitializer} key. In non-boot
 * applications this must be added manually like in a {@code web.xml} with:
 * 
 * <pre>
 * {@literal
 * <context-param>
 *   <param-name>contextInitializerClasses</param-name>
 *   <param-value>org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener</param-value>
 * </context-param>
 * }
 * </pre>
 * 
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 * @since 5.2
 * @see BroadleafSharedOverrideProfileAwarePropertySource
 * @see FrameworkCommonClasspathPropertySource
 */
public class BroadleafEnvironmentConfiguringApplicationListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    /**
     * A -D argument representing a path to a file that overrides all of the other properties resolved from internal property files
     */
    public static final String PROPERTY_OVERRIDES_PROPERTY = "property-override";
    public static final String PROPERTY_SHARED_OVERRIDES_PROPERTY = "property-shared-override";
    public static final String DEPRECATED_RUNTIME_ENVIRONMENT_KEY = "runtime.environment";
    
    /**
     * The name of the Broadleaf framework composite properties within the Environment, useful for ordering before and after
     */
    public static final String FRAMEWORK_SOURCES_NAME = "broadleafFrameworkSources";
    
    /**
     * The name of the profile-aware property sources
     */
    public static final String PROFILE_AWARE_SOURCES_NAME = "broadleafProfileAwareSources";
    
    /**
     * The name of the the property source from the command line -Dproperty-shared-override
     */
    public static final String SHARED_OVERRIDE_SOURCES_NAME = "broadleafCommandlineArgumentSharedOverridesSource";
    
    /**
     * The name of the the property source from the command line -Dproperty-override
     */
    public static final String OVERRIDE_SOURCES_NAME = "broadleafCommandlineArgumentOverridesSource";
    
    
    private static final Log LOG = LogFactory.getLog(BroadleafEnvironmentConfiguringApplicationListener.class);
    
    protected List<FrameworkCommonClasspathPropertySource> getFrameworkSources() {
        return SpringFactoriesLoader.loadFactories(FrameworkCommonClasspathPropertySource.class, null);
    }
    
    protected List<BroadleafSharedOverrideProfileAwarePropertySource> getProfileAwareSources() {
        return SpringFactoriesLoader.loadFactories(BroadleafSharedOverrideProfileAwarePropertySource.class, null);
    }
    
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        // first add all of the framework property sources which should be 'common.properties'
        List<FrameworkCommonClasspathPropertySource> frameworkSources = getFrameworkSources();
        for (FrameworkCommonClasspathPropertySource source : frameworkSources) {
            String configLocation = source.getClasspathFolder();
            
            Resource commonProp = createClasspathResource(configLocation, "common", null);
            if (commonProp.exists()) {
                addToEnvironment(env, Arrays.asList(commonProp), FRAMEWORK_SOURCES_NAME, null);
            }
        }
        
        String deprecatedRuntimeEnvironment = env.getProperty(DEPRECATED_RUNTIME_ENVIRONMENT_KEY);
        if (StringUtils.isNotBlank(deprecatedRuntimeEnvironment)) {
            LOG.warn("The use of -Druntime.environment is deprecated in favor of Spring Profiles and will be removed in a future release. To specify a profile as a -D argument use -Dspring.profiles.active as a drop-in replacement"
                + " for -Druntime.environment. Adding " + deprecatedRuntimeEnvironment + " to the list of active Spring profiles.");
            env.addActiveProfile(deprecatedRuntimeEnvironment);
        }
        
        List<Resource> commonSharedResources = new ArrayList<>();
        List<Resource> commonResources = new ArrayList<>();
        List<Resource> profileSpecificSharedResources = new ArrayList<>();
        List<Resource> profileSpecificResources = new ArrayList<>();
        List<BroadleafSharedOverrideProfileAwarePropertySource> profileAwareSources = getProfileAwareSources();
        for (BroadleafSharedOverrideProfileAwarePropertySource source : profileAwareSources) {
            // then add all of the user property sources
            String configLocation = source.getClasspathFolder();
            
            Resource commonSharedProp = createClasspathResource(configLocation, "common", "shared");
            commonSharedResources.add(commonSharedProp);
            Resource commonProp = createClasspathResource(configLocation, "common", null);
            commonResources.add(commonProp);
            
            String[] activeProfiles = env.getActiveProfiles();
            if (ArrayUtils.isNotEmpty(activeProfiles)) {
                for (String profile : activeProfiles) {
                    Resource profileSpecificSharedProps = createClasspathResource(configLocation, profile, "shared");
                    profileSpecificSharedResources.add(profileSpecificSharedProps);
                   
                    Resource profileSpecificProps = createClasspathResource(configLocation, profile, null);
                    profileSpecificResources.add(profileSpecificProps);
                }
            } else {
                String[] defaultProfiles = env.getDefaultProfiles();
                
                for (String defaultProfile : defaultProfiles) {
                    Resource profileSpecificSharedProps = createClasspathResource(configLocation, defaultProfile, "shared");
                    profileSpecificSharedResources.add(profileSpecificSharedProps);
                   
                    Resource profileSpecificProps = createClasspathResource(configLocation, defaultProfile, null);
                    profileSpecificResources.add(profileSpecificProps);
                    
                }
                
                String deprecatedDefaultProfile = getDeprecatedDefaultProfileKey();
                if (!ArrayUtils.contains(defaultProfiles, deprecatedDefaultProfile)) {
                    Resource developmentSharedProps = createClasspathResource(configLocation, deprecatedDefaultProfile, "shared");
                    profileSpecificSharedResources.add(developmentSharedProps);
                   
                    Resource developmentProps = createClasspathResource(configLocation, deprecatedDefaultProfile, null);
                    profileSpecificResources.add(developmentProps);
                    
                    boolean deprecatedDefaultProfileFound = developmentSharedProps.exists() || developmentProps.exists();
                    
                    if (deprecatedDefaultProfileFound) {
                        LOG.warn("The usage of " + getDeprecatedDefaultProfileKey() + ".properties is deprecated and will be removed in a future release. Use Spring's default profile properties of 'default.properties'."
                            + " Alternatively, set the 'spring.profiles.default' system property with -Dspring.profiles.default=development to change the default profile name that Spring runs in.");
                        env.setDefaultProfiles(ArrayUtils.add(defaultProfiles, deprecatedDefaultProfile));
                    }
                }
            }
        }
        
        addToEnvironment(env, commonSharedResources, PROFILE_AWARE_SOURCES_NAME, FRAMEWORK_SOURCES_NAME);
        addToEnvironment(env, commonResources, PROFILE_AWARE_SOURCES_NAME, FRAMEWORK_SOURCES_NAME);
        addToEnvironment(env, profileSpecificSharedResources, PROFILE_AWARE_SOURCES_NAME, FRAMEWORK_SOURCES_NAME);
        addToEnvironment(env, profileSpecificResources, PROFILE_AWARE_SOURCES_NAME, FRAMEWORK_SOURCES_NAME);
        
        // At the very end of all of it, look at the property-override and property-shared-overrides locations and add that higher than the profile aware ones
        String sharedOverrideFileLocation = env.getProperty(PROPERTY_SHARED_OVERRIDES_PROPERTY);
        String currentHighestPrecedenceProperties = PROFILE_AWARE_SOURCES_NAME;
        if (StringUtils.isNotBlank(sharedOverrideFileLocation)) {
            Resource sharedOverrideFileResource = new FileSystemResource(sharedOverrideFileLocation);
            if (sharedOverrideFileResource.exists()) {
                addToEnvironment(env, Arrays.asList(sharedOverrideFileResource), SHARED_OVERRIDE_SOURCES_NAME, PROFILE_AWARE_SOURCES_NAME);
                currentHighestPrecedenceProperties = SHARED_OVERRIDE_SOURCES_NAME;
            } else {
                LOG.warn(String.format("An environment property of %s was specified but the file path %s does not exist, not overriding properties", PROPERTY_SHARED_OVERRIDES_PROPERTY, sharedOverrideFileLocation));
            }
        }
        
        String overrideFileLocation = env.getProperty(PROPERTY_OVERRIDES_PROPERTY);
        if (StringUtils.isNotBlank(overrideFileLocation)) {
            Resource overrideFileResource = new FileSystemResource(overrideFileLocation);
            if (overrideFileResource.exists()) {
                addToEnvironment(env, Arrays.asList(overrideFileResource), OVERRIDE_SOURCES_NAME, currentHighestPrecedenceProperties);
            } else {
                LOG.warn(String.format("An environment property of %s was specified but the file path %s does not exist, not overriding properties", PROPERTY_OVERRIDES_PROPERTY, overrideFileLocation));
            }
        }
    }
    
    protected Resource createClasspathResource(String rootLocation, String propertyName, String suffix) {
        suffix = (StringUtils.isEmpty(suffix)) ? "" : "-" + suffix;
        String fileName = propertyName + suffix + ".properties";
        return new ClassPathResource(FilenameUtils.concat(rootLocation, fileName));
    }
    
    /**
     * <p>
     * Adds the specified <b>resource</b> as a {@link PropertySource} to the given <b>environment</b> at the order from <b>addBeforeResourceName</b>. If the
     * <b>resource</b> does not exist (meaning {@code resource.exists() == false}) then this immediately returns <b>addBeforeResourceName</b>
     * 
     * <p>
     * If <b>addBeforeResourceName</b> is null, the given <b>resource</b> will be added last via {@link MutablePropertySources#addLast(PropertySource)}.
     */
    protected void addToEnvironment(ConfigurableEnvironment environment, List<Resource> resources, String compositeSourceName, String addBeforeSourceName) {
        try {
            for (Resource resource : resources) {
                if (!resource.exists()) {
                    LOG.debug(resource.getDescription() + " does not exist, skipping adding to the Environment");
                    continue;
                }
                PropertySource<?> props = new ResourcePropertySource(resource);
                
                CompositePropertySource compositeSource = (CompositePropertySource) environment.getPropertySources().get(compositeSourceName);
                if (compositeSource == null) {
                    compositeSource = new CompositePropertySource(compositeSourceName);
                    if (addBeforeSourceName == null) {
                        environment.getPropertySources().addLast(compositeSource);
                    } else {
                        environment.getPropertySources().addBefore(addBeforeSourceName, compositeSource);
                    }
                    LOG.debug("Added new composite property source source " + compositeSource.getName() + " to the environment");
                }
                
                compositeSource.addFirstPropertySource(props);
                LOG.debug(String.format("Added property source %s at the beginning of the composite source ", props.getName(), compositeSource.getName()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getDeprecatedDefaultProfileKey() {
        return "development";
    }

}
