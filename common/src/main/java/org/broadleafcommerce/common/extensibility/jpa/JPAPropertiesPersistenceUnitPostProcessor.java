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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.broadleafcommerce.common.demo.CompositeAutoImportSql;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 *   This class allows us to override Persistence Unit properties on a per-environment basis. Spring's
 *   PersistenceUnitManager allows us to pass in a list of PersistenceUnitPostProcessors.  This class will allow us to override
 *   or add custom JPA properties to the Persistence Unit.  This is useful when different environments have different requirements
 *   for JPA properties.  The best example of this is SQL Dialect.  You may want to use a dialect such as Oracle for production
 *   and test environments, and perhaps HSQLDB for local and integration testing.  You can set the dialect property using the
 *   <code>RuntimeEnvironmentPropertiesConfigurer</code>.  The keys will be the same in each environment, but the values
 *   would be defined the environment-specific properties files.  If you want the property to be added only to certain environments,
 *   add the value "null" to the properties file.  For example:
 *
 *
 *   <bean id="blPersistenceUnitManager" class="org.broadleafcommerce.profile.extensibility.jpa.MergePersistenceUnitManager">
 *       <property name="persistenceXmlLocations">
 *           <list>
 *               <value>classpath*:/META-INF/mycompany_persistence.xml</value>
 *           </list>
 *        </property>
 *        <property name="persistenceUnitPostProcessors">
 *            <list>
 *                <bean class="org.broadleafcommerce.common.extensibility.jpa.JPAPropertiesPersistenceUnitPostProcessor">
 *                    <property name="persistenceUnitProperties">
 *                        <map>
 *                            <!-- Notice that the value will be replaced by property substitution from an environment
 *                                 specific file. Also note that the Persistence Unit name is prepended to the key and value to allow for configuration of
 *                                 multiple Persistence Units. This needs to be keyed this way in the properties file. The prepended persistence 
 *                                 unit name will be removed from the property name when it is added to the Persistence Unit. -->
 *                            <entry key="blPU.hibernate.dialect" value="${blPU.hibernate.dialect}"/>
 *                        </map>
 *                    </property>
 *                </bean>
 *            </list>
 *        </property>
 *    </bean>
 *
 */
public class JPAPropertiesPersistenceUnitPostProcessor implements org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor {

    private static final Log LOG = LogFactory.getLog(JPAPropertiesPersistenceUnitPostProcessor.class);
    
    protected Map<String, String> persistenceUnitProperties = new HashMap<>();
    protected Map<String, String> overrideProperties = new HashMap<>();

    @Resource(name="blCompositeAutoImportSql")
    protected CompositeAutoImportSql compositeAutoImportSql;

    @Value("${blPU.hibernate.hbm2ddl.auto}")
    protected String blPUHibernateHbm2ddlAuto;
    @Value("${blPU.hibernate.dialect}")
    protected String blPUHibernateDialect;
    @Value("${blPU.hibernate.show_sql}")
    protected String blPUHibernateShow_sql;
    @Value("${blPU.hibernate.cache.use_second_level_cache}")
    protected String blPUHibernateCacheUse_second_level_cache;
    @Value("${blPU.hibernate.cache.use_query_cache}")
    protected String blPUHibernateCacheUse_query_cache;
    @Value("${blPU.hibernate.hbm2ddl.import_files}")
    protected String blPUHibernateHbm2ddlImport_files;
    @Value("${blPU.hibernate.hbm2ddl.import_files_sql_extractor}")
    protected String blPUHibernateHbm2ddlImport_files_sql_extractor;

