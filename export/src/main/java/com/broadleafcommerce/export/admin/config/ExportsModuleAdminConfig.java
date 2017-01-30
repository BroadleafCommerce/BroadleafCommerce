/**
 * 
 */
package com.broadleafcommerce.export.admin.config;

import org.broadleafcommerce.common.admin.condition.ConditionalOnAdmin;
import org.broadleafcommerce.common.demo.AutoImportPersistenceUnit;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.broadleafcommerce.common.demo.AutoImportStage;
import org.broadleafcommerce.common.demo.ImportCondition;
import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.resolver.BroadleafClasspathTemplateResolver;
import org.broadleafcommerce.presentation.resolver.BroadleafTemplateMode;
import org.broadleafcommerce.presentation.resolver.BroadleafTemplateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.broadleafcommerce.export.admin.server.security.service.ExportRowLevelSecurityProvider;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@ConditionalOnAdmin
@Configuration("blExportsModuleAdminConfig")
public class ExportsModuleAdminConfig {
    
    @Merge(targetRef = "blMessageSourceBaseNames")
    public List<String> blExportMessages() {
        return Arrays.asList("classpath:/messages/ExportMessages");
    }
    
    @Merge(targetRef = "blRowLevelSecurityProviders")
    public List<ExportRowLevelSecurityProvider> blExportRowLevelSecurityProviders(@Autowired ExportRowLevelSecurityProvider securityProvider) {
        return Arrays.asList(securityProvider);
    }
    
    @ConditionalOnTemplating
    @Configuration
    public static class ExportsAdminFrontendConfig {
        
        @Autowired
        protected Environment env;
        
        @Merge(targetRef = "blJsLocations")
        public List<String> blExportAdminJsLocations() {
            return Arrays.asList("classpath:/export_style/js/");
        }
        
        @Merge(targetRef = "blJsFileList")
        public List<String> blExportAdminJsFileList() {
            return Arrays.asList("admin/export.js");
        }
        
        @Bean
        public BroadleafTemplateResolver blAdminExportClasspathTemplateResolver() {
            BroadleafClasspathTemplateResolver resolver = new BroadleafClasspathTemplateResolver();
            resolver.setPrefix("export_style/templates/admin/");
            resolver.setSuffix(".html");
            resolver.setTemplateMode(BroadleafTemplateMode.HTML);
            resolver.setCharacterEncoding("UTF-8");
            resolver.setCacheable(env.getProperty("cache.page.templates", Boolean.class));
            resolver.setCacheTTLMs(env.getProperty("cache.page.templates.ttl", Long.class));
            return resolver;
        }
    }
    
    @Conditional(ImportCondition.class)
    @Configuration
    public static class ExportsData {
        
        @Bean
        public AutoImportSql blExportsImportSql() {
            return new AutoImportSql(AutoImportPersistenceUnit.BL_PU, "config/bc/sql/load_export_admin_security.sql", AutoImportStage.PRIMARY_FRAMEWORK_SECURITY);
        }
    }

}
