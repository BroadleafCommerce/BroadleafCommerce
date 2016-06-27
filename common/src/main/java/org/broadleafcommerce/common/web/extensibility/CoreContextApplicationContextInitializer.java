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
package org.broadleafcommerce.common.web.extensibility;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.SystemPropertyRuntimeEnvironmentKeyResolver;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extensibility.context.StandardConfigLocations;
import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jeff Fischer
 */
@Component("blCoreContextApplicationContextInitializer")
public class CoreContextApplicationContextInitializer implements ApplicationContextInitializer<XmlWebApplicationContext>, Ordered {

    private static final Log LOG = LogFactory.getLog(CoreContextApplicationContextInitializer.class);

    protected static final String SHARED_PROPERTY_OVERRIDE = "property-shared-override";
    protected static final String PROPERTY_OVERRIDE = "property-override";
    protected static Set<String> defaultEnvironments = new LinkedHashSet<String>();
    protected static Set<Resource> blcPropertyLocations = new LinkedHashSet<Resource>();
    protected static Set<Resource> defaultPropertyLocations = new LinkedHashSet<Resource>();

    static {
        defaultEnvironments.add("production");
        defaultEnvironments.add("staging");
        defaultEnvironments.add("integrationqa");
        defaultEnvironments.add("integrationdev");
        defaultEnvironments.add("development");

        blcPropertyLocations.add(new ClassPathResource("config/bc/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/admin/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/cms/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/web/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/fw/"));

        defaultPropertyLocations.add(new ClassPathResource("runtime-properties/"));
    }

    protected SupportLogger logger = SupportLogManager.getLogger("UserOverride", this.getClass());
    protected String defaultEnvironment = "development";
    protected String determinedEnvironment = null;
    protected String startPattern = ".*(\\<\\s*bean\\s+id\\s*=\\s*\"blPropertySources\").*";
    protected String endPattern = ".*\\<\\s*\\/bean.*";
    protected String valuePattern = ".*(\\<value\\>\\s*(.*)\\s*\\<\\/value\\>).*";
    protected String importPattern = ".*\\<import\\s+resource=\"(.*)\".*";
    protected String startCommentPattern = ".*(\\<\\!--).*";
    protected String endCommentPattern = ".*(--\\>).*";
    protected Pattern start = Pattern.compile(startPattern);
    protected Pattern end = Pattern.compile(endPattern);
    protected Pattern value = Pattern.compile(valuePattern);
    protected Pattern _import = Pattern.compile(importPattern);
    protected Pattern startComment = Pattern.compile(startCommentPattern);
    protected Pattern endComment = Pattern.compile(endCommentPattern);
    protected Set<String> environments = Collections.emptySet();

    @Override
    public void initialize(XmlWebApplicationContext applicationContext) {
        String[] appConfigLocations = applicationContext.getConfigLocations();
        String[] broadleafConfigLocations;
        try {
            broadleafConfigLocations = StandardConfigLocations.retrieveAll(StandardConfigLocations.APPCONTEXTTYPE);
        } catch (IOException e) {
            throw ExceptionHelper.refineException(e);
        }
        for (int j=0;j<broadleafConfigLocations.length;j++){
            broadleafConfigLocations[j] = "classpath:/" + broadleafConfigLocations[j];
        }
        String[] combined = new String[appConfigLocations.length + broadleafConfigLocations.length];
        System.arraycopy(broadleafConfigLocations, 0, combined, 0, broadleafConfigLocations.length);
        System.arraycopy(appConfigLocations, 0, combined, broadleafConfigLocations.length, appConfigLocations.length);
        applicationContext.setConfigLocations(combined);

        try {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Set<Resource> locations = new HashSet<Resource>();
            buildPropertySources(applicationContext, combined, locations);
            setupPropertySources(environment, locations);
        } catch (IOException e) {
            throw ExceptionHelper.refineException(e);
        }
    }

    protected void buildPropertySources(XmlWebApplicationContext applicationContext, String[] combined, Set<Resource> locations) throws IOException {
        List<String> imports = new ArrayList<String>();
        for (String location : combined) {
            extractPropertySource(applicationContext, location, imports, locations);
        }
        if (!CollectionUtils.isEmpty(imports)) {
            buildPropertySources(applicationContext, imports.toArray(new String[imports.size()]), locations);
        }
    }

