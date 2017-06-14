/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.common.config.PostAutoConfigurationImport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * Borrows much of its config from {@link MultipartAutoConfiguration} but overrides the default property
 * used for max upload size
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
@PostAutoConfigurationImport(NonAutoconfigMultiPartConfiguration.class)
@EnableConfigurationProperties(MultipartProperties.class)
public class AdminMultipartUploadConfig {
    
    public static final long DEFAULT_10_MEGABYTES = 10000000;
    
    protected MultipartProperties multipartProperties;
    
    public AdminMultipartUploadConfig(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }
    
    @Bean
    public MultipartConfigElement multipartConfigElement(@Value("${asset.server.max.uploadable.file.size}") long blcMaxFileSize ) {
        MultipartConfigElement multipartConfig = this.multipartProperties.createMultipartConfig();
        // If a user has configured the max file size from Broadleaf to be anything but the default,
        // override it here
        if (blcMaxFileSize != DEFAULT_10_MEGABYTES) {
            // TODO: warn that using asset.server.max.uploadable.file.size is deprecated, docs to change the defaults
            multipartConfig = new MultipartConfigElement(multipartConfig.getLocation(),
                multipartConfig.getMaxRequestSize(),
                blcMaxFileSize,
                multipartConfig.getFileSizeThreshold());
        }
        return multipartConfig;
    }
    
}
