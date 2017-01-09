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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Responsible for determining if an entity has been conditionally enabled. The primary utility of this class is to allow
 * conditional inclusion of additional entities important for enhancements or bug fixes. Since this behavior requires
 * explicit action by an implementation's codebase, fixes that require schema changes can safely be introduced in a patch release stream.
 * </p>
 * Setup inside a Broadleaf Commerce module is generally performed in a manner similar to this example:
 * {@code
    <bean id="blAuditConditionalORMConfig" class="org.broadleafcommerce.common.extensibility.jpa.ConditionalORMConfigDto">
        <property name="puName" value="blPU"/>
        <property name="conditionalProperty" value="enable.workflow.audit.report.request"/>
    </bean>

    <bean id="blCommonConditionalEntities" class="org.springframework.beans.factory.config.MapFactoryBean">
        <property name="sourceMap">
            <map>
                <entry key="com.broadleafcommerce.enterprise.workflow.domain.AuditLogRequestImpl" value-ref="blAuditConditionalORMConfig"/>
                <entry key="com.broadleafcommerce.enterprise.workflow.domain.AuditLogNodeRequestImpl" value-ref="blAuditConditionalORMConfig"/>
                <entry key="com.broadleafcommerce.enterprise.workflow.domain.AuditReportRequestImpl" value-ref="blAuditConditionalORMConfig"/>
            </map>
        </property>
    </bean>

    <bean class="org.broadleafcommerce.common.extensibility.context.merge.EarlyStageMergeBeanPostProcessor">
        <property name="collectionRef" value="blCommonConditionalEntities"/>
        <property name="targetRef" value="blConditionalEntities"/>
    </bean>
 * }
 * The goal is to add configuration for one or more entities and then add that configuration to the "blCommonConditionalEntities" map in
 * Spring. The activity of this configuration will remain dormant until the "conditionalProperty" is defined and set to true in the implementation's
 * Spring property files (or override property file). At that point, the entities will be introduced to Hibernate for normal
 * inclusion. This has a similar effect to declaring a <class></class> element in a persistence.xml file. Note, the "blCommonConditionalOrmFiles"
 * collection may be targeted as well, allowing you to conditionally includes ORM mapping files as well. This has a similar
 * effect to declaring a <mapping-file></mapping-file> element in a persistence.xml file.
 *
 * @author Jeff Fischer
 */
public class ConditionalORMConfigPersistenceUnitPostProcessor implements org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor, BeanFactoryAware {

    @Resource(name="blConditionalEntities")
    protected Map<String, ORMConfigDto> entities;

    protected Map<String, ORMConfigDto> enabledEntities = new HashMap<>();

    @Resource(name="blConditionalOrmFiles")
    protected Map<String, ORMConfigDto> ormFiles;

    protected Map<String, ORMConfigDto> enabledOrmFiles = new HashMap<>();

    protected ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @PostConstruct
    public void init() {
        for (Map.Entry<String, ORMConfigDto> entry : entities.entrySet()) {
            if (isEnabled(entry.getValue())) {
                enabledEntities.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, ORMConfigDto> entry : ormFiles.entrySet()) {
            if (isEnabled(entry.getValue())) {
                enabledOrmFiles.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        String puName = pui.getPersistenceUnitName();
        for (Map.Entry<String, ORMConfigDto> entry : enabledEntities.entrySet()) {
            if (puName.equals(entry.getValue().getPuName())) {
                pui.getManagedClassNames().add(entry.getKey());
            }
        }
        for (Map.Entry<String, ORMConfigDto> entry : enabledOrmFiles.entrySet()) {
            if (puName.equals(entry.getValue().getPuName())) {
                pui.getMappingFileNames().add(entry.getKey());
            }
        }
    }

    protected Boolean isEnabled(ORMConfigDto configDto) {
        if (configDto.getClass().isAssignableFrom(ConditionalORMConfigDto.class)) {
            ConditionalORMConfigDto conditionalConfigDto = (ConditionalORMConfigDto) configDto;
            if (conditionalConfigDto.getConditionalProperty() != null) {
                try {
                    String value = beanFactory.resolveEmbeddedValue("${" + configDto + ":false}");
                    return Boolean.parseBoolean(value);
                } catch (Exception e) {
                    return false;
                }
            } else if (conditionalConfigDto.getConditionalClassName() != null) {
                try {
                    Class.forName(conditionalConfigDto.getConditionalClassName());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return true;
    }
}