    protected boolean isCommentedInLine(Integer matchPos, List<Integer> startCommentPos, List<Integer> endCommentPos, boolean isCommentStarted) {
        boolean response = false;
        if (startCommentPos.isEmpty() && isCommentStarted) {
            for (Integer endPos : endCommentPos) {
                if (endPos > matchPos) {
                    response = true;
                    break;
                }
            }
        } else {
            for (Integer startPos : startCommentPos) {
                if (startPos < matchPos) {
                    for (Integer endPos : endCommentPos) {
                        if (startPos < endPos && endPos > matchPos) {
                            response = true;
                            break;
                        }
                    }
                    if (!response) {
                        response = endCommentPos.isEmpty();
                        if (!response) {
                            for (Integer endPos : endCommentPos) {
                                response = startPos > endPos && startPos < matchPos;
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    protected boolean isCommentStartedBlock(List<Integer> startCommentPos, List<Integer> endCommentPos) {
        boolean response = !startCommentPos.isEmpty() && endCommentPos.isEmpty();
        for (Integer startPos : startCommentPos) {
            for (Integer endPos : endCommentPos) {
                if (startPos > endPos) {
                    response = true;
                } else {
                    response = false;
                }
            }
        }
        return response;
    }

    protected boolean isCommentEndedBlock(List<Integer> startCommentPos, List<Integer> endCommentPos) {
        boolean response = !endCommentPos.isEmpty() && startCommentPos.isEmpty();
        for (Integer endPos : endCommentPos) {
            for (Integer startPos : startCommentPos) {
                if (endPos > startPos) {
                    response = true;
                } else {
                    response = false;
                }
            }
        }
        return response;
    }

    protected void extractPropertySource(XmlWebApplicationContext applicationContext, String location, List<String> imports, Set<Resource> locations) throws IOException {
        Resource[] resources = applicationContext.getResources(location);
        for (Resource resource : resources) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                boolean eof = false; //end of file
                boolean isCommentStarted = false;
                boolean isCommentEnded = false;
                boolean isSectionStarted = false;
                while (!eof) {
                    String line = reader.readLine();
                    if (line == null) {
                        eof = true;
                    } else {
                        List<Integer> startCommentPos = new ArrayList<Integer>();
                        Matcher startCommentMatcher = startComment.matcher(line);
                        if (startCommentMatcher.matches()) {
                            for (int j=1;j<=startCommentMatcher.groupCount();j++) {
                                startCommentPos.add(startCommentMatcher.end(j));
                            }
                        }
                        List<Integer> endCommentPos = new ArrayList<Integer>();
                        Matcher endCommentMatcher = endComment.matcher(line);
                        if (endCommentMatcher.matches()) {
                            for (int j=1;j<=endCommentMatcher.groupCount();j++) {
                                endCommentPos.add(endCommentMatcher.end(j));
                            }
                        }
                        if (!isCommentStarted) {
                            isCommentStarted = isCommentStartedBlock(startCommentPos, endCommentPos);
                            isCommentEnded = !isCommentStarted;
                        }
                        Matcher importMatcher = _import.matcher(line);
                        if (importMatcher.matches()) {
                            if (!isCommentedInLine(importMatcher.end(1), startCommentPos, endCommentPos, isCommentStarted) && !isCommentStarted) {
                                imports.add(importMatcher.group(1));
                            }
                        } else {
                            Matcher startMatcher = start.matcher(line);
                            if (startMatcher.matches()) {
                                isSectionStarted = true;
                            }
                            if (isSectionStarted) {
                                Matcher valueMatcher = value.matcher(line);
                                if (valueMatcher.matches()) {
                                    if (!isCommentedInLine(valueMatcher.end(1), startCommentPos, endCommentPos, isCommentStarted) && !isCommentStarted) {
                                        locations.add(applicationContext.getResource(valueMatcher.group(2)));
                                    }
                                } else {
                                    Matcher endMatcher = end.matcher(line);
                                    isSectionStarted = !endMatcher.matches();
                                }
                            }
                        }
                        if (!isCommentEnded) {
                            isCommentEnded = isCommentEndedBlock(startCommentPos, endCommentPos);
                            isCommentStarted = !isCommentEnded;
                        }
                    }
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            }
        }
    }

    public void setupPropertySources(ConfigurableEnvironment configurableEnvironment, Set<Resource> declaredPropertySourceLocations) throws IOException {
        // If no environment override has been specified, used the default environments
        if (environments == null || environments.size() == 0) {
            environments = defaultEnvironments;
        }

        // Prepend the default property locations to the specified property locations (if any)
        Set<Resource> combinedLocations = new LinkedHashSet<Resource>();
        if (!CollectionUtils.isEmpty(declaredPropertySourceLocations)) {
            combinedLocations.addAll(declaredPropertySourceLocations);
        }

        if (!environments.contains(defaultEnvironment)) {
            throw new AssertionError("Default environment '" + defaultEnvironment + "' not listed in environment list");
        }

        String environment = determineEnvironment();
        ArrayList<Resource> allLocations = new ArrayList<Resource>();

        /* Process configuration in the following order (later files override earlier files
         * common-shared.properties
         * [environment]-shared.properties
         * common.properties
         * [environment].properties
         * -Dproperty-override-shared specified value, if any
         * -Dproperty-override specified value, if any  */
        Set<Set<Resource>> testLocations = new LinkedHashSet<Set<Resource>>();
        testLocations.add(combinedLocations);
        testLocations.add(defaultPropertyLocations);

        for (Resource resource : createBroadleafResource()) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }

        for (Set<Resource> locations : testLocations) {
            for (Resource resource : createSharedCommonResource(locations)) {
                if (resource.exists()) {
                    allLocations.add(resource);
                }
            }

            for (Resource resource : createSharedPropertiesResource(environment, locations)) {
                if (resource.exists()) {
                    allLocations.add(resource);
                }
            }

            for (Resource resource : createCommonResource(locations)) {
                if (resource.exists()) {
                    allLocations.add(resource);
                }
            }

            for (Resource resource : createPropertiesResource(environment, locations)) {
                if (resource.exists()) {
                    allLocations.add(resource);
                }
            }
        }

        Resource sharedPropertyOverride = createSharedOverrideResource();
        if (sharedPropertyOverride != null) {
            allLocations.add(sharedPropertyOverride);
        }

        Resource propertyOverride = createOverrideResource();
        if (propertyOverride != null) {
            allLocations.add(propertyOverride);
        }

        Properties props = new Properties();
        for (Resource resource : allLocations) {
            if (resource.exists()) {
                // We will log source-control managed properties with trace and overrides with info
                if (((resource.equals(sharedPropertyOverride) || resource.equals(propertyOverride)))
                        || LOG.isTraceEnabled()) {
                    props = new Properties(props);
                    props.load(resource.getInputStream());
                    for (Map.Entry<Object, Object> entry : props.entrySet()) {
                        if (resource.equals(sharedPropertyOverride) || resource.equals(propertyOverride)) {
                            logger.support("Read " + entry.getKey() + " from " + resource.getFilename());
                        } else {
                            LOG.trace("Read " + entry.getKey() + " from " + resource.getFilename());
                        }
                    }
                }
            } else {
                LOG.debug("Unable to locate resource: " + resource.getFilename());
            }
        }
        for (Resource location : allLocations) {
            configurableEnvironment.getPropertySources().addFirst(new ResourcePropertySource(location));
        }
    }

    protected Resource[] createBroadleafResource() throws IOException {
        Resource[] resources = new Resource[blcPropertyLocations.size()];
        int index = 0;
        for (Resource resource : blcPropertyLocations) {
            resources[index] = resource.createRelative("common.properties");
            index++;
        }
        return resources;
    }

    protected Resource[] createSharedCommonResource(Set<Resource> locations) throws IOException {
        Resource[] resources = new Resource[locations.size()];
        int index = 0;
        for (Resource resource : locations) {
            resources[index] = resource.createRelative("common-shared.properties");
            index++;
        }
        return resources;
    }

    protected Resource[] createSharedPropertiesResource(String environment, Set<Resource> locations) throws IOException {
        String fileName = environment.toString().toLowerCase() + "-shared.properties";
        Resource[] resources = new Resource[locations.size()];
        int index = 0;
        for (Resource resource : locations) {
            resources[index] = resource.createRelative(fileName);
            index++;
        }
        return resources;
    }

    protected Resource[] createCommonResource(Set<Resource> locations) throws IOException {
        Resource[] resources = new Resource[locations.size()];
        int index = 0;
        for (Resource resource : locations) {
            resources[index] = resource.createRelative("common.properties");
            index++;
        }
        return resources;
    }

    protected Resource[] createPropertiesResource(String environment, Set<Resource> locations) throws IOException {
        String fileName = environment.toString().toLowerCase() + ".properties";
        Resource[] resources = new Resource[locations.size()];
        int index = 0;
        for (Resource resource : locations) {
            resources[index] = resource.createRelative(fileName);
            index++;
        }
        return resources;
    }

    protected Resource createSharedOverrideResource() throws IOException {
        String path = System.getProperty(SHARED_PROPERTY_OVERRIDE);
        return StringUtils.isBlank(path) ? null : new FileSystemResource(path);
    }

    protected Resource createOverrideResource() throws IOException {
        String path = System.getProperty(PROPERTY_OVERRIDE);
        return StringUtils.isBlank(path) ? null : new FileSystemResource(path);
    }

    public String determineEnvironment() {
        if (determinedEnvironment != null) {
            return determinedEnvironment;
        }
        determinedEnvironment = new SystemPropertyRuntimeEnvironmentKeyResolver().resolveRuntimeEnvironmentKey();

        if (determinedEnvironment == null) {
            LOG.warn("Unable to determine runtime environment, using default environment '" + defaultEnvironment + "'");
            determinedEnvironment = defaultEnvironment;
        }

        return determinedEnvironment.toLowerCase();
    }

    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
