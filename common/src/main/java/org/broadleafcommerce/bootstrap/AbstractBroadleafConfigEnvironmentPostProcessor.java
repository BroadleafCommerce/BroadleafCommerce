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
package org.broadleafcommerce.bootstrap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Jeff Fischer
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public abstract class AbstractBroadleafConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private PropertySourcesLoader propertySourceLoader = new PropertySourcesLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String suffix = getPropertySuffix();
        if (!StringUtils.isEmpty(suffix)) {
            suffix = "-" + suffix;
        } else {
            suffix = "";
        }
        Resource commonProp = new ClassPathResource(getConfigLocation() + "common" + suffix + ".properties");
        if (commonProp.exists()) {
            load(environment, commonProp, null);
        }
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length > 0) {
            for (String profile : profiles) {
                Resource profileProp = new ClassPathResource(getConfigLocation() + profile + suffix + ".properties");
                load(environment, profileProp, profile);
            }
        } else {
            String profile = getDefaultEnvironmentKey();
            Resource profileProp = new ClassPathResource(getConfigLocation() + profile + suffix + ".properties");
            load(environment, profileProp, profile);
        }
    }

    protected void load(ConfigurableEnvironment environment, Resource resource, String profile) {
        try {
            PropertySource<?> propertySource = propertySourceLoader.load(resource, null);
            if (propertySource != null) {
                if (profile == null) {
                    environment.getPropertySources().addLast(propertySource);
                } else {
                    Iterator<PropertySource<?>> itr = environment.getPropertySources().iterator();
                    boolean found = false;
                    while (itr.hasNext()) {
                        PropertySource<?> source = itr.next();
                        if (source.getName().contains("common.properties")) {
                            environment.getPropertySources().addBefore(source.getName(), propertySource);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        environment.getPropertySources().addLast(propertySource);
                    }
                    if (!ArrayUtils.contains(environment.getActiveProfiles(), profile)) {
                        environment.addActiveProfile(profile);
                    }
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    protected abstract String getConfigLocation();

    protected String getPropertySuffix() {
        return null;
    }

    protected String getDefaultEnvironmentKey() {
        return "development";
    }
}
