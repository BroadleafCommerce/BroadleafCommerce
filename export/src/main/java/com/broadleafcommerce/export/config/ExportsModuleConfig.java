/*
 * #%L
 * BroadleafCommerce Export Module
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
package com.broadleafcommerce.export.config;

import org.broadleafcommerce.common.config.FrameworkCommonPropertySource;
import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.common.logging.LifeCycleEvent;
import org.broadleafcommerce.common.logging.ModuleLifecycleLoggingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration("blExportsModuleConfig")
public class ExportsModuleConfig {

    @Bean
    public static ModuleLifecycleLoggingBean blExportLifecycleBean() {
        ModuleLifecycleLoggingBean mllb = new ModuleLifecycleLoggingBean();
        mllb.setModuleName("Export");
        mllb.setLifeCycleEvent(LifeCycleEvent.LOADING);
        return mllb;
    }

    @Bean
    public FrameworkCommonPropertySource blExportPropertySource() {
        return new FrameworkCommonPropertySource("config/bc/exports");
    }
    
    @Merge(targetRef = "blMergedPersistenceXmlLocations", early = true)
    public List<String> blExportPersistenceXmlLocations() {
        return Arrays.asList("classpath*:/META-INF/persistence-export.xml");
    }
    
    @Merge(targetRef = "blMergedEntityContexts", early = true)
    public List<String> blExportEntityContexts() {
        return Arrays.asList("classpath:bl-export-applicationContext-entity.xml");
    }
}
