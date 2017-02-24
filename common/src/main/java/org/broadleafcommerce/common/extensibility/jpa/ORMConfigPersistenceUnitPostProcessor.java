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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for compiling the set of {@link ORMConfigDto} and adding the configuration items to a persistence unit.
 * 
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link ORMConfigDto}
 */
public class ORMConfigPersistenceUnitPostProcessor implements org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor {

    @Autowired(required = false)
    protected List<ORMConfigDto> configs = new ArrayList<>();

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        String puName = pui.getPersistenceUnitName();
        for (ORMConfigDto config : configs) {
            if (puName.equals(config.getPuName())) {
                if (CollectionUtils.isNotEmpty(config.getClassNames())) {
                    pui.getManagedClassNames().addAll(config.getClassNames());
                }
                if (CollectionUtils.isNotEmpty(config.getMappingFiles())) {
                    pui.getMappingFileNames().addAll(config.getMappingFiles());
                }
            }
        }
    }
}