    @Value("${blCMSStorage.hibernate.hbm2ddl.auto}")
    protected String blCMSStorageHibernateHbm2ddlAuto;
    @Value("${blCMSStorage.hibernate.dialect}")
    protected String blCMSStorageHibernateDialect;
    @Value("${blCMSStorage.hibernate.show_sql}")
    protected String blCMSStorageHibernateShow_sql;
    @Value("${blCMSStorage.hibernate.cache.use_second_level_cache}")
    protected String blCMSStorageHibernateCacheUse_second_level_cache;
    @Value("${blCMSStorage.hibernate.cache.use_query_cache}")
    protected String blCMSStorageHibernateCacheUse_query_cache;
    @Value("${blCMSStorage.hibernate.hbm2ddl.import_files}")
    protected String blCMSStorageHibernateHbm2ddlImport_files;
    @Value("${blCMSStorage.hibernate.hbm2ddl.import_files_sql_extractor}")
    protected String blCMSStorageHibernateHbm2ddlImport_files_sql_extractor;

    @Value("${blSecurePU.hibernate.hbm2ddl.auto}")
    protected String blSecurePUHibernateHbm2ddlAuto;
    @Value("${blSecurePU.hibernate.dialect}")
    protected String blSecurePUHibernateDialect;
    @Value("${blSecurePU.hibernate.show_sql}")
    protected String blSecurePUHibernateShow_sql;
    @Value("${blSecurePU.hibernate.cache.use_second_level_cache}")
    protected String blSecurePUHibernateCacheUse_second_level_cache;
    @Value("${blSecurePU.hibernate.cache.use_query_cache}")
    protected String blSecurePUHibernateCacheUse_query_cache;
    @Value("${blSecurePU.hibernate.hbm2ddl.import_files}")
    protected String blSecurePUHibernateHbm2ddlImport_files;
    @Value("${blSecurePU.hibernate.hbm2ddl.import_files_sql_extractor}")
    protected String blSecurePUHibernateHbm2ddlImport_files_sql_extractor;

