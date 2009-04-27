package org.broadleafcommerce.extensibility.jpa;

import java.net.URL;
import java.util.List;

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

    private MutablePersistenceUnitInfo masterPU = new MutablePersistenceUnitInfo();
    private List<String> validPersistenceUnitNames;

    @Override
    protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo newPU) {
        super.postProcessPersistenceUnitInfo(newPU);
        if (validPersistenceUnitNames == null) {
            throw new IllegalArgumentException("validPersistenceUnitNames must be set");
        }
        String persistenceUnitName = newPU.getPersistenceUnitName();
        if (validPersistenceUnitNames.contains(persistenceUnitName)) {
            final URL persistenceUnitRootUrl = newPU.getPersistenceUnitRootUrl();
            if (!masterPU.getJarFileUrls().contains(persistenceUnitRootUrl)){
                masterPU.addJarFileUrl(persistenceUnitRootUrl);
            }
            List<URL> urls = newPU.getJarFileUrls();
            for (URL url : urls){
                if (!masterPU.getJarFileUrls().contains(url)){
                    masterPU.addJarFileUrl(url);
                }
            }
            List<String> managedClassNames = newPU.getManagedClassNames();
            for (String managedClassName : managedClassNames){
                if (!masterPU.getManagedClassNames().contains(managedClassName)) {
                    masterPU.addManagedClassName(managedClassName);
                }
            }
            List<String> mappingFileNames = newPU.getMappingFileNames();
            for (String mappingFileName : mappingFileNames) {
                if (!masterPU.getMappingFileNames().contains(mappingFileName)) {
                    masterPU.addMappingFileName(mappingFileName);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#obtainPersistenceUnitInfo(java.lang.String)
     */
    @Override
    public PersistenceUnitInfo obtainPersistenceUnitInfo(String persistenceUnitName) {
        MutablePersistenceUnitInfo pui = getPersistenceUnitInfo(persistenceUnitName);
        if (pui == null) {
            throw new IllegalArgumentException("No persistence unit with name '"
                    + persistenceUnitName + "' found");
        }

        List<URL> jarUrls = pui.getJarFileUrls();
        jarUrls.clear();
        List<URL> urls = masterPU.getJarFileUrls();
        for (URL url : urls) {
            jarUrls.add(url);
        }
        List<String> classNames = pui.getManagedClassNames();
        classNames.clear();
        List<String> managedClassNames = masterPU.getManagedClassNames();
        for (String managedClassName : managedClassNames) {
            classNames.add(managedClassName);
        }
        List<String> mappingNames = pui.getMappingFileNames();
        mappingNames.clear();
        List<String> mappingFileNames = masterPU.getMappingFileNames();
        for (String mappingFileName : mappingFileNames) {
            mappingNames.add(mappingFileName);
        }

        return pui;
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#obtainDefaultPersistenceUnitInfo()
     */
    @Override
    public PersistenceUnitInfo obtainDefaultPersistenceUnitInfo() {
        throw new IllegalStateException("Default Persistence Unit is not supported. The persistence unit name must be specified at the entity manager factory.");
    }

    /**
     * @return the validPersistenceUnitNames
     */
    public List<String> getValidPersistenceUnitNames() {
        return validPersistenceUnitNames;
    }

    /**
     * @param validPersistenceUnitNames the validPersistenceUnitNames to set
     */
    public void setValidPersistenceUnitNames(List<String> validPersistenceUnitNames) {
        this.validPersistenceUnitNames = validPersistenceUnitNames;
    }

}
