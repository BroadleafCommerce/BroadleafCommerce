package org.broadleafcommerce.extensibility.jpa;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

/**
 * Merges properties from multiple persistence.xml files into a single PersistenceUnit.
 * For the post-processor to work properly all persistence.xml files must specify the same
 * persistence unit name.
 * @author jjacobs
 */
public class MergePersistenceUnitManager extends DefaultPersistenceUnitManager {
	
    @Override
    protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo newPU) {
        super.postProcessPersistenceUnitInfo(newPU);
        final URL persistenceUnitRootUrl = newPU.getPersistenceUnitRootUrl();
        newPU.addJarFileUrl(persistenceUnitRootUrl);
        final String persistenceUnitName = newPU.getPersistenceUnitName();
        final MutablePersistenceUnitInfo oldPU = getPersistenceUnitInfo(persistenceUnitName);
        if (oldPU != null) {
            List<URL> urls = oldPU.getJarFileUrls();
            for (URL url : urls)
                newPU.addJarFileUrl(url);
            List<String> managedClassNames = oldPU.getManagedClassNames();
            for (String managedClassName : managedClassNames)
                newPU.addManagedClassName(managedClassName);
            List<String> mappingFileNames = oldPU.getMappingFileNames();
            for (String mappingFileName : mappingFileNames)
                newPU.addMappingFileName(mappingFileName);
            Properties oldProperties = oldPU.getProperties();
            Properties newProperties = newPU.getProperties();
            newProperties.putAll(oldProperties);
            newPU.setProperties(newProperties);
        }
    }
    
}
