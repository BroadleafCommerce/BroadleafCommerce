
package org.broadleafcommerce.openadmin.server.factory;

import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;

/**
 * Responsible for creating different persistence packages for different operations
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface PersistencePackageFactory {

    /**
     * Creates a persistence package for standard operations.
     * 
     * @param className
     * @param customCriteria
     * @param foreignKeys
     * @param configurationKey
     * @return the persistence package
     */
    public PersistencePackage standard(String className, String[] customCriteria, ForeignKey[] foreignKeys,
            String configurationKey);

    /**
     * Creates a persistence package for operations on adorned target collections
     * 
     * @param className
     * @param customCriteria
     * @param adornedList
     * @return the persistence package
     */
    public PersistencePackage adornedTarget(String className, String[] customCriteria, AdornedTargetList adornedList);

}