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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public interface PersistenceModule {

    public boolean isCompatible(OperationType operationType);
    
    public Entity add(PersistencePackage persistencePackage) throws ServiceException;
    
    public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException;
    
    public void extractProperties(Class<?>[] inheritanceLine, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties);
    
    public Entity update(PersistencePackage persistencePackage) throws ServiceException;
    
    public void remove(PersistencePackage persistencePackage) throws ServiceException;
    
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;
    
    public void setPersistenceManager(PersistenceManager persistenceManager);
    
}
