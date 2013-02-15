
package org.broadleafcommerce.openadmin.server.factory;

import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;

/**
 * Responsible for creating different persistence packages for different operations
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface PersistencePackageFactory {

    /**
     * Creates a persistence package for the given request
     * 
     * @param request
     * @return the persistence package
     */
    public PersistencePackage create(PersistencePackageRequest request);

}