    @PostConstruct
    public void populatePresetProperties() {
        if (!blPUHibernateHbm2ddlAuto.startsWith("${")) persistenceUnitProperties.put("blPU.hibernate.hbm2ddl.auto", blPUHibernateHbm2ddlAuto);
        if (!blPUHibernateDialect.startsWith("${")) persistenceUnitProperties.put("blPU.hibernate.dialect", blPUHibernateDialect);
        if (!blPUHibernateShow_sql.startsWith("${")) persistenceUnitProperties.put("blPU.hibernate.show_sql", blPUHibernateShow_sql);
        if (!blPUHibernateCacheUse_second_level_cache.startsWith("${")) persistenceUnitProperties.put("blPU.hibernate.cache.use_second_level_cache", blPUHibernateCacheUse_second_level_cache);
        if (!blPUHibernateCacheUse_query_cache.startsWith("${")) persistenceUnitProperties.put("blPU.hibernate.cache.use_query_cache", blPUHibernateCacheUse_query_cache);
        if (!blPUHibernateHbm2ddlImport_files.startsWith("${") && !"null".equals(blPUHibernateHbm2ddlImport_files)) {
            persistenceUnitProperties.put("blPU.hibernate.hbm2ddl.import_files", blPUHibernateHbm2ddlImport_files);
        } else {
            String autoImportSql = compositeAutoImportSql.compileSqlFilePathList("blPU");
            if (LOG.isInfoEnabled()) {
                Map<String, List<AutoImportSql>> loggingMap = compositeAutoImportSql.constructAutoImportSqlMapForPU("blPU");
                LOG.info("Auto-importing the following SQL files in order:");
                for (String stage : loggingMap.keySet()) {
                    LOG.info(stage);
                    for (AutoImportSql sqlFile : loggingMap.get(stage)) {
                        LOG.info("[order:" + sqlFile.getOrder() + "] " + sqlFile.getSqlFilePath());
                    }
                }
            }

            if (!StringUtils.isEmpty(autoImportSql)) {
                persistenceUnitProperties.put("blPU.hibernate.hbm2ddl.import_files", autoImportSql);
            }
        }
        if (!blPUHibernateHbm2ddlImport_files_sql_extractor.startsWith("${")) persistenceUnitProperties.put("blPU.hibernate.hbm2ddl.import_files_sql_extractor", blPUHibernateHbm2ddlImport_files_sql_extractor);

        if (!blCMSStorageHibernateHbm2ddlAuto.startsWith("${")) persistenceUnitProperties.put("blCMSStorage.hibernate.hbm2ddl.auto", blCMSStorageHibernateHbm2ddlAuto);
        if (!blCMSStorageHibernateDialect.startsWith("${")) persistenceUnitProperties.put("blCMSStorage.hibernate.dialect", blCMSStorageHibernateDialect);
        if (!blCMSStorageHibernateShow_sql.startsWith("${")) persistenceUnitProperties.put("blCMSStorage.hibernate.show_sql", blCMSStorageHibernateShow_sql);
        if (!blCMSStorageHibernateCacheUse_second_level_cache.startsWith("${")) persistenceUnitProperties.put("blCMSStorage.hibernate.cache.use_second_level_cache", blCMSStorageHibernateCacheUse_second_level_cache);
        if (!blCMSStorageHibernateCacheUse_query_cache.startsWith("${")) persistenceUnitProperties.put("blCMSStorage.hibernate.cache.use_query_cache", blCMSStorageHibernateCacheUse_query_cache);
        if (!blCMSStorageHibernateHbm2ddlImport_files.startsWith("${")) persistenceUnitProperties.put("blCMSStorage.hibernate.hbm2ddl.import_files", blCMSStorageHibernateHbm2ddlImport_files);
        if (!blCMSStorageHibernateHbm2ddlImport_files_sql_extractor.startsWith("${")) persistenceUnitProperties.put("blCMSStorage.hibernate.hbm2ddl.import_files_sql_extractor", blCMSStorageHibernateHbm2ddlImport_files_sql_extractor);

        if (!blSecurePUHibernateHbm2ddlAuto.startsWith("${")) persistenceUnitProperties.put("blSecurePU.hibernate.hbm2ddl.auto", blSecurePUHibernateHbm2ddlAuto);
        if (!blSecurePUHibernateDialect.startsWith("${")) persistenceUnitProperties.put("blSecurePU.hibernate.dialect", blSecurePUHibernateDialect);
        if (!blSecurePUHibernateShow_sql.startsWith("${")) persistenceUnitProperties.put("blSecurePU.hibernate.show_sql", blSecurePUHibernateShow_sql);
        if (!blSecurePUHibernateCacheUse_second_level_cache.startsWith("${")) persistenceUnitProperties.put("blSecurePU.hibernate.cache.use_second_level_cache", blSecurePUHibernateCacheUse_second_level_cache);
        if (!blSecurePUHibernateCacheUse_query_cache.startsWith("${")) persistenceUnitProperties.put("blSecurePU.hibernate.cache.use_query_cache", blSecurePUHibernateCacheUse_query_cache);
        if (!blSecurePUHibernateHbm2ddlImport_files.startsWith("${")) persistenceUnitProperties.put("blSecurePU.hibernate.hbm2ddl.import_files", blSecurePUHibernateHbm2ddlImport_files);
        if (!blSecurePUHibernateHbm2ddlImport_files_sql_extractor.startsWith("${")) persistenceUnitProperties.put("blSecurePU.hibernate.hbm2ddl.import_files_sql_extractor", blSecurePUHibernateHbm2ddlImport_files_sql_extractor);

        persistenceUnitProperties.putAll(overrideProperties);
    }
    
    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        if (persistenceUnitProperties != null) {
            String puName = pui.getPersistenceUnitName() + ".";
            Set<String> keys = persistenceUnitProperties.keySet();
            Properties props = pui.getProperties();

            for (String key : keys) {
                if (key.startsWith(puName)){
                    String value = persistenceUnitProperties.get(key);
                    String newKey = key.substring(puName.length());
                    if ("null".equalsIgnoreCase(value)){
                        props.remove(newKey);
                    } else if (value != null && ! "".equals(value)) {
                        props.put(newKey, value);
                    }
                }
            }
            pui.setProperties(props);
        }
    }
    
    public void setPersistenceUnitProperties(Map<String, String> properties) {
        this.overrideProperties = properties;
    }
}
