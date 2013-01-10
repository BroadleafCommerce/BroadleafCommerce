/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.extensibility.jpa;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.persistence.spi.PersistenceUnitInfo;

import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

/**
 * Merges jars, class names and mapping file names from several persistence.xml files. The
 * MergePersistenceUnitManager will continue to keep track of individual persistence unit
 * names (including individual data sources). When a specific PersistenceUnitInfo is requested
 * by unit name, the appropriate PersistenceUnitInfo is returned with modified jar files
 * urls, class names and mapping file names that include the comprehensive collection of these
 * values from all persistence.xml files. Note, only persistence units belonging to the
 * validPersistenceUnitNames list are included in the merge.
 * 
 * 
 * @author jfischer, jjacobs
 */
public class MergePersistenceUnitManager extends DefaultPersistenceUnitManager {

    private HashMap<String, MutablePersistenceUnitInfo> mergedPus = new HashMap<String, MutablePersistenceUnitInfo>();

    protected MutablePersistenceUnitInfo getMergedUnit(String persistenceUnitName, MutablePersistenceUnitInfo newPU) {
        if (!mergedPus.containsKey(persistenceUnitName)) {
            mergedPus.put(persistenceUnitName, newPU);
        }
        return mergedPus.get(persistenceUnitName);
    }

    @Override
    protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo newPU) {
        super.postProcessPersistenceUnitInfo(newPU);
        newPU.addJarFileUrl(newPU.getPersistenceUnitRootUrl());
        String persistenceUnitName = newPU.getPersistenceUnitName();
        MutablePersistenceUnitInfo temp = getMergedUnit(persistenceUnitName, newPU);
        final URL persistenceUnitRootUrl = newPU.getPersistenceUnitRootUrl();
        temp.setPersistenceUnitRootUrl(persistenceUnitRootUrl);
        List<String> managedClassNames = newPU.getManagedClassNames();
        for (String managedClassName : managedClassNames){
            if (!temp.getManagedClassNames().contains(managedClassName)) {
                temp.addManagedClassName(managedClassName);
            }
        }
        List<String> mappingFileNames = newPU.getMappingFileNames();
        for (String mappingFileName : mappingFileNames) {
            if (!temp.getMappingFileNames().contains(mappingFileName)) {
                temp.addMappingFileName(mappingFileName);
            }
        }
        temp.setExcludeUnlistedClasses(newPU.excludeUnlistedClasses());
        for (URL url : newPU.getJarFileUrls()) {
            if (!temp.getJarFileUrls().contains(url)) {
                temp.addJarFileUrl(url);
            }
        }
        temp.setJtaDataSource(newPU.getJtaDataSource());
        temp.setNonJtaDataSource(newPU.getNonJtaDataSource());
        if (temp.getProperties() == null) {
            temp.setProperties(newPU.getProperties());
        } else {
            Properties props = newPU.getProperties();
            if (props != null) {
                for (Object key : props.keySet()) {
                    temp.getProperties().setProperty((String) key, props.getProperty((String) key)); 
                }
            }
        }
        temp.setTransactionType(newPU.getTransactionType());
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#obtainPersistenceUnitInfo(java.lang.String)
     */
    @Override
    public PersistenceUnitInfo obtainPersistenceUnitInfo(String persistenceUnitName) {
        return mergedPus.get(persistenceUnitName);
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#obtainDefaultPersistenceUnitInfo()
     */
    @Override
    public PersistenceUnitInfo obtainDefaultPersistenceUnitInfo() {
        throw new IllegalStateException("Default Persistence Unit is not supported. The persistence unit name must be specified at the entity manager factory.");
    }

}
