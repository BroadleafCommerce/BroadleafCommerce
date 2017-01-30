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
