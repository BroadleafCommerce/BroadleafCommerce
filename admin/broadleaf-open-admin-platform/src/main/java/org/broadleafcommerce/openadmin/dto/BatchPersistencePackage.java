/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Jeff Fischer
 */
public class BatchPersistencePackage implements Serializable {

    protected PersistencePackage[] persistencePackages;

    public PersistencePackage[] getPersistencePackages() {
        return persistencePackages;
    }

    public void setPersistencePackages(PersistencePackage[] persistencePackages) {
        this.persistencePackages = persistencePackages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        BatchPersistencePackage that = (BatchPersistencePackage) o;

        if (!Arrays.equals(persistencePackages, that.persistencePackages)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return persistencePackages != null ? Arrays.hashCode(persistencePackages) : 0;
    }
}